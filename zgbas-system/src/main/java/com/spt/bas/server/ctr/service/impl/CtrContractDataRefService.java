package com.spt.bas.server.ctr.service.impl;

import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.*;
import com.spt.bas.client.vo.BsCompanyVo;
import com.spt.bas.server.dao.ApplyInvoiceDao;
import com.spt.bas.server.dao.CtrContractApplyDao;
import com.spt.bas.server.dao.CtrContractDao;
import com.spt.bas.server.service.IBsCompanyAccountService;
import com.spt.bas.server.service.IBsCompanyService;
import com.spt.bas.server.service.ICtrContractRelaService;
import com.spt.pm.annotation.ServerTransactional;
import com.spt.pm.dao.PmProcessDao;
import com.spt.pm.entity.PmProcess;
import com.spt.pm.service.IPmApproveService;
import com.spt.pm.vo.PmApproveSaveVo;
import com.spt.tools.core.json.JsonUtil;
import com.spt.tools.jpa.persistence.WebUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * 合同数据刷新工具类
 */
@Component
public class CtrContractDataRefService {

    private static final Logger log = LoggerFactory.getLogger(CtrContractDataRefService.class);

    @Autowired
    private CtrContractDao ctrContractDao;
    @Autowired
    private CtrContractApplyDao ctrContractApplyDao;
    @Autowired
    private PmProcessDao processDao;
    @Autowired
    private IPmApproveService pmApproveService;
    @Autowired
    private IBsCompanyAccountService bsCompanyAccountService;
    @Autowired
    private IBsCompanyService bsCompanyService;
    @Autowired
    private ICtrContractRelaService ctrContractRelaService;
    @Autowired
    private ApplyInvoiceDao applyInvoiceDao;

    /**
     * 1.1 刷新 自营业务乙二醇未收票的采购合同，自动生成并完成收票审批单
     */
    @ServerTransactional
    public void refreshBuyBilledAmount(CtrContract contract) {
        this.startAndCompleteInvoiceReceive(contract);
    }

    /**
     * 1.1.1 刷新 自营业务乙二醇未收票的采购合同，自动生成并完成收票审批单
     */
    @ServerTransactional
    public void refreshBuyBilledAmount() throws Exception{
        // 查询未收票的自营采购合同
        Map<String, Object> searchParams = new HashMap<>();
        searchParams.put("NEQS_status", BasConstants.CONTRACTSTATUS_C);
        searchParams.put("NEQS_contractStatus", BasConstants.CONTRACTSTATUS_C);
        searchParams.put("LIKES_productsName", "乙二醇");
        searchParams.put("EQS_businessType", BasConstants.BUSINESS_TYPE_ZY_CG);
        searchParams.put("EQB_billFlg", false);
        Specification<CtrContract> specification = WebUtil.buildSpecification(searchParams);
        List<CtrContract> contractList = ctrContractDao.findAll(specification);
        if (CollectionUtils.isEmpty(contractList)) {
            log.info("refreshBuyBilledAmount 中止，未查询出未收票的乙二醇自营采购合同");
            return;
        }

        ExecutorService executorService = Executors.newCachedThreadPool();
        ExecutorCompletionService<String> execu = new ExecutorCompletionService<>(executorService);
        int taskSize = contractList.size();
        float bathSize = 10F;
        int bath = (int) Math.ceil((taskSize / bathSize));
        for (int i = 0; i < bath; i++) {
            int start = (int) (bathSize * i);
            int end = (int) (start + bathSize);
            end = Math.min(end, taskSize);
            List<CtrContract> syncList = contractList.subList(start, end);
            execu.submit(() -> {
                syncList.forEach(this::startAndCompleteInvoiceReceive);
                return "refreshBuyBilledAmount OK";
            });
        }
        for (int i = 0; i < bath; i++) {
            Future<String> future = execu.take();
            log.info("result:{},{}", i, future.get());
        }
        executorService.shutdown();
    }

    /**
     * 1.2 刷新 自营业务乙二醇未收票的销售合同，自动生成并完成开票审批单
     */
    @ServerTransactional
    public void refreshSellBilledAmount(CtrContract contract) {
        this.startAndCompleteInvoice(contract);
    }

    /**
     * 1.2.1 刷新 自营业务乙二醇未收票的销售合同，自动生成并完成开票审批单
     */
    @ServerTransactional
    public void refreshSellBilledAmount() throws Exception{
        // 查询未开票的自营销售合同
        Map<String, Object> searchParams = new HashMap<>();
        searchParams.put("NEQS_status", BasConstants.CONTRACTSTATUS_C);
        searchParams.put("NEQS_contractStatus", BasConstants.CONTRACTSTATUS_C);
        searchParams.put("LIKES_productsName", "乙二醇");
        searchParams.put("EQS_businessType", BasConstants.BUSINESS_TYPE_ZY_XS);
        searchParams.put("EQB_billFlg", false);
        Specification<CtrContract> specification = WebUtil.buildSpecification(searchParams);
        List<CtrContract> contractList = ctrContractDao.findAll(specification);
        if (CollectionUtils.isEmpty(contractList)) {
            log.info("refreshSellBilledAmount 中止，未查询出未开票的乙二醇自营销售合同");
            return;
        }
        ExecutorService executorService = Executors.newCachedThreadPool();
        ExecutorCompletionService<String> execu = new ExecutorCompletionService<>(executorService);
        int taskSize = contractList.size();
        float bathSize = 10F;
        int bath = (int) Math.ceil((taskSize / bathSize));
        for (int i = 0; i < bath; i++) {
            int start = (int) (bathSize * i);
            int end = (int) (start + bathSize);
            end = Math.min(end, taskSize);
            List<CtrContract> syncList = contractList.subList(start, end);
            execu.submit(() -> {
                syncList.forEach(this::startAndCompleteInvoice);
                return "refreshSellBilledAmount OK";
            });
        }
        for (int i = 0; i < bath; i++) {
            Future<String> future = execu.take();
            log.info("result:{},{}", i, future.get());
        }
        executorService.shutdown();
    }

    /**
     * 自动创建并完成收票审批单
     *
     * @param contract
     */
    public void startAndCompleteInvoiceReceive(CtrContract contract) {
        try {
            // 判断收票金额是否可发起
            CtrContractApply contractApply = ctrContractApplyDao.findByCtrContractId(contract.getId());
            if (Objects.isNull(contractApply)) {
                log.error("startAndCompleteInvoiceReceive error can't find contract, contractNo:{}", contract.getContractNo());
                return;
            }
            BigDecimal applyBillAmount = contractApply.getApplyBillAmount();
            BigDecimal totalAmount = contract.getTotalAmount();

            BigDecimal dealAmount = totalAmount.subtract(applyBillAmount);
            if (dealAmount.compareTo(BigDecimal.ZERO) <= 0) {
                log.info("startAndCompleteInvoiceReceive contractNo:{} 剩余可收票金额为0", contract.getContractNo());
                return;
            }
            // 构建收票单数据
            PmProcess process = processDao.findByProcessCodeAndEnterpriseId(BasConstants.PROCESS_APPLY_INRECEIVED, contract.getEnterpriseId());
            ApplyInvoiceReceived invoice = new ApplyInvoiceReceived();
            invoice.setId(0L);
            invoice.setContractId(contract.getId());
            invoice.setContractNo(contract.getContractNo());
            invoice.setBusinessNo(contract.getContractNo());
            invoice.setCompanyId(contract.getCompanyId());
            invoice.setCompanyName(contract.getCompanyName());
            invoice.setPayedAmount(contract.getDealedAmount());
            invoice.setTotalAmount(contract.getTotalAmount());
            invoice.setInvoiceAmount(dealAmount);
            invoice.setInInvoiceNo("");
            invoice.setInInvoiceDate(new Date());
            invoice.setInvoiceCompanyName(contract.getOurCompanyName());
            invoice.setStatus(BasConstants.APPROVE_STATUS_D);
            invoice.setEnterpriseId(contract.getEnterpriseId());
            invoice.setOurCompanyName(contract.getOurCompanyName());
            invoice.setDeptId(contract.getDeptId());

            // 设置自动发起并完成审批单
            String entityJson = JsonUtil.obj2Json(invoice);
            PmApproveSaveVo startVo = new PmApproveSaveVo();
            startVo.setBizEntityJson(entityJson);
            startVo.setProcessId(process.getId());
            startVo.setDeptId(contract.getDeptId());
            startVo.setMode(BasConstants.APPROVE_STATUS_A);
            startVo.setStatus(BasConstants.APPROVE_STATUS_D);
            startVo.setApproveId(0L);
            startVo.setUserId(contract.getMatchUserId());
            startVo.setUserName(contract.getMatchUserName());
            startVo.setEnterpriseId(contract.getEnterpriseId());
            startVo.setAutoStartMessage("化工业务，自动发起收票申请");
            startVo.setAutoStartFlgReal(true);
            pmApproveService.startFlow(startVo);
        } catch (Exception e) {
            log.info("startAndCompleteInvoiceReceive error,contractNo:{}", contract.getContractNo(), e);
        }
    }

    /**
     * 自动创建并完成开票审批单
     *
     * @param contract
     */
    public void startAndCompleteInvoice(CtrContract contract) {
        try {
            if (StringUtils.equals(BasConstants.BUSINESS_TYPE_ZY_TP,contract.getBusinessType())) {
                return;
            }
            Long contractId = contract.getId();
            CtrContractApply contractApply = ctrContractApplyDao.findByCtrContractId(contractId);
            if (Objects.isNull(contractApply)) {
                log.error("autoInitiatedInvoice error can't find contract");
                return;
            }
            PmProcess process = processDao.findByProcessCodeAndEnterpriseId(BasConstants.PROCESS_APPLY_INVOICE, contract.getEnterpriseId());
            ApplyInvoice invoice = new ApplyInvoice();
            invoice.setId(0L);
            invoice.setContractNo(contract.getContractNo());
            invoice.setInvoiceDate(new Date());
            invoice.setCompanyId(contract.getCompanyId());
            invoice.setCompanyName(contract.getCompanyName());
            invoice.setReceiveAmount(contract.getDealedAmount());
            invoice.setBillMark(BasConstants.Common_Invoice_Mark);
            BsCompany bsCompany = bsCompanyService.findByCompanyName(contract.getCompanyName());
            String companyPhone = "1";
            String address = "-";
            if (Objects.nonNull(bsCompany) && StringUtils.isNotBlank(bsCompany.getAddress())){
                address = bsCompany.getAddress();
            }
            if (Objects.nonNull(bsCompany) && StringUtils.isNotBlank(bsCompany.getCompanyPhone())){
                companyPhone = bsCompany.getCompanyPhone();
            }
            invoice.setCompanyPhone(companyPhone);
            invoice.setAddress(address);
            BsCompanyVo bsCompanyVo = new BsCompanyVo();
            bsCompanyVo.setId(contract.getCompanyId());
            bsCompanyVo.setEnterpriseId(BasConstants.ZG_ENTERPRISE_ID);
            BsCompanyAccount companyAccount = bsCompanyAccountService.findDefaultAccount(bsCompanyVo);
            if (Objects.nonNull(companyAccount)) {
                invoice.setBankAccount(companyAccount.getBankAccount());
                invoice.setBankName(companyAccount.getBankName());
                invoice.setTaxNo(companyAccount.getTaxNo());
            }

            BigDecimal applyBillAmount = Objects.isNull(contractApply.getApplyBillAmount()) ? BigDecimal.ZERO : contractApply.getApplyBillAmount();
            BigDecimal dealAmount = contract.getTotalAmount().subtract(applyBillAmount);
            if (dealAmount.compareTo(BigDecimal.ZERO) <= 0) {
                log.info("startAndCompleteInvoice 剩余可开票金额为0");
                return;
            }
            invoice.setDealAmount(dealAmount);
            invoice.setEnterpriseId(contract.getEnterpriseId());
            invoice.setContractId(contractId);
            invoice.setOurCompanyName(contract.getOurCompanyName());
            invoice.setBilledAmount(contract.getBilledAmount());
            invoice.setTotalAmount(contract.getTotalAmount());
            invoice.setApplyNo(composeContractNo(contract.getContractNo()));
            invoice.setMatchUserName(contract.getMatchUserName());
            invoice.setDeptId(contract.getDeptId());
            CtrContractRela contractRela = ctrContractRelaService.getRelaBySellContractId(contractId);
            invoice.setBuyCompanyId(Objects.nonNull(contractRela) ? contractRela.getBuyCompanyId() : 0L);
            invoice.setFileTypeId(9L);

            String entityJson = JsonUtil.obj2Json(invoice);
            PmApproveSaveVo startVo = new PmApproveSaveVo();
            startVo.setBizEntityJson(entityJson);
            startVo.setProcessId(process.getId());
            startVo.setDeptId(contract.getDeptId());
            startVo.setMode(BasConstants.APPROVE_STATUS_A);
            startVo.setStatus(BasConstants.APPROVE_STATUS_A);
            startVo.setApproveId(0L);
            startVo.setUserId(contract.getMatchUserId());
            startVo.setUserName(contract.getMatchUserName());
            startVo.setEnterpriseId(contract.getEnterpriseId());
            startVo.setAutoStartMessage("化工业务，自动发起开票申请");
            startVo.setAutoStartFlgReal(true);
            pmApproveService.startFlow(startVo);
        } catch (Exception e) {
            log.error("startAndCompleteInvoice error", e);
        }
    }

    private String composeContractNo(String contractNo) {
        List<ApplyInvoice> list = applyInvoiceDao.findByContractNo(contractNo);
        String fmt = String.format("%02d", list.size() + 1);
        return contractNo + BasConstants.APPLY_TYPE_N + fmt;
    }
}
