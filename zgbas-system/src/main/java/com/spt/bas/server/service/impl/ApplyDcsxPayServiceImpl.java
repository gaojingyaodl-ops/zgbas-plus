package com.spt.bas.server.service.impl;

import cn.hutool.core.date.DateUtil;
import com.alibaba.cloud.commons.lang.StringUtils;
import com.spt.auth.sdk.entity.SysDeptSdk;
import com.spt.auth.sdk.open.IAuthOpenFacade;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.constant.BsDictConstants;
import com.spt.bas.client.entity.ApplyCtrDCSX;
import com.spt.bas.client.entity.ApplyPay;
import com.spt.bas.client.entity.CtrContract;
import com.spt.bas.client.entity.CtrContractDcsxApply;
import com.spt.bas.server.annotation.ServerTransactional;
import com.spt.bas.server.cache.BsDictUtil;
import com.spt.bas.server.dao.ApplyDcsxPayDao;
import com.spt.bas.server.dao.ApplyPayDao;
import com.spt.bas.server.dao.CtrContractDcsxApplyDao;
import com.spt.bas.server.enums.FundFlowEnum;
import com.spt.bas.server.service.*;
import com.spt.bas.server.util.RuleUtil;
import com.spt.bas.server.util.SubjectUtil;
import com.spt.pm.entity.PmApprove;
import com.spt.pm.entity.PmApproveStep;
import com.spt.pm.entity.PmProcess;
import com.spt.pm.inter.IPmApproveListener;
import com.spt.pm.inter.IPmEntity;
import com.spt.pm.inter.IPmService;
import com.spt.pm.service.IPmApproveService;
import com.spt.pm.vo.PmApproveCurrVo;
import com.spt.pm.vo.PmApproveWithdrawVo;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.service.BaseService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 付款申请
 *
 * @author shengong
 */
@Component("applyDcsxPayService")
@Transactional(readOnly = true)
public class ApplyDcsxPayServiceImpl extends BaseService<ApplyPay> implements IApplyDcsxPayService, IPmService, IPmApproveListener {
    @Autowired
    private ApplyPayDao applyPayDao;
    @Autowired
    private ApplyDcsxPayDao applyDcsxPayDao;
    @Autowired
    private ICtrContractOphisService contractOphisService;
    @Autowired
    private IApplyDcsxService applyDcsxService;
    @Autowired
    private IPmApproveService pmApproveService;
    @Autowired
    private IAuthOpenFacade authOpenFacade;
    @Autowired
    private CtrContractDcsxApplyDao ctrContractDcsxApplyDao;
    @Autowired
    private IApplyDcsxReceiveService applyDcsxReceiveService;
    @Resource
    private IFundAmountFlowService fundAmountFlowService;
    @Autowired
    private ICtrContractService ctrContractService;
    @Override
    public BaseDao<ApplyPay> getBaseDao() {
        return applyPayDao;
    }

    @Override
    public Class<ApplyPay> getEntityClazz() {
        return ApplyPay.class;
    }

    @Override
    @ServerTransactional
    public void updateFileId(Long id, String fileId) {
        applyPayDao.updateFileId(id, fileId);
    }

    @Override
    public void doStepFlow(PmApprove approve, PmApproveStep nextStep) throws ApplicationException {
        if (approve.getStatus().equals(BasConstants.APPROVE_STATUS_D)) {
            ApplyPay pay = applyPayDao.findOne(approve.getBizId());
            Long contractId = pay.getContractId();
            ApplyCtrDCSX contractApply = applyDcsxService.getEntity(contractId);
            BigDecimal dealedAmount = contractApply.getDealedAmount();
            BigDecimal payAmount = pay.getPayAmount().add(dealedAmount);

            BigDecimal realApplyPayAmount = contractApply.getApplyPayAmount().subtract(pay.getPayAmount());
            realApplyPayAmount = realApplyPayAmount.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : realApplyPayAmount;
            contractApply.setApplyPayAmount(realApplyPayAmount);

            contractApply.setPayType(pay.getPayType());
            contractApply.setDealedAmount(payAmount);
            contractApply.setPayMode(pay.getPayMode());
            contractApply.setLastPayDate(pay.getPayDate());
            contractApply.setTicketDueTime(pay.getTicketDueTime());
            contractApply.setDueTime(pay.getDueTime());
            contractApply.setRemark(pay.getRemark());
            contractApply.setFileTypeId(pay.getFileTypeId());
            BigDecimal totalAmount = contractApply.getTotalAmount();
            BigDecimal settlementTotalAmount = contractApply.getSettlementTotalAmount();
            if (StringUtils.equals(BasConstants.BUSINESS_TYPE_ZY_TP, contractApply.getBusinessType()) &&
                    settlementTotalAmount != null && settlementTotalAmount.compareTo(BigDecimal.ZERO) > 0) {
                totalAmount = settlementTotalAmount;
            }
            contractApply.setUnpayedAmount(totalAmount.subtract(contractApply.getDealedAmount()));

            // 适配历史审批中的审批单添加操作记录：过段时间可以去除
            Date createdDate = approve.getCreatedDate();
            if (createdDate != null) {
                String format = DateUtil.format(createdDate, "yyyy-MM-dd");
                if (BasConstants.NEW_ADD_HIS_START_DATE.compareTo(format) >= 0) {
                    // 添加合同操作记录
                    if (StringUtils.equals(BasConstants.BUSINESS_TYPE_ZY_TP, contractApply.getBusinessType())) {
                        contractOphisService.addHisDcTp(BasConstants.APPLY_TYPE_DCSX_P,contractApply.getContractStatus(), contractId, approve, pay.getPayDate());
                    } else {
                        contractOphisService.addHisDcsx(BasConstants.APPLY_TYPE_DCSX_P,contractApply.getContractStatus(), contractId, approve, pay.getPayDate());
                    }
                }
            }

            try {
                applyDcsxReceiveService.doApplyDcsxReceiveTask(contractApply, pay);
            } catch (Exception e) {
                logger.error("doApplyDcsxReceiveTask error", e);
            }

            if (StringUtils.equals("F", pay.getPayMode())){
                fundAmountFlowService.addFundFlow(pay.getOurCompanyName(), pay.getCompanyName(), pay.getPayAmount().negate(), FundFlowEnum.Payment, approve);
            }
        }
    }


    @Override
    @ServerTransactional
    public void doWithdraw(PmApproveWithdrawVo vo) throws ApplicationException {
        ApplyPay pay = applyPayDao.findOne(vo.getBizId());
        Long contractId = pay.getContractId();
        BigDecimal payAmount = pay.getPayAmount();

        ApplyCtrDCSX entity = applyDcsxService.getEntity(contractId);
        entity.setDealedAmount(entity.getDealedAmount().add(payAmount.negate()));
        applyDcsxService.save(entity);

        CtrContractDcsxApply contractApply = ctrContractDcsxApplyDao.findByCtrContractId(contractId);
        contractApply.setApplyPayAmount(contractApply.getApplyPayAmount().add(payAmount.negate()));
        ctrContractDcsxApplyDao.save(contractApply);

        PmApprove approve = pmApproveService.getEntity(pay.getApproveId());
        if (StringUtils.equals("F", pay.getPayMode())){
            fundAmountFlowService.addFundFlow(pay.getOurCompanyName(), pay.getCompanyName(), pay.getPayAmount(), FundFlowEnum.PaymentCancel, approve);
        }
    }

    @Override
    public  List<ApplyPay> findApplyPay(String contractNo) {
        return applyDcsxPayDao.findApplyPay(contractNo);
    }

    @Override
    @ServerTransactional
    public void doStepBack(PmApproveCurrVo approve, PmApproveStep nextStep) throws ApplicationException {
        ApplyPay pay = applyPayDao.findOne(approve.getBizId());
        Long contractId = pay.getContractId();
        ApplyCtrDCSX contractApply = applyDcsxService.getEntity(contractId);
        BigDecimal applyPayAmount = contractApply.getApplyPayAmount();
        BigDecimal payAmount = pay.getPayAmount();
        contractApply.setApplyPayAmount(applyPayAmount.add(payAmount.negate()));
        BigDecimal totalAmount = contractApply.getTotalAmount();
        BigDecimal settlementTotalAmount = contractApply.getSettlementTotalAmount();
        if (StringUtils.equals(BasConstants.BUSINESS_TYPE_ZY_TP, contractApply.getBusinessType()) &&
                settlementTotalAmount != null && settlementTotalAmount.compareTo(BigDecimal.ZERO) > 0) {
            totalAmount = settlementTotalAmount;
        }
        contractApply.setUnpayedAmount(totalAmount.subtract(contractApply.getDealedAmount()));
        applyDcsxService.save(contractApply);
    }


    @Override
    @ServerTransactional
    public IPmEntity saveEntity(IPmEntity pmEntity) throws ApplicationException {
        if (pmEntity != null) {
            ApplyPay entity = (ApplyPay) pmEntity;
            ApplyCtrDCSX ctr = applyDcsxService.getEntity(entity.getContractId());
            PmApprove entity1 = pmApproveService.getEntity(ctr.getApproveId());
            if(entity1 != null){
                SysDeptSdk deptByUserId = authOpenFacade.findDeptByUserId(entity1.getCreateUserId());
                entity.setDeptId(deptByUserId.getDeptId());
            }
            BigDecimal totalAmount = ctr.getTotalAmount();
            BigDecimal settlementTotalAmount = ctr.getSettlementTotalAmount();
            if (StringUtils.equals(BasConstants.BUSINESS_TYPE_ZY_TP, ctr.getBusinessType()) &&
                    settlementTotalAmount != null && settlementTotalAmount.compareTo(BigDecimal.ZERO) > 0) {
                totalAmount = settlementTotalAmount;
            }
            BigDecimal unPayedAmount = totalAmount.subtract(ctr.getDealedAmount());
            entity.setUnpayedAmount(unPayedAmount);
            // 设置所属区域
            List<CtrContract> contractLists = ctrContractService.findByApproveId(ctr.getApproveId());
            // 设置所属区域
            if(CollectionUtils.isNotEmpty(contractLists)){
                Optional<CtrContract> first = contractLists.stream().filter(it -> Objects.equals(it.getContractType(), "S")).findFirst();
                first.ifPresent(contract -> entity.setOwningRegion(contract.getOwningRegion()));
            }
            return save(entity);
        }
        return null;
    }

    @Override
    public String getSubject(IPmEntity pmEntity, PmProcess process) {
        if (pmEntity != null) {
            ApplyPay pay = (ApplyPay) pmEntity;
            String contractNo = pay.getContractNo();
            String companyName = pay.getCompanyName();
            BigDecimal sumNumber = pay.getPayAmount();
            String payType = pay.getPayType();
            String title = BsDictUtil.getValue(pay.getEnterpriseId(), BsDictConstants.DICT_TYPE_PAYTYPE, payType);
            String companyName1 = RuleUtil.companyNameSubString(companyName);
            String companyName2 = RuleUtil.companyNameSubString( pay.getOurCompanyName());
            String company="";
            if(StringUtils.isNotBlank(companyName1)&&StringUtils.isNotBlank(companyName2)){
                company=companyName1+"-"+companyName2;
            }
            return SubjectUtil.formatSubject(contractNo,company,SubjectUtil.formatMoney(sumNumber , RuleUtil.monetaryUnit),title);
        }
        return null;
    }

    @Override
    @ServerTransactional
    public void doStepIn(PmApprove approve) throws ApplicationException {
        ApplyPay entity = applyPayDao.findOne(approve.getBizId());
        ApplyCtrDCSX contractApply = applyDcsxService.getEntity(entity.getContractId());
        if (Objects.isNull(contractApply)){
            return;
        }
        if (entity.getPayAmount().compareTo(BigDecimal.ZERO) == 0) {
            throw new ApplicationException("付款金额必须大于0!");
        }

        BigDecimal sumApplyPayAmount = applyPayDao.findSumApplyAmountPay(entity.getContractNo());
        sumApplyPayAmount = Objects.isNull(sumApplyPayAmount) ? BigDecimal.ZERO : sumApplyPayAmount;
        BigDecimal totalAmount = contractApply.getTotalAmount();
        BigDecimal settlementTotalAmount = contractApply.getSettlementTotalAmount();
        if (StringUtils.equals(BasConstants.BUSINESS_TYPE_ZY_TP, contractApply.getBusinessType()) &&
                settlementTotalAmount != null && settlementTotalAmount.compareTo(BigDecimal.ZERO) > 0) {
            totalAmount = settlementTotalAmount;
        }
        BigDecimal subtract = totalAmount.subtract(sumApplyPayAmount);
        if (subtract.compareTo(BigDecimal.ZERO) < 0) {
            throw new ApplicationException("付款金额有误,剩余可付款金额为：" + subtract);
        }
        contractApply.setApplyPayAmount(sumApplyPayAmount);
        contractApply.setUnpayedAmount(totalAmount.subtract(sumApplyPayAmount));
        applyDcsxService.save(contractApply);
        if (StringUtils.equals(BasConstants.BUSINESS_TYPE_ZY_TP, contractApply.getBusinessType())) {
            contractOphisService.addHisDcTp(BasConstants.APPLY_TYPE_DCSX_P,contractApply.getContractStatus(), entity.getContractId(), approve, entity.getPayDate());
        } else {
            contractOphisService.addHisDcsx(BasConstants.APPLY_TYPE_DCSX_P,contractApply.getContractStatus(), entity.getContractId(), approve, entity.getPayDate());
        }
    }


    @Override
    public List<ApplyPay> findByContractId(Long contractId) {
        return applyPayDao.findByContractId(contractId);
    }

    @Override
    @ServerTransactional
    public void updateApplyStatus(Long contractId) {
        applyPayDao.updateApplyStatus(contractId);
    }

}



