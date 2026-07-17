package com.spt.bas.server.service.impl;


import cn.hutool.core.date.DateUtil;
import com.spt.auth.sdk.entity.SysDeptSdk;
import com.spt.auth.sdk.open.IAuthOpenFacade;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.*;
import com.spt.bas.client.vo.CtrContractApplyVo;
import com.spt.bas.server.annotation.ServerTransactional;
import com.spt.bas.server.ctr.service.ICtrContractUpdateService;
import com.spt.bas.server.dao.ApplyInvoiceDao;
import com.spt.bas.server.dao.CtrContractDcsxApplyDao;
import com.spt.bas.server.dao.CtrServiceContractDao;
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
import com.spt.tools.data.annotation.ServiceTransactional;
import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.service.BaseService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * 代采赊销开票
 */
@Component("applyInvoiceDcsxService")
@Transactional(readOnly = true)
public class ApplyInvoiceDcsxServiceImpl extends BaseService<ApplyInvoice> implements IApplyInvoiceDcsxService, IPmService, IPmApproveListener {
    @Autowired
    private ApplyInvoiceDao applyInvoiceDao;
    @Autowired
    ICtrContractUpdateService contractUpdateService;
    @Autowired
    private IApplyDcsxService applyDcsxService;
    @Autowired
    private ICtrContractApplyService contractApplyService;
    @Autowired
    private ICtrContractOphisService contractOphisService;
    @Autowired
    private IApplyDcsxService dcsxService;
    @Autowired
    private IBsCompanyAccountService bsCompanyAccountService;
    @Autowired
    private CtrServiceContractDao ctrServiceContractDao;
    @Autowired
    private IAuthOpenFacade authOpenFacade;
    @Autowired
    private IPmApproveService iPmApproveService;
    @Autowired
    private ICtrContractProfitService ctrContractProfitService;
    @Autowired
    private CtrContractDcsxApplyDao ctrContractDcsxApplyDao;

    @Override
    public BaseDao<ApplyInvoice> getBaseDao() {
        return applyInvoiceDao;
    }

    @Override
    public Class<ApplyInvoice> getEntityClazz() {
        return ApplyInvoice.class;
    }

    @Override
    public void doStepFlow(PmApprove approve, PmApproveStep nextStep) throws ApplicationException {
        if (approve.getStatus().equals(BasConstants.APPROVE_STATUS_D)) {
            ApplyInvoice invoice = applyInvoiceDao.findOne(approve.getBizId());
            Long contractId = invoice.getContractId();
            ApplyCtrDCSX contract = null;
                if (invoice.getDealAmount().compareTo(BigDecimal.ZERO) == 0) {
                    throw new ApplicationException("开票金额必须大于0!");
                }
                contract = dcsxService.findById(contractId);
                if (contract.getApplyCancelFlg()) {
                    throw new ApplicationException("请驳回，该合同处于合同作废阶段!");
                }

            // 适配历史审批中的审批单添加操作记录：过段时间可以去除
            Date createdDate = approve.getCreatedDate();
            if (createdDate != null) {
                String format = DateUtil.format(createdDate, "yyyy-MM-dd");
                if (BasConstants.NEW_ADD_HIS_START_DATE.compareTo(format) >= 0) {
                    // 添加合同操作记录
                    if (StringUtils.equals(BasConstants.BUSINESS_TYPE_ZY_TP, contract.getBusinessType())) {
                        contractOphisService.addHisDcTp(BasConstants.APPLY_TYPE_DCSX_N, contract.getContractStatus(), contractId, approve, invoice.getInvoiceDate());
                    } else {
                        contractOphisService.addHisDcsx(BasConstants.APPLY_TYPE_DCSX_N, contract.getContractStatus(), contractId, approve, invoice.getInvoiceDate());
                    }
                }
            }

                //开票金额
                BigDecimal invoiceAmount = Objects.isNull(contract.getInvoiceBillAmount()) ? BigDecimal.ZERO : contract.getInvoiceBillAmount();
                invoiceAmount =invoiceAmount.add(invoice.getDealAmount());
                String flag=contract.getContractStatus();
                BigDecimal dealedAmount = contract.getDealedAmount();
                BigDecimal billedAmount = contract.getBilledAmount();
                BigDecimal totalAmount = contract.getTotalAmount();
                String status = contract.getStatus();
            if(dealedAmount.compareTo(totalAmount)>=0 && billedAmount.compareTo(totalAmount)>=0 && invoiceAmount.compareTo(contract.getTotalAmount())>=0){

                    status="D";
                }
                if(invoiceAmount.compareTo(contract.getTotalAmount())>=0 ){
                    // V2：已开票
                    flag="V2";
                }
                dcsxService.updateStatus(contract.getId(),invoiceAmount,flag,status);
        }
    }

    @Override
    @ServiceTransactional
    public void doWithdraw(PmApproveWithdrawVo vo) throws ApplicationException {
        ApplyInvoice invoice = applyInvoiceDao.findOne(vo.getBizId());
        Long contractId = invoice.getContractId();
        BigDecimal dealedAmount = invoice.getDealAmount();

        ApplyCtrDCSX entity = applyDcsxService.getEntity(contractId);
        entity.setInvoiceBillAmount(entity.getInvoiceBillAmount().add(dealedAmount.negate()));
        applyDcsxService.save(entity);

        CtrContractDcsxApply contractApply = ctrContractDcsxApplyDao.findByCtrContractId(contractId);
        contractApply.setApplyBillAmount(contractApply.getApplyPayAmount().add(dealedAmount.negate()));
        ctrContractDcsxApplyDao.save(contractApply);
    }


    @Override
    public IPmEntity saveEntity(IPmEntity pmEntity) throws ApplicationException {

        if (pmEntity != null) {
            ApplyInvoice entity = (ApplyInvoice) pmEntity;
            entity.setBillMark(BasConstants.Special_Invoice_Mark);
            PmApprove entity1 = iPmApproveService.getEntity(entity.getApproveId());
            if(entity1 != null){
                SysDeptSdk deptByUserId = authOpenFacade.findDeptByUserId(entity1.getCreateUserId());
                entity.setDeptId(deptByUserId.getDeptId());
            }
            if (entity.getId() == 0) {
                //生成唯一编号
                String applyNo = composeContractNo(entity.getContractNo());
                entity.setApplyNo(applyNo);
            }

            return save(entity);
        }
        return null;
    }

    private String composeContractNo(String contractNo) {
        List<ApplyInvoice> list = applyInvoiceDao.findByContractNo(contractNo);
        String fmt = String.format("%02d", list.size() + 1);
        return contractNo + BasConstants.APPLY_TYPE_N + fmt;
    }

    @Override
    public String getSubject(IPmEntity pmEntity, PmProcess process) {
        if (pmEntity != null) {
            ApplyInvoice entity = (ApplyInvoice) pmEntity;
            String billMark = entity.getBillMark();
            if(BasConstants.Special_Invoice_Mark.equals(billMark)){
                String companyName1 = RuleUtil.companyNameSubString(entity.getCompanyName());
                String companyName2 = RuleUtil.companyNameSubString(entity.getOurCompanyName());
                String subject = SubjectUtil.formatSubject("[特殊]"+entity.getContractNo(),companyName1,SubjectUtil.formatMoney(entity.getDealAmount(), RuleUtil.monetaryUnit),companyName2);
                return subject;
            }else{
                String companyName1 = RuleUtil.companyNameSubString(entity.getCompanyName());
                String companyName2 = RuleUtil.companyNameSubString(entity.getOurCompanyName());
                String subject = SubjectUtil.formatSubject(entity.getContractNo(),companyName1,SubjectUtil.formatMoney(entity.getDealAmount(), RuleUtil.monetaryUnit),companyName2);
                return subject;
            }

        }
        return null;
    }

    @Override
    @ServerTransactional
    public void doStepIn(PmApprove approve) throws ApplicationException {
        ApplyInvoice entity = applyInvoiceDao.findOne(approve.getBizId());
        List<ApplyInvoice> invoiceList = applyInvoiceDao.findByContractNo(entity.getContractNo());
        ApplyCtrDCSX contract = dcsxService.findById(approve.getContractId());
        BigDecimal totalAmount = contract.getTotalAmount();
        BigDecimal settlementTotalAmount = contract.getSettlementTotalAmount();
        if (CollectionUtils.isNotEmpty(invoiceList)) {
            BigDecimal receiveAmount = invoiceList.stream()
                    .filter(i -> (StringUtils.equals("D", i.getStatus()) || StringUtils.equals("A", i.getStatus())))
                    .map(ApplyInvoice::getDealAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal currAmount = totalAmount.subtract(receiveAmount);
            if (StringUtils.equals(BasConstants.BUSINESS_TYPE_ZY_TP, contract.getBusinessType())) {
                currAmount = settlementTotalAmount.subtract(receiveAmount);
            }
            if (currAmount.compareTo(BigDecimal.ZERO) < 0) {
                throw new ApplicationException("开票金额申请错误，剩余可开票金额： " + BigDecimal.ZERO.max(currAmount.add(entity.getDealAmount())));
            }
        }
        if (entity.getDealAmount().compareTo(BigDecimal.ZERO) == 0) {
            throw new ApplicationException("开票金额必须大于0!");
        }
        //判断开票开户行是否存在
        verifyCompanyAccount(entity);
        if (StringUtils.equals(BasConstants.BUSINESS_TYPE_ZY_TP, contract.getBusinessType())) {
            contractOphisService.addHisDcTp(BasConstants.APPLY_TYPE_DCSX_N, contract.getContractStatus(), entity.getContractId(), approve, entity.getInvoiceDate());
        } else {
            contractOphisService.addHisDcsx(BasConstants.APPLY_TYPE_DCSX_N, contract.getContractStatus(), entity.getContractId(), approve, entity.getInvoiceDate());
        }
    }

    /**
     * 判断开票开户行是否存在
     *
     * @param entity
     */
    private void verifyCompanyAccount(ApplyInvoice entity) throws ApplicationException {
        BsCompanyAccount account = new BsCompanyAccount();
        account.setCompanyId(entity.getCompanyId());
        account.setBankAccount(entity.getBankAccount());
        account.setBankName(entity.getBankName());
        account.setTaxNo(entity.getTaxNo());
        account.setEnterpriseId(entity.getEnterpriseId());
        bsCompanyAccountService.verifyCompanyAccount(account);
    }


    @Override
    @ServerTransactional
    public void doStepBack(PmApproveCurrVo approve, PmApproveStep nextStep) throws ApplicationException {
        ApplyInvoice entity = applyInvoiceDao.findOne(approve.getBizId());
        if (isServiceContract(entity)) {
            Long serviceContractId = entity.getContractId();
            CtrServiceContract serviceContract = ctrServiceContractDao.findOne(serviceContractId);
            //更新CtrContractApply中数据
            CtrContractApplyVo vo = new CtrContractApplyVo();
            vo.setContractId(serviceContract.getCtrContractId());
            vo.setDealAmount(entity.getDealAmount().negate());
            vo.setApplyType(BasConstants.APPLY_TYPE_SB);
            contractApplyService.updateCtrContractApply(vo);
        } else {
            //更新CtrContractApply中数据
            CtrContractApplyVo vo = new CtrContractApplyVo();
            vo.setContractId(entity.getContractId());
            vo.setDealAmount(entity.getDealAmount().negate());
            vo.setApplyType(BasConstants.APPLY_TYPE_N);
            contractApplyService.updateCtrContractApply(vo);
        }

    }


    /**
     * 判断是否是服务合同
     *
     * @param invoice
     *
     * @return
     */
    private Boolean isServiceContract(ApplyInvoice invoice) {
        if (invoice.getContractNo().contains("SE")) {
            return true;
        }
        return false;
    }

}

