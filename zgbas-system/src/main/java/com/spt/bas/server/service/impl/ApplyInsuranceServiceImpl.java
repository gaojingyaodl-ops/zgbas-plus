package com.spt.bas.server.service.impl;

import com.spt.auth.sdk.entity.SysDeptSdk;
import com.spt.auth.sdk.open.IAuthOpenFacade;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.ApplyInsurance;
import com.spt.bas.purchase.wx.client.entity.SaveInfo;
import com.spt.bas.purchase.wx.client.remote.ISaveTempClient;
import com.spt.bas.server.annotation.ServerTransactional;
import com.spt.bas.server.dao.ApplyInsuranceDao;
import com.spt.bas.server.service.IApplyInsuranceService;
import com.spt.pm.entity.PmApprove;
import com.spt.pm.entity.PmApproveStep;
import com.spt.pm.entity.PmProcess;
import com.spt.pm.inter.IPmApproveListener;
import com.spt.pm.inter.IPmEntity;
import com.spt.pm.inter.IPmService;
import com.spt.pm.service.IPmApproveService;
import com.spt.pm.vo.PmApproveWithdrawVo;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.service.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * <p>
 *  保险额度申报
 * </p>
 *
 * @Author: shengong
 * @Date: Created in 2021-02-19 13:36
 */
@Component("applyInsuranceService")
public class ApplyInsuranceServiceImpl extends BaseService<ApplyInsurance> implements IApplyInsuranceService, IPmService, IPmApproveListener {

    @Autowired
    private ApplyInsuranceDao applyInsuranceDao;
    @Autowired
    private ISaveTempClient iSaveTempClient;
    @Autowired
    private IPmApproveService pmApproveService;
    @Autowired
    private IAuthOpenFacade authOpenFacade;

    @Override
    public void doStepFlow(PmApprove approve, PmApproveStep nextStep) throws ApplicationException {
        ApplyInsurance insurance = applyInsuranceDao.findOne(approve.getBizId());
        if (approve.getStatus().equals(BasConstants.APPROVE_STATUS_D)) {
            Long companyId = insurance.getCompanyId();
            SaveInfo saveInfo = iSaveTempClient.getInfoByCompanyId(insurance.getCompanyId(), false, "10");
            if (saveInfo != null) {
                saveInfo.setCompanyId(companyId);
                saveInfo.setCommitFlg(true);
                iSaveTempClient.save(saveInfo);
            }
        }
    }


    @Override
    public IPmEntity saveEntity(IPmEntity pmEntity) throws ApplicationException {
        if (pmEntity != null) {
            ApplyInsurance entity = (ApplyInsurance) pmEntity;
            PmApprove entity1 = pmApproveService.getEntity(entity.getApproveId());
            if (entity1 != null) {
                SysDeptSdk deptByUserId = authOpenFacade.findDeptByUserId(entity1.getCreateUserId());
                entity.setDeptId(deptByUserId.getDeptId());
            }
            return save(entity);
        }
        return null;
    }
    @Override
    public String getSubject(IPmEntity pmEntity, PmProcess process) {
        if (pmEntity != null) {
            ApplyInsurance entity = (ApplyInsurance) pmEntity;
            String subject = String.format("%s", "[" + entity.getCompanyName() + "]");
            return subject;
        }
        return null;
    }


    @Override
    public void doWithdraw(PmApproveWithdrawVo vo) throws ApplicationException {

    }

    @Override
    public BaseDao<ApplyInsurance> getBaseDao() {
        return applyInsuranceDao;
    }

    @Override
    public ApplyInsurance getLatestInsurance(Long companyId) {
        return applyInsuranceDao.getTopByCompanyIdOrderByCreatedDateDesc(companyId);
    }

    @Override
    public ApplyInsurance findByCorpSerialNo(String corpSerialNo) {
        return applyInsuranceDao.findByCorpSerialNo(corpSerialNo);
    }

    @Override
    public ApplyInsurance findTopByCompanyIdAndApplyStatus(Long companyId, String applyStatus) {
        return applyInsuranceDao.findTopByCompanyIdAndApplyStatusOrderByCreatedDateDesc(companyId, applyStatus);
    }

    @Override
    public ApplyInsurance findByRiskCompName(String companyName) {
        return applyInsuranceDao.findByRiskCompName(companyName);
    }

    @Override
    public ApplyInsurance findTopByCompanyIdAndStatus(Long companyId, String status) {
        return applyInsuranceDao.findTopByCompanyIdAndStatusOrderByCreatedDateDesc(companyId, status);
    }
    @Override
    public ApplyInsurance findTopByCompanyNameAndStatusIsNullOrStatus(String riskCompanyName, String status) {
        List<ApplyInsurance> applyInsuranceList = applyInsuranceDao.findTopByCompanyNameAndStatusIsNullOrStatus(riskCompanyName, status);
        if(applyInsuranceList != null && applyInsuranceList.size() > 0){
            return applyInsuranceList.get(0);
        }
        return null;
    }

    @Override
    public ApplyInsurance findTopByCompanyIdAndStatusIsNull(Long companyId) {
        List<ApplyInsurance> applyInsuranceList = applyInsuranceDao.findTopByCompanyIdAndStatusIsNull(companyId);
        if(applyInsuranceList != null && applyInsuranceList.size() > 0){
            return applyInsuranceList.get(0);
        }
        return null;
    }

    @Override
    public ApplyInsurance findByCompanyId(Long companyId) {
        return applyInsuranceDao.findTopByCompanyIdOrderByCreatedDateDesc(companyId);
    }

    @Override
    @ServerTransactional
    public void insuranceApply(ApplyInsurance insurance) {
        try {
            logger.info("保险资料申请测试！");
        } catch (Exception e) {
            logger.error("保险资料申请错误！", e);
        }
    }
}
