package com.spt.bas.server.service.impl;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.ApplyCfca;
import com.spt.bas.client.entity.ApplyWxCfca;
import com.spt.bas.client.entity.BsCompany;
import com.spt.bas.purchase.wx.client.constant.SaveInfoType;
import com.spt.bas.purchase.wx.client.entity.SaveInfo;
import com.spt.bas.purchase.wx.client.entity.UserDetail;
import com.spt.bas.purchase.wx.client.remote.ISaveTempClient;
import com.spt.bas.purchase.wx.client.remote.IWxUserDetailClient;
import com.spt.bas.purchase.wx.client.vo.CompanyBaseInfoVo;
import com.spt.bas.server.annotation.ServerTransactional;
import com.spt.bas.server.dao.ApplyWxCfcaDao;
import com.spt.bas.server.dao.BsCompanyDao;
import com.spt.bas.server.service.IApplyWxCfcaService;
import com.spt.bas.server.service.IBsCompanyService;
import com.spt.pm.dao.PmApproveContentsDao;
import com.spt.pm.entity.PmApprove;
import com.spt.pm.entity.PmApproveContents;
import com.spt.pm.entity.PmApproveStep;
import com.spt.pm.entity.PmProcess;
import com.spt.pm.inter.IPmApproveListener;
import com.spt.pm.inter.IPmEntity;
import com.spt.pm.inter.IPmService;
import com.spt.pm.vo.PmApproveCurrVo;
import com.spt.pm.vo.PmApproveWithdrawVo;
import com.spt.sign.client.entity.EnterpriseAccount;
import com.spt.sign.client.remote.IEnterpriseAccountClient;
import com.spt.tools.core.bean.RespVo;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.core.json.JsonUtil;
import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.service.BaseService;
import org.apache.tomcat.util.threads.ThreadPoolExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;


@Component("applyWxCfcaService")
@Transactional(readOnly = true)
public class ApplyWxCfcaServiceImpl extends BaseService<ApplyWxCfca> implements IApplyWxCfcaService, IPmService, IPmApproveListener {
    private ThreadFactory namedThreadFactory = new ThreadFactoryBuilder().setNameFormat("thread-communication-wxcfca-runner-%d").build();
    private ExecutorService executorService = new ThreadPoolExecutor(10, 20, 200L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(), namedThreadFactory);
    @Autowired
    private IEnterpriseAccountClient enterpriseAccountClient;
    @Autowired
    private ApplyWxCfcaDao applyWxCfcaDao;
    @Autowired
    private PmApproveContentsDao pmApproveContentsDao;
    @Autowired
    private ISaveTempClient saveTempClient;
    @Autowired
    private IWxUserDetailClient userDetailDao;
    @Autowired
    private IBsCompanyService companyService;
    @Autowired
    private BsCompanyDao bsCompanyDao;
    @Override
    public BaseDao<ApplyWxCfca> getBaseDao() {
        return applyWxCfcaDao;
    }

    @Override
    public IPmEntity saveEntity(IPmEntity pmEntity) throws ApplicationException {
        return null;
    }
    @Override
    public String getSubject(IPmEntity pmEntity, PmProcess process) {
        return null;
    }

    @Override
    public void doStepFlow(PmApprove approve, PmApproveStep nextStep) throws ApplicationException {
        if (approve.getStatus().equals(BasConstants.APPROVE_STATUS_D)) {
            PmApproveContents pmApproveContents = pmApproveContentsDao.findOne(approve.getBizId());
            String contents = pmApproveContents.getContents();
            ApplyWxCfca applyWxCfca = JsonUtil.json2Object(ApplyWxCfca.class, contents);
             List<ApplyWxCfca> byCompanyId = applyWxCfcaDao.findByCompanyId(applyWxCfca.getCompanyId());
            applyWxCfcaDao.updateStatus(byCompanyId.get(0).getId(),BasConstants.APPROVE_STATUS_D,approve.getId(),approve.getCreateUserName());
            // cfca通过后更新企业信息字段
            BsCompany company = companyService.getEntity(applyWxCfca.getCompanyId());
            company.setOpenCfcaFlg(true);
            companyService.save(company);
            // 修改userDetails状态
            UserDetail userDetail = userDetailDao.findByUserId(applyWxCfca.getWxUserId());
            userDetail.setIsBind(true);
            userDetail.setCfcaApprovedStatus(BasConstants.APPLY_STATUS_COMPLETE);

            userDetail.setCompanyApplyStatus(BasConstants.APPLY_STATUS_COMPLETE);
            userDetailDao.save(userDetail);
            // 审核完成后保存临时信息
            saveTempClient.commitTempInfo(SaveInfoType.BASE_INFO.getType(), userDetail.getUserId());
            // 同步临时保存信息到company表中
            syncBaseInfo(userDetail);

            //推送企业开户信息至签章系统
            pushEnterpriseAccountToSign(applyWxCfca);
        }
    }

    @Override
    @ServerTransactional
    public void doWithdraw(PmApproveWithdrawVo vo) throws ApplicationException {
        PmApproveContents pmApproveContents = pmApproveContentsDao.findOne(vo.getBizId());
        if (Objects.nonNull(pmApproveContents)){
            ApplyCfca entity = JsonUtil.json2Object(ApplyCfca.class, pmApproveContents.getContents());
            // 提交申请后 隐藏"协助申请线上化"按钮
            companyService.updateOnLineApplyFlg(entity.getCompanyId(), true);
        }
    }

    /**
     * 同步临时保存信息到company表中
     * @param userDetail
     */
    private void syncBaseInfo(UserDetail userDetail) {
        SaveInfo infoByType = saveTempClient.getInfoByType(userDetail.getUserId(), SaveInfoType.BASE_INFO.getType());
        if (infoByType.getContent() != null) {
            CompanyBaseInfoVo companyBaseInfo = JsonUtil.json2Object(CompanyBaseInfoVo.class, infoByType.getContent());
            BsCompany company = bsCompanyDao.findOne(userDetail.getCompanyId());
            // 法人
            company.setLegalRepresent(companyBaseInfo.getLegalRepresent());
            // 证件类型
            company.setCardType(companyBaseInfo.getCardType());
            // 法人身份证正面
            company.setCardFrontId(companyBaseInfo.getLegalPersonPicUrl());
            // 法人身份证反面
            company.setCardReverseId(companyBaseInfo.getLegalPersonOppositePicUrl());
            // 法人身份证号
            company.setIdentityCardNumber(companyBaseInfo.getIdentityCardNumber());
            // 联系人电话-微信小程序登录人-经办人手机号
            company.setContactPhone(companyBaseInfo.getManagerPhone());
            // 联系人邮箱--经办人邮箱
            company.setContactEmail(companyBaseInfo.getManagerEmail());
            bsCompanyDao.save(company);
        }
    }

    @Override
    @ServerTransactional
    public void doStepIn(PmApprove approve) throws ApplicationException {
        PmApproveContents pmApproveContents = pmApproveContentsDao.findOne(approve.getBizId());
        if (Objects.nonNull(pmApproveContents)){
            ApplyCfca entity = JsonUtil.json2Object(ApplyCfca.class, pmApproveContents.getContents());
            // 提交申请后 隐藏"协助申请线上化"按钮
            companyService.updateOnLineApplyFlg(entity.getCompanyId(), true);
        }
    }

    /**
     * 审批驳回
     *
     * @param approve
     * @param nextStep
     */
    @Override
    public void doStepBack(PmApproveCurrVo approve, PmApproveStep nextStep) throws ApplicationException {
        PmApproveContents pmApproveContents = pmApproveContentsDao.findOne(approve.getBizId());
        String contents = pmApproveContents.getContents();
        ApplyCfca entity = JsonUtil.json2Object(ApplyCfca.class, contents);
        // 修改userDetails状态
        UserDetail userDetail = userDetailDao.findByUserId(entity.getWxUserId());
        userDetail.setCompanyApplyStatus(BasConstants.APPLY_STATUS_REJECT);
        userDetail.setCfcaApprovedStatus(BasConstants.APPLY_STATUS_REJECT);
        userDetailDao.save(userDetail);

        // 驳回追回后 展示"协助申请线上化"按钮
        companyService.updateOnLineApplyFlg(entity.getCompanyId(), false);
    }

    /**
     * 推送企业开户信息至签章系统-执行自动开户
     * @param applyWxCfca
     */
    private void pushEnterpriseAccountToSign(ApplyWxCfca applyWxCfca) {
        if (Objects.isNull(applyWxCfca)) {
            return;
        }
        logger.info("pushEnterpriseAccountToSign enterpriseName:{}", applyWxCfca.getCompanyName());
        executorService.execute(() -> {
            EnterpriseAccount enterpriseAccount = new EnterpriseAccount();
            enterpriseAccount.setNotSendPwd("1");
            enterpriseAccount.setIdentTypeCode("8");
            enterpriseAccount.setAppCode(BasConstants.APP_CODE);
            enterpriseAccount.setEnterpriseName(applyWxCfca.getCompanyName());
            enterpriseAccount.setTransactorName(applyWxCfca.getLegalRepresent());
            enterpriseAccount.setTranIdentNo(applyWxCfca.getIdentityCardNumber());
            enterpriseAccount.setTranIdentTypeCode("0");
            enterpriseAccount.setEmail(applyWxCfca.getEmail());
            enterpriseAccount.setMobilePhone(applyWxCfca.getPhone());
            enterpriseAccount.setLandLinePhone(applyWxCfca.getPhone());
            BsCompany companyDetail = companyService.getCompanyDetail(applyWxCfca.getCompanyId());
            enterpriseAccount.setIdentNo(Objects.nonNull(companyDetail) ? companyDetail.getCompanyCreditNo() : "");
            RespVo<?> respVo = enterpriseAccountClient.receiveCompanySign(enterpriseAccount);
            logger.info("pushEnterpriseAccountToSign result:{}", JsonUtil.obj2Json(respVo));
        });
    }

}
