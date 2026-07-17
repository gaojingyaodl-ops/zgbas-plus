package com.spt.bas.server.service.impl;

import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.ApplyCompanyInfo;
import com.spt.bas.client.entity.ApplyDeposit;
import com.spt.bas.client.entity.ApplyEntrust;
import com.spt.bas.client.entity.BsCompany;
import com.spt.bas.purchase.wx.client.constant.SaveInfoType;
import com.spt.bas.purchase.wx.client.entity.SaveInfo;
import com.spt.bas.purchase.wx.client.entity.UserDetail;
import com.spt.bas.purchase.wx.client.remote.ISaveTempClient;
import com.spt.bas.purchase.wx.client.remote.IWxUserDetailClient;
import com.spt.bas.purchase.wx.client.vo.CompanyBaseInfoVo;
import com.spt.bas.server.dao.ApplyCompanyInfoDao;
import com.spt.bas.server.dao.BsCompanyDao;
import com.spt.bas.server.service.IApplyCompanyInfoService;
import com.spt.bas.server.service.IApplyDepositService;
import com.spt.bas.server.service.IApplyEntrustService;
import com.spt.pm.dao.PmApproveContentsDao;
import com.spt.pm.entity.PmApprove;
import com.spt.pm.entity.PmApproveContents;
import com.spt.pm.entity.PmApproveStep;
import com.spt.pm.inter.IPmApproveListener;
import com.spt.pm.vo.PmApproveCurrVo;
import com.spt.pm.vo.PmApproveWithdrawVo;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.core.json.JsonUtil;
import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.service.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

/**
 * <p>
 *  企业资料审核
 * </p>
 *
 * @Author: shengong
 * @Date: Created in 2020-10-10 14:31
 */
@Component("applyCompanyInfoService")
@Transactional(readOnly = true)
public class ApplyCompanyInfoServiceImpl extends BaseService<ApplyCompanyInfo> implements IApplyCompanyInfoService, IPmApproveListener {

    @Autowired
    private PmApproveContentsDao pmApproveContentsDao;
    @Autowired
    private IWxUserDetailClient userDetailDao;
    @Autowired
    private ApplyCompanyInfoDao applyCompanyInfoDao;
    @Autowired
    private ISaveTempClient saveTempClient;
    @Autowired
    private IApplyEntrustService applyEntrustService;
    @Autowired
    private IApplyDepositService applyDepositService;
    @Autowired
    private BsCompanyDao bsCompanyDao;

    /**
     * 执行审批步骤
     *
     * @param approve
     * @param nextStep
     */
    @Override
    public void doStepFlow(PmApprove approve, PmApproveStep nextStep) throws ApplicationException {
        if (approve.getStatus().equals(BasConstants.APPROVE_STATUS_D)) {
            PmApproveContents pmApproveContents = pmApproveContentsDao.findOne(approve.getBizId());
            String contents = pmApproveContents.getContents();
            ApplyCompanyInfo entity = JsonUtil.json2Object(ApplyCompanyInfo.class, contents);
            entity.setApplyUserId(approve.getCreateUserId());
            entity.setApplyUserName(approve.getCreateUserName());
            entity.setApproveId(approve.getId());
            entity.setId(0L);
            entity.setFileId(pmApproveContents.getFileId());
            logger.info("doStepFlow - " + JsonUtil.obj2Json(entity));
            save(entity);

            // 修改userDetails状态
            UserDetail userDetail = userDetailDao.findByUserId(entity.getWxUserId());
            userDetail.setIsBind(true);
            userDetail.setCompanyApplyStatus(BasConstants.APPLY_STATUS_COMPLETE);
            userDetailDao.save(userDetail);

            // 审核完成后保存临时信息
            saveTempClient.commitTempInfo(SaveInfoType.BASE_INFO.getType(), userDetail.getUserId());

            // 同步临时保存信息到company表中
            syncBaseInfo(userDetail);

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
            // 联系人电话
            company.setContactPhone(companyBaseInfo.getPhone());
            // 联系人邮箱
            company.setContactEmail(companyBaseInfo.getEmail());
            bsCompanyDao.save(company);
        }
    }

    /**
     * 发起委托授权审批
     * @param entity
     * @param userDetail
     */
    private void startEntrustFlow(ApplyCompanyInfo entity,UserDetail userDetail) throws ApplicationException {
        ApplyEntrust entrust = new ApplyEntrust();
        entrust.setApplyUserId(entity.getApplyUserId());
        entrust.setApplyUserName(entity.getApplyUserName());
        entrust.setCompanyName(entity.getCompanyName());
        entrust.setWxUserId(entity.getWxUserId());
        entrust.setEnterpriseId(BasConstants.ZG_ENTERPRISE_ID);
        entrust.setCompanyId(entity.getCompanyId());
        SaveInfo entrustInfo = saveTempClient.getEntrustInfo(entity.getWxUserId());
        try {
            Map<String, Object> stringObjectMap = JsonUtil.json2Map(entrustInfo.getContent());
            String powerOfAttorneyFileId = (String)stringObjectMap.get("powerOfAttorneyFileId");
            entrust.setFileId(powerOfAttorneyFileId);
        }catch (Exception e){
            entrust.setFileId(null);
            logger.error("获取委托授权盖章图片错误");
        }
        entrust.setApproveId(0L);
        applyEntrustService.startFlow(JsonUtil.obj2Json(entrust), userDetail.getCompanyId());
        // 修改状态为审批中
        UserDetail byUserId = userDetailDao.findByUserId(entity.getWxUserId());
        byUserId.setEntrustApplyStatus(BasConstants.APPLY_STATUS_APPLYING);
        userDetailDao.save(byUserId);
    }

    /**
     * 发起入金验证审批
     * @param entity
     * @param userDetail
     */
    private void startDepositFlow(ApplyCompanyInfo entity,UserDetail userDetail) throws ApplicationException {
        ApplyDeposit applyDeposit = new ApplyDeposit();
        applyDeposit.setTargetAmount(userDetail.getTotalDepositPrice());
        applyDeposit.setApplyUserId(entity.getApplyUserId());
        applyDeposit.setApplyUserName(entity.getApplyUserName());
        applyDeposit.setWxUserId(entity.getWxUserId());
        applyDeposit.setCompanyName(entity.getCompanyName());
        applyDeposit.setApproveId(0L);
        applyDeposit.setCompanyId(entity.getCompanyId());
        applyDepositService.startFlow(JsonUtil.obj2Json(applyDeposit), userDetail.getCompanyId());
        // 修改状态为审批中
        UserDetail byUserId = userDetailDao.findByUserId(entity.getWxUserId());
        byUserId.setDepositStatus(BasConstants.APPLY_STATUS_APPLYING);
        userDetailDao.save(byUserId);
    }

    /**
     * 审批驳回
     *
     * @param approve
     * @param nextStep
     */
    @Override
    public void doStepBack(PmApproveCurrVo approve, PmApproveStep nextStep) throws ApplicationException {
        // 如果企业资料审批未通过，则委托授权和入金测试被拒绝，对应的字段值为3
        PmApproveContents pmApproveContents = pmApproveContentsDao.findOne(approve.getBizId());
        String contents = pmApproveContents.getContents();
        ApplyCompanyInfo entity = JsonUtil.json2Object(ApplyCompanyInfo.class, contents);
        UserDetail userDetail = userDetailDao.getEntity(entity.getWxUserId());
        userDetail.setIsBind(true);
        userDetail.setCompanyApplyStatus("3");
        userDetail.setEntrustApplyStatus("3");
        userDetail.setDepositStatus("3");
        userDetailDao.save(userDetail);
    }

    /**
     * 审批撤回
     *
     * @param vo
     */
    @Override
    public void doWithdraw(PmApproveWithdrawVo vo) throws ApplicationException {

    }

    @Override
    public BaseDao<ApplyCompanyInfo> getBaseDao() {
        return applyCompanyInfoDao;
    }



}
