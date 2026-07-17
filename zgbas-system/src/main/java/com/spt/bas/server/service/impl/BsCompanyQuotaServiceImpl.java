package com.spt.bas.server.service.impl;

import com.spt.auth.sdk.entity.SysUserSdk;
import com.spt.auth.sdk.open.IAuthOpenFacade;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.BsCompany;
import com.spt.bas.client.entity.BsCompanyQuota;
import com.spt.bas.purchase.wx.client.entity.UserDetail;
import com.spt.bas.purchase.wx.client.remote.IWxUserDetailClient;
import com.spt.bas.server.dao.BsCompanyQuotaDao;
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
import com.spt.tools.core.date.DateOperator;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.core.json.JsonUtil;
import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.service.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 额度浮动审批
 */
@Component("bsCompanyQuotaService")
@Transactional(readOnly = true)
public class BsCompanyQuotaServiceImpl extends BaseService<BsCompanyQuota> implements IBsCompanyQuotaService, IPmApproveListener {

    private static final Long ZG_ENTERPRISE_ID = BasConstants.ZG_ENTERPRISE_ID;

    @Autowired
    private BsCompanyQuotaDao bsCompanyQuotaDao;
    @Autowired
    private PmApproveContentsDao pmApproveContentsDao;
    @Autowired
    private IBsCompanyService bsCompanyService;
    @Autowired
    private IPmProcessService pmProcessService;
    @Autowired
    private IPmApproveService pmApproveService;
    @Autowired
    private IAuthOpenFacade authOpenFacade;
    @Autowired
    private IWxUserDetailClient userDetailDao;

    @Override
    public void doStepFlow(PmApprove approve, PmApproveStep nextStep) throws ApplicationException {
        if (approve.getStatus().equals(BasConstants.APPROVE_STATUS_D)) {
            PmApproveContents pmApproveContents = pmApproveContentsDao.findOne(approve.getBizId());
            String contents = pmApproveContents.getContents();
            BsCompanyQuota entity = JsonUtil.json2Object(BsCompanyQuota.class, contents);
            entity.setApplyUserId(approve.getCreateUserId());
            entity.setApplyUserName(approve.getCreateUserName());
            entity.setApproveId(approve.getId());
            entity.setId(0L);
            entity.setFileId(pmApproveContents.getFileId());
            logger.info("doStepFlow - " + JsonUtil.obj2Json(entity));
            save(entity);

            // 获取基础额度
            BsCompany bsCompany = bsCompanyService.getEntity(entity.getCompanyId());
            BigDecimal baseQuota = bsCompany.getBaseQuota();
            // oldTotalCreditAmount 历史额度
            BigDecimal oldTotalCreditAmount = bsCompany.getTotalCreditAmount();
            // 计算最终额度
            BigDecimal totalCreditAmount = evalTotalCreditAmount(entity, baseQuota);

            //完成后，修改企业状态
            bsCompany.setTotalCreditAmount(totalCreditAmount);

            BigDecimal totalTemporaryAmount = bsCompany.getTotalTemporaryAmount();

            PmProcessSearchVo searchVo = new PmProcessSearchVo();
            searchVo.setEnterpriseId(ZG_ENTERPRISE_ID);
            searchVo.setProcessCode(BasConstants.PROCESS_APPLY_QUOTA);
            PmProcess process = pmProcessService.findByProcessCode(searchVo);
            // 如果totalTemporaryAmount额度为null时将totalCreditAmount赋值给它，在自动恢复原来状态时将totalTemporaryAmount重新置空
            if((totalTemporaryAmount == null || totalTemporaryAmount.compareTo(new BigDecimal(0)) == 0 )
                    && approve.getProcessId().compareTo(process.getId()) == 0){
                bsCompany.setTotalTemporaryAmount(oldTotalCreditAmount);
            }

            bsCompany.setFloatingRateStatus(BasConstants.APPLY_STATUS_COMPLETE);
            bsCompanyService.save(bsCompany);

            // 更新userDetail金额信息
            UserDetail userDetail = userDetailDao.findByCompanyIdAndIsBindTrueAndEnableFlgTrue(entity.getCompanyId());
            if (userDetail != null) {
                userDetail.setTotalCreditAmount(totalCreditAmount);
                userDetailDao.save(userDetail);
            }
        }
    }

    /**
     * 计算最终额度
     * 最终额度 = MAX ( 基本额度，MIN ( 基本额度 * ( 1 + 上浮比例 )，最高限额 ) )
     *
     * @return
     */
    private BigDecimal evalTotalCreditAmount(BsCompanyQuota quota, BigDecimal baseQuota) {
        baseQuota = baseQuota == null ? BigDecimal.ZERO : baseQuota;
        BigDecimal r;
        // BigDecimal baseUp = quota.getFloatingRate().add(BigDecimal.ONE).multiply(baseQuota);
        BigDecimal baseUp = quota.getFloatingRate().multiply(baseQuota);
        if (baseUp.compareTo(quota.getFloatingMaxAmount()) <= 0) {
            r = baseUp;
        }else {
            r = quota.getFloatingMaxAmount();
        }

        // MAX ( 基本额度，r )
        // if (baseQuota.compareTo(r) >= 0) {
        //    r = baseQuota;
        //}
        return r.add(baseQuota);
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
        BsCompanyQuota entity = JsonUtil.json2Object(BsCompanyQuota.class, contents);
        BsCompany company = bsCompanyService.getEntity(entity.getCompanyId());
        company.setFloatingRateStatus(BasConstants.APPLY_STATUS_REJECT);
        bsCompanyService.save(company);
        logger.info("doStepBack - " + JsonUtil.obj2Json(entity));
    }

    @Override
    public void doWithdraw(PmApproveWithdrawVo vo) throws ApplicationException {

    }

    @Override
    public BaseDao<BsCompanyQuota> getBaseDao() {
        return bsCompanyQuotaDao;
    }

    @Override
    public Class<BsCompanyQuota> getEntityClazz() {
        return BsCompanyQuota.class;
    }

    @Override
    public void startFlow(String bizEntityJson,Long companyId) throws ApplicationException {
        PmApproveSaveVo startVo = new PmApproveSaveVo();
        startVo.setMode(BasConstants.APPROVE_STATUS_A);
        startVo.setStatus(BasConstants.APPROVE_STATUS_A);
        startVo.setEnterpriseId(ZG_ENTERPRISE_ID);
        BsCompany company = bsCompanyService.getEntity(companyId);
        PmProcessSearchVo searchVo = new PmProcessSearchVo();
        searchVo.setEnterpriseId(ZG_ENTERPRISE_ID);
        searchVo.setProcessCode("APPLY_CPN_QUOTA");
        PmProcess process = pmProcessService.findByProcessCode(searchVo);
        SysUserSdk userById = authOpenFacade.findUserById(company.getMatchUserId());
        startVo.setUserId(userById.getUserId());
        startVo.setUserName(userById.getNickName());
        startVo.setProcessId(process.getId());
        startVo.setApproveId(0L);
        startVo.setBizEntityJson(bizEntityJson);
        pmApproveService.startFlow(startVo);
    }

    @Override
    public BsCompanyQuota getLatestApply(Long companyId) {
        BsCompanyQuota bsCompanyQuota = bsCompanyQuotaDao.findTopByCompanyIdOrderByIdDesc(companyId);
        if(bsCompanyQuota != null) {
            PmApprove pmApprove = pmApproveService.findApproveNoByApproveId(bsCompanyQuota.getApproveId());
            // 是否是完成状态
            if(pmApprove != null && BasConstants.APPROVE_STATUS_D.equals(pmApprove.getStatus())) {
                Date lastApproveDate = pmApprove.getLastApproveDate();
                // 额度有效日期 审批完成后3天为有效期
                Date effectiveDate = DateOperator.addDays(lastApproveDate, +3);
                Date newDate = new Date();
                // 超过有效期
                if(newDate.compareTo(effectiveDate) > 0){
                    return null;
                }
            }
        }
        return bsCompanyQuota;
    }
}
