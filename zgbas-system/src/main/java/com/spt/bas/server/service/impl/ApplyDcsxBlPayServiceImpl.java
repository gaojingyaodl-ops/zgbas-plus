package com.spt.bas.server.service.impl;

import com.spt.auth.sdk.entity.SysDeptSdk;
import com.spt.auth.sdk.open.IAuthOpenFacade;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.ApplyCtrContractFactor;
import com.spt.bas.client.entity.ApplyPay;
import com.spt.bas.client.entity.CtrContract;
import com.spt.bas.server.dao.ApplyCtrContractFactoDao;
import com.spt.bas.server.dao.ApplyDcsxBlPayDao;
import com.spt.bas.server.dao.ApplyPayDao;
import com.spt.bas.server.service.IApplyDcsxBlPayService;
import com.spt.bas.server.service.IApplyPayService;
import com.spt.bas.server.service.ICtrContractService;
import com.spt.bas.server.util.SubjectUtil;
import com.spt.bas.server.util.RuleUtil;
import com.spt.pm.entity.PmApprove;
import com.spt.pm.entity.PmApproveStep;
import com.spt.pm.entity.PmProcess;
import com.spt.pm.inter.IPmApproveListener;
import com.spt.pm.inter.IPmEntity;
import com.spt.pm.inter.IPmService;
import com.spt.pm.service.IPmApproveService;
import com.spt.pm.vo.PmApproveCurrVo;
import com.spt.pm.vo.PmApproveRetrieveVo;
import com.spt.pm.vo.PmApproveWithdrawVo;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.core.number.NumberUtil;
import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.service.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;


@Component("applyDcsxBLPayService")
@Transactional(readOnly = true)
public class ApplyDcsxBlPayServiceImpl extends BaseService<ApplyPay> implements IApplyDcsxBlPayService, IPmService, IPmApproveListener {

    @Autowired
    private IPmApproveService pmApproveService;
    @Autowired
    private IApplyPayService applyPayService;
    @Autowired
    private ApplyPayDao applyPayDao;
    //@Autowired
    //private IAdminOpenFacade adminOpenFacade;
    @Autowired
    private IAuthOpenFacade authOpenFacade;
    @Autowired
    private ApplyDcsxBlPayDao applyDcsxBlPayDao;
    @Autowired
    private ICtrContractService contractService;

    @Autowired
    private ApplyCtrContractFactoDao applyCtrContractFactoDao;

    @Override
    public ApplyCtrContractFactor findByApproveId(Long approveid) {
        return applyDcsxBlPayDao.findByApproveId(approveid);
    }

    @Override
    public BaseDao<ApplyPay> getBaseDao() {
        return applyDcsxBlPayDao;
    }

    /**
     * 发起审批
     *
     * @param approve
     */
    @Override
    public void doStepIn(PmApprove approve) throws ApplicationException {
        ApplyPay pay = applyPayDao.findOne(approve.getBizId());
        ApplyCtrContractFactor byApprove = applyCtrContractFactoDao.findByContractNo(pay.getContractNo());
        if(byApprove.getRepaymentApplyStatus().equals("D")||byApprove.getRepaymentApplyStatus().equals("B")){
            throw new ApplicationException("已存在有效记录,无法重复发起提交");

        }
       if(byApprove.getRepaymentApplyStatus()==null||byApprove.getRepaymentApplyStatus().equals("")){
           byApprove.setRepaymentApplyStatus("B");//申请中
           applyCtrContractFactoDao.save(byApprove);
       }

    }

    @Override
    public void doStepFlow(PmApprove approve, PmApproveStep nextStep) throws ApplicationException {
        if (approve.getStatus().equals(BasConstants.APPROVE_STATUS_D)) {
             ApplyPay pay = applyPayDao.findOne(approve.getBizId());
             pay.setStatus("D");
             pay.setBusinessType("ZY-BB");
             applyPayDao.save(pay);
             ApplyCtrContractFactor byApprove = applyCtrContractFactoDao.findByContractNo(pay.getContractNo());
             byApprove.setFactorStatus(BasConstants.FACTOR_STATUS_D);
             byApprove.setBackDate(approve.getCreatedDate());
             byApprove.setBackAmount( pay.getPayAmount());
             byApprove.setRepaymentApplyStatus("D");
             applyCtrContractFactoDao.save(byApprove);
             CtrContract byContractNo = contractService.findByContractNo(pay.getContractNo());
             byContractNo.setFactorStatus(BasConstants.FACTOR_STATUS_D);
            contractService.save(byContractNo);
//            contractOphisService.addHisDcsx(BasConstants.APPLY_TYPE_DCSX_P, contractId, approve, pay.getPayDate());
        }
    }

    @Override
    public void doWithdraw(PmApproveWithdrawVo vo) throws ApplicationException {
        ApplyPay pay = applyPayDao.findOne(vo.getBizId());
        ApplyCtrContractFactor byApprove = applyCtrContractFactoDao.findByContractNo(pay.getContractNo());
        byApprove.setRepaymentApplyStatus("");
        applyCtrContractFactoDao.save(byApprove);

    }

    @Override
    public void doStepBack(PmApproveCurrVo approve, PmApproveStep nextStep) throws ApplicationException {
        ApplyPay pay = applyPayDao.findOne(approve.getBizId());
        ApplyCtrContractFactor byApprove = applyCtrContractFactoDao.findByContractNo(pay.getContractNo());
        byApprove.setRepaymentApplyStatus("");
        applyCtrContractFactoDao.save(byApprove);
    }



    @Override
    public void doRetrieve(PmApproveRetrieveVo vo) throws ApplicationException {
        ApplyPay pay = applyPayDao.findOne(vo.getBizId());
        ApplyCtrContractFactor byApprove = applyCtrContractFactoDao.findByContractNo(pay.getContractNo());
        byApprove.setRepaymentApplyStatus("");
        applyCtrContractFactoDao.save(byApprove);
    }

    @Override
    public IPmEntity saveEntity(IPmEntity pmEntity) throws ApplicationException {
        if (pmEntity != null) {
            ApplyPay entity = (ApplyPay) pmEntity;
            PmApprove entity1 = pmApproveService.getEntity(entity.getApproveId());
            if(entity1 != null){
                //SysDept deptByUserId = adminOpenFacade.findDeptByUserId(entity1.getCreateUserId());
                SysDeptSdk deptByUserId = authOpenFacade.findDeptByUserId(entity1.getCreateUserId());
                entity.setDeptId(deptByUserId.getDeptId());
            }
            return save(entity);

        }
        return null;
    }

    @Override
    public String getSubject(IPmEntity pmEntity, PmProcess pmProcess) {
        if (pmEntity != null) {
            ApplyPay pay = (ApplyPay) pmEntity;
            String contractNo = pay.getContractNo();
            BigDecimal sumNumber = pay.getPayAmount();
            String payAmount = NumberUtil.formatNumber(sumNumber, "#.##");
            String title = "保理预算还款";
            String subject = SubjectUtil.formatSubject(contractNo,payAmount+ RuleUtil.monetaryUnit,title);
            return subject;
        }
        return null;
    }


}
