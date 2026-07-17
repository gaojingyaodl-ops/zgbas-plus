package com.spt.bas.server.service.impl;

import cn.hutool.core.date.DateUtil;
import com.spt.auth.sdk.entity.SysDeptSdk;
import com.spt.auth.sdk.open.IAuthOpenFacade;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.ApplyCtrDCSX;
import com.spt.bas.client.entity.ApplyInvoiceReceived;
import com.spt.bas.client.entity.CtrContractDcsxApply;
import com.spt.bas.server.annotation.ServerTransactional;
import com.spt.bas.server.dao.ApplyInvoiceReceivedDao;
import com.spt.bas.server.dao.CtrContractDcsxApplyDao;
import com.spt.bas.server.service.IApplyDcsxService;
import com.spt.bas.server.service.IApplyInvoiceReceivedDcsxService;
import com.spt.bas.server.service.ICtrContractOphisService;
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

/**
 * 收票
 */
@Component("applyInvoiceReceivedDcsxService")
@Transactional(readOnly = true)
public class ApplyInvoiceReceivedDcsxServiceImpl extends BaseService<ApplyInvoiceReceived> implements IApplyInvoiceReceivedDcsxService, IPmService, IPmApproveListener {
    @Autowired
    private ApplyInvoiceReceivedDao applyInvoiceReceivedDao;
    @Autowired
    private  IApplyDcsxService applyDcsxService;
    @Autowired
    private ICtrContractOphisService contractOphisService;
    @Autowired
    private IPmApproveService pmApproveService;
    @Autowired
    private IAuthOpenFacade authOpenFacade;
    @Autowired
    private CtrContractDcsxApplyDao ctrContractDcsxApplyDao;

    @Override
    public BaseDao<ApplyInvoiceReceived> getBaseDao() {
        return applyInvoiceReceivedDao;
    }

    @Override
    public Class<ApplyInvoiceReceived> getEntityClazz() {
        return ApplyInvoiceReceived.class;
    }

    @Override
    @ServerTransactional
    public void updateFileId(Long id, String fileId) {
        applyInvoiceReceivedDao.updateFileId(id, fileId);
    }

    @Override
    public void doStepFlow(PmApprove approve, PmApproveStep nextStep) throws ApplicationException {

        if (approve.getStatus().equals(BasConstants.APPROVE_STATUS_D)) {
            ApplyInvoiceReceived invoice = applyInvoiceReceivedDao.findOne(approve.getBizId());
            Long contractId = invoice.getContractId();
            ApplyCtrDCSX contract = applyDcsxService.getEntity(contractId);
            contract.setInvoiceNo(invoice.getInInvoiceNo());
            contract.setInvoiceDate(invoice.getInInvoiceDate());
            contract.setInBillNo(invoice.getInBillNo());


            // 适配历史审批中的审批单添加操作记录：过段时间可以去除
            Date createdDate = approve.getCreatedDate();
            if (createdDate != null) {
                String format = DateUtil.format(createdDate, "yyyy-MM-dd");
                if (BasConstants.NEW_ADD_HIS_START_DATE.compareTo(format) >= 0) {
                    // 添加合同操作记录
                    if (StringUtils.equals(BasConstants.BUSINESS_TYPE_ZY_TP, contract.getBusinessType())) {
                        contractOphisService.addHisDcTp(BasConstants.APPLY_TYPE_DCSX_V,contract.getContractStatus(), contractId, approve, invoice.getInInvoiceDate());
                    } else {
                        contractOphisService.addHisDcsx(BasConstants.APPLY_TYPE_DCSX_V,contract.getContractStatus(), contractId, approve, invoice.getInInvoiceDate());
                    }
                }
            }
            BigDecimal billedAmount = contract.getBilledAmount();
            BigDecimal dealedAmount = invoice.getInvoiceAmount();
            BigDecimal sum = billedAmount.add(dealedAmount);
            contract.setBilledAmount(sum);
            if (sum.compareTo(contract.getTotalAmount()) == 0 || sum.compareTo(contract.getTotalAmount()) == 1) {
                if (contract.getStatus().equals("F")) {
                    contract.setStatus("D");
                } else {
                    contract.setStatus("V");
                }
            }else{
                if (contract.getStatus().equals("F")) {
                    contract.setStatus("F");
                } else {
                    contract.setStatus("B");
                }
            }
        }

    }

    @Override
    @ServiceTransactional
    public void doWithdraw(PmApproveWithdrawVo vo) throws ApplicationException {
        ApplyInvoiceReceived invoice = applyInvoiceReceivedDao.findOne(vo.getBizId());
        Long contractId = invoice.getContractId();
        BigDecimal invoiceAmount = invoice.getInvoiceAmount();

        ApplyCtrDCSX entity = applyDcsxService.getEntity(contractId);
        entity.setBilledAmount(entity.getBilledAmount().add(invoiceAmount.negate()));
        applyDcsxService.save(entity);

        CtrContractDcsxApply contractApply = ctrContractDcsxApplyDao.findByCtrContractId(contractId);
        contractApply.setApplyBillAmount(contractApply.getApplyPayAmount().add(invoiceAmount.negate()));
        ctrContractDcsxApplyDao.save(contractApply);
    }

    @Override
    public IPmEntity saveEntity(IPmEntity pmEntity) throws ApplicationException {
        if (pmEntity != null) {
            ApplyInvoiceReceived entity = (ApplyInvoiceReceived) pmEntity;
            // 同步字段 ourCompanyName invoiceCompanyName
            entity.setOurCompanyName(entity.getInvoiceCompanyName());
            PmApprove entity1 = pmApproveService.getEntity(entity.getApproveId());
            if(entity1 != null){
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
            ApplyInvoiceReceived entity = (ApplyInvoiceReceived) pmEntity;
            String companyName1 = RuleUtil.companyNameSubString(entity.getCompanyName());
            String companyName2 = RuleUtil.companyNameSubString(entity.getInvoiceCompanyName());
            String subject = SubjectUtil.formatSubject(entity.getContractNo(),companyName1,SubjectUtil.formatMoney(entity.getInvoiceAmount() , RuleUtil.monetaryUnit),companyName2);
            return subject;
        }
        return null;
    }

    @Override
    @ServerTransactional
    public void doStepIn(PmApprove approve) throws ApplicationException {
        ApplyInvoiceReceived entity = applyInvoiceReceivedDao.findOne(approve.getBizId());
        List<ApplyInvoiceReceived> invoiceList = applyInvoiceReceivedDao.findByContractNo(entity.getContractNo());
        ApplyCtrDCSX contract = applyDcsxService.findById(entity.getContractId());
        BigDecimal totalAmount = contract.getTotalAmount();
        BigDecimal settlementTotalAmount = contract.getSettlementTotalAmount();
        if (CollectionUtils.isNotEmpty(invoiceList)) {
            BigDecimal invoiceAmount = invoiceList.stream()
                    .filter(i -> (StringUtils.equals("D", i.getStatus()) || StringUtils.equals("A", i.getStatus())))
                    .map(ApplyInvoiceReceived::getInvoiceAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal currAmount = totalAmount.subtract(invoiceAmount);
            if (StringUtils.equals(BasConstants.BUSINESS_TYPE_ZY_TP, contract.getBusinessType())) {
                currAmount = settlementTotalAmount.subtract(invoiceAmount);
            }
            if (currAmount.compareTo(BigDecimal.ZERO) < 0) {
                throw new ApplicationException("收票金额申请错误，剩余可开票金额： " + BigDecimal.ZERO.max(currAmount.add(entity.getInvoiceAmount())));
            }
        }
        if (entity.getInvoiceAmount().compareTo(BigDecimal.ZERO) == 0) {
            throw new ApplicationException("收票金额必须大于0!");
        }
        if (StringUtils.equals(BasConstants.BUSINESS_TYPE_ZY_TP, contract.getBusinessType())) {
            contractOphisService.addHisDcTp(BasConstants.APPLY_TYPE_DCSX_V,contract.getContractStatus(), entity.getContractId(), approve, entity.getInInvoiceDate());
        } else {
            contractOphisService.addHisDcsx(BasConstants.APPLY_TYPE_DCSX_V,contract.getContractStatus(), entity.getContractId(), approve, entity.getInInvoiceDate());
        }
    }
    @Override
    @ServerTransactional
    public void doStepBack(PmApproveCurrVo approve, PmApproveStep nextStep) throws ApplicationException {

    }

    @Override
    public List<ApplyInvoiceReceived> findByContractId(Long contractId) {
        return applyInvoiceReceivedDao.findByContractId(contractId);
    }

}

