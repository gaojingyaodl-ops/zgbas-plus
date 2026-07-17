package com.spt.bas.server.service.impl;

import com.spt.auth.sdk.entity.SysUserSdk;
import com.spt.auth.sdk.open.IAuthOpenFacade;
import com.spt.bas.client.constant.ApplySource;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.ApplyDeposit;
import com.spt.bas.client.entity.BsCompany;
import com.spt.bas.client.entity.BsCompanyQuota;
import com.spt.bas.purchase.wx.client.entity.UserDetail;
import com.spt.bas.purchase.wx.client.remote.IWxUserDetailClient;
import com.spt.bas.server.dao.ApplyDepositDao;
import com.spt.bas.server.service.IApplyDepositService;
import com.spt.bas.server.service.IBsCompanyQuotaService;
import com.spt.bas.server.service.IBsCompanyService;
import com.spt.pm.dao.PmApproveContentsDao;
import com.spt.pm.entity.PmApprove;
import com.spt.pm.entity.PmApproveContents;
import com.spt.pm.entity.PmApproveStep;
import com.spt.pm.entity.PmProcess;
import com.spt.pm.inter.IPmApproveListener;
import com.spt.pm.service.IPmApproveService;
import com.spt.pm.service.IPmProcessService;
import com.spt.pm.vo.PmApproveCurrVo;
import com.spt.pm.vo.PmApproveSaveVo;
import com.spt.pm.vo.PmApproveWithdrawVo;
import com.spt.pm.vo.PmProcessSearchVo;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.core.json.JsonUtil;
import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.service.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 *     入金验证审批
 * </p>
 *
 * @Author: shengong
 * @Date: Created in 2020-10-10 14:15
 */
@Component("applyDepositService")
@Transactional(readOnly = true)
public class ApplyDepositServiceImpl extends BaseService<ApplyDeposit> implements IApplyDepositService, IPmApproveListener {

    @Autowired
    private ApplyDepositDao applyDepositDao;
    @Autowired
    private IBsCompanyService bsCompanyService;
    @Autowired
    private IWxUserDetailClient userDetailDao;
    @Autowired
    private IPmProcessService pmProcessService;
    @Autowired
    private IAuthOpenFacade authOpenFacade;
    @Autowired
    private IPmApproveService pmApproveService;
    @Autowired
    private PmApproveContentsDao pmApproveContentsDao;
    @Autowired
    private IBsCompanyQuotaService bsCompanyQuotaService;

    /**
     * 发起审批
     *
     * @param approve
     */
    @Override
    public void doStepIn(PmApprove approve) throws ApplicationException {

    }

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
            ApplyDeposit entity = JsonUtil.json2Object(ApplyDeposit.class, contents);
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
            userDetail.setDepositStatus(BasConstants.APPLY_STATUS_COMPLETE);
            userDetailDao.save(userDetail);

//            // 判断入金测试是否完成 且准入 如果完成发起额度审批
//            if (BasConstants.APPLY_STATUS_COMPLETE.equals(userDetail.getEntrustApplyStatus())
//                && BasConstants.APPLY_STATUS_COMPLETE.equals(userDetail.getApplyIouStatus())) {
//                startQuota(entity, userDetail);
//            }
        }
    }

    // 发起额度审批
    private void startQuota(ApplyDeposit entity,UserDetail userDetail) throws ApplicationException {
        // 发起额度审批
        BsCompanyQuota bsCompanyQuota = new BsCompanyQuota();
        bsCompanyQuota.setCompanyId(entity.getCompanyId());
        bsCompanyQuota.setEnterpriseId(BasConstants.ZG_ENTERPRISE_ID);
        bsCompanyQuota.setTotalCreditAmount(userDetail.getTotalCreditAmount());
        bsCompanyQuota.setTotalFuturesAmount(userDetail.getTotalFuturesAmount());
        bsCompanyQuota.setTotalSpotAmount(userDetail.getTotalSpotAmount());
        bsCompanyQuota.setApproveId(0L);
        bsCompanyQuota.setCompanyName(entity.getCompanyName());
        bsCompanyQuota.setWxUserId(userDetail.getUserId());
        bsCompanyQuota.setApplySource(ApplySource.PURCHASE.getCode());
        bsCompanyQuotaService.startFlow(JsonUtil.obj2Json(bsCompanyQuota), entity.getCompanyId());
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
        ApplyDeposit entity = JsonUtil.json2Object(ApplyDeposit.class, contents);
        UserDetail userDetail = userDetailDao.findByUserId(entity.getWxUserId());
        userDetail.setIsBind(true);
        userDetail.setDepositStatus(BasConstants.APPLY_STATUS_REJECT);
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
    public BaseDao<ApplyDeposit> getBaseDao() {
        return applyDepositDao;
    }


    @Override
    public void startFlow(String bizEntityJson, Long companyId) throws ApplicationException {
        PmApproveSaveVo startVo = new PmApproveSaveVo();
        startVo.setMode(BasConstants.APPROVE_STATUS_A);
        startVo.setStatus(BasConstants.APPROVE_STATUS_A);
        startVo.setEnterpriseId(BasConstants.ZG_ENTERPRISE_ID);
        BsCompany company = bsCompanyService.getEntity(companyId);
        PmProcessSearchVo searchVo = new PmProcessSearchVo();
        searchVo.setEnterpriseId(BasConstants.ZG_ENTERPRISE_ID);
        searchVo.setProcessCode("APPLY_DEPOSIT");
        PmProcess process = pmProcessService.findByProcessCode(searchVo);
        SysUserSdk userById = authOpenFacade.findUserById(company.getMatchUserId());
        startVo.setUserId(userById.getUserId());
        startVo.setUserName(userById.getNickName());
        startVo.setProcessId(process.getId());
        startVo.setApproveId(0L);
        startVo.setBizEntityJson(bizEntityJson);
        pmApproveService.startFlow(startVo);
    }
}
