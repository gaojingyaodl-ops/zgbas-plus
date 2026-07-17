package com.spt.bas.server.service.impl;

import com.spt.auth.sdk.entity.SysDeptSdk;
import com.spt.auth.sdk.open.IAuthOpenFacade;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.constant.BsDictConstants;
import com.spt.bas.client.entity.ApplyCtrContractFactor;
import com.spt.bas.client.entity.ApplyPay;
import com.spt.bas.client.entity.CtrContract;
import com.spt.bas.server.cache.BsDictUtil;
import com.spt.bas.server.dao.ApplyCtrContractFactoDao;
import com.spt.bas.server.dao.ApplyPayDao;
import com.spt.bas.server.service.ICtrContractService;
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
import com.spt.tools.data.annotation.ServiceTransactional;
import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.service.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

/**
 * 保理预算保证金付款申请
 * @Author: gaojy
 * @create 2022/9/23 16:30
 * @version: 1.0
 * @description:
 */
@Component("applyFactorPayService")
public class ApplyFactorPayServiceImpl extends BaseService<ApplyPay> implements IPmService, IPmApproveListener {
    @Autowired
    private ApplyPayDao applyPayDao;
    @Autowired
    private ICtrContractService ctrContractService;
    @Autowired
    private IPmApproveService pmApproveService;
    @Autowired
    private IAuthOpenFacade authOpenFacade;
    @Autowired
    private ApplyCtrContractFactoDao applyCtrContractFactoDao;

    @Override
    public BaseDao<ApplyPay> getBaseDao() {
        return applyPayDao;
    }

    @Override
    public Class<ApplyPay> getEntityClazz() {
        return ApplyPay.class;
    }

    @Override
    public void doStepFlow(PmApprove approve, PmApproveStep nextStep) throws ApplicationException {
        if (approve.getStatus().equals(BasConstants.APPROVE_STATUS_D)) {
            ApplyPay pay = applyPayDao.findOne(approve.getBizId());
            Long contractId = pay.getContractId();
            CtrContract contract = ctrContractService.getEntity(contractId);

            contract.setFactorStatus(BasConstants.FACTOR_STATUS_B);
            ctrContractService.save(contract);
            ApplyCtrContractFactor factor = applyCtrContractFactoDao.findByContractNo(contract.getContractNo());
            if (Objects.nonNull(factor)) {
                factor.setFactorStatus(BasConstants.FACTOR_STATUS_B);
                applyCtrContractFactoDao.updateStatusByContractNo(factor.getContractNo(), factor.getFactorStatus());
            }
            pay.setFactorAmount(contract.getFactoringAmount());
            applyPayDao.save(pay);
        }
    }

    @Override
    @ServiceTransactional
    public void doStepIn(PmApprove approve) throws ApplicationException {
        ApplyPay entity = applyPayDao.findOne(approve.getBizId());
        ApplyCtrContractFactor factor = applyCtrContractFactoDao.findByContractNo(entity.getContractNo());
        if (Objects.nonNull(factor) && Boolean.FALSE.equals(factor.getFactorPayFlg())) {
            throw new ApplicationException("保证金付款申请已存在，请勿重复申请！");
        } else {
            factor.setFactorPayFlg(false);
            applyCtrContractFactoDao.save(factor);
        }
    }

    @Override
    public void doStepBack(PmApproveCurrVo approve, PmApproveStep nextStep) throws ApplicationException{
        ApplyPay pay = applyPayDao.findOne(approve.getBizId());
        ApplyCtrContractFactor factor = applyCtrContractFactoDao.findByContractNo(pay.getContractNo());
        factor.setFactorPayFlg(true);
        applyCtrContractFactoDao.save(factor);
    }

    @Override
    public void doWithdraw(PmApproveWithdrawVo vo) throws ApplicationException {
        ApplyPay pay = applyPayDao.findOne(vo.getBizId());
        ApplyCtrContractFactor factor = applyCtrContractFactoDao.findByContractNo(pay.getContractNo());
        factor.setFactorPayFlg(true);
        applyCtrContractFactoDao.save(factor);
    }

    @Override
    public void doRetrieve(PmApproveRetrieveVo vo) throws ApplicationException {
        ApplyPay pay = applyPayDao.findOne(vo.getBizId());
        ApplyCtrContractFactor factor = applyCtrContractFactoDao.findByContractNo(pay.getContractNo());
        factor.setFactorPayFlg(true);
        applyCtrContractFactoDao.save(factor);
    }

    @Override
    public IPmEntity saveEntity(IPmEntity pmEntity) throws ApplicationException {
        if (pmEntity != null) {
            ApplyPay entity = (ApplyPay) pmEntity;

            CtrContract ctr = ctrContractService.getEntity(entity.getContractId());
            BigDecimal unPayedAmount = ctr.getTotalAmount().subtract(ctr.findRealDealedAmount());
            entity.setUnpayedAmount(unPayedAmount);
            entity.setFactorAmount(entity.getPayAmount());
            PmApprove pmApprove = pmApproveService.getEntity(entity.getApproveId());
            if (pmApprove != null) {
                SysDeptSdk deptByUserId = authOpenFacade.findDeptByUserId(pmApprove.getCreateUserId());
                entity.setDeptId(deptByUserId.getDeptId());
            }
            entity.setBusinessType(ctr.getBusinessType());

            //新增时
            if (entity.getId() == 0) {
                //生成合同号
                String applyNo = composeContractNo(entity.getContractNo());
                entity.setApplyNo(applyNo);
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
            String companyName = pay.getCompanyName();
            BigDecimal sumNumber = pay.getPayAmount();
            String payAmount = NumberUtil.formatNumber(sumNumber, "#.##");
            String payType = pay.getPayType();
            String title = BsDictUtil.getValue(pay.getEnterpriseId(), BsDictConstants.DICT_TYPE_PAYTYPE, payType);
            return String.format("%s", "[" + contractNo + " " + companyName + " " + payAmount + " " + title + "]");
        }
        return null;
    }

    private String composeContractNo(String contractNo) {
        List<ApplyPay> deliveryIn = applyPayDao.findByContractNo(contractNo);
        String fmt = String.format("%02d", deliveryIn.size() + 1);
        return contractNo + BasConstants.APPLY_TYPE_P + fmt;
    }
}
