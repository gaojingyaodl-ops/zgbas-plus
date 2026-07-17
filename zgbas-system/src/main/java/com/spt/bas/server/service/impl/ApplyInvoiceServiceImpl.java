package com.spt.bas.server.service.impl;

import com.spt.auth.sdk.entity.SysDeptSdk;
import com.spt.auth.sdk.entity.SysUserSdk;
import com.spt.auth.sdk.open.IAuthOpenFacade;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.constant.OwnRegionEnum;
import com.spt.bas.client.entity.*;
import com.spt.bas.client.remote.IBsCompanyClient;
import com.spt.bas.client.remote.IPmApproveClient;
import com.spt.bas.client.remote.IPmProcessClient;
import com.spt.bas.client.vo.BsCompanyVo;
import com.spt.bas.client.vo.CtrContractApplyVo;
import com.spt.bas.client.vo.CtrContractChooseVo;
import com.spt.bas.client.vo.api.ApiCode;
import com.spt.bas.server.annotation.ServerTransactional;
import com.spt.bas.server.cache.BsDictUtil;
import com.spt.bas.server.ctr.service.ICtrContractUpdateService;
import com.spt.bas.server.dao.*;
import com.spt.bas.server.service.*;
import com.spt.bas.server.util.BasBusinessUtil;
import com.spt.bas.server.util.RuleUtil;
import com.spt.bas.server.util.SubjectUtil;
import com.spt.pm.dao.PmProcessDao;
import com.spt.pm.entity.PmApprove;
import com.spt.pm.entity.PmApproveStep;
import com.spt.pm.entity.PmProcess;
import com.spt.pm.inter.IPmApproveListener;
import com.spt.pm.inter.IPmEntity;
import com.spt.pm.inter.IPmService;
import com.spt.pm.service.IPmApproveService;
import com.spt.pm.vo.PmApproveCurrVo;
import com.spt.pm.vo.PmApproveSaveVo;
import com.spt.pm.vo.PmApproveWithdrawVo;
import com.spt.pm.vo.PmProcessSearchVo;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.core.exception.WebApplicationException;
import com.spt.tools.core.json.JsonUtil;
import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.service.BaseService;
import feign.Contract;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 开票
 */
@Component("applyInvoiceService")
@Transactional(readOnly = true)
public class ApplyInvoiceServiceImpl extends BaseService<ApplyInvoice> implements IApplyInvoiceService, IPmService, IPmApproveListener {
    @Autowired
    private ApplyInvoiceDao applyInvoiceDao;
    @Autowired
    private ICtrContractUpdateService contractUpdateService;
    @Autowired
    private ICtrContractService contractService;
    @Autowired
    private ICtrContractApplyService contractApplyService;
    @Autowired
    private ICtrContractOphisService contractOphisService;
    @Autowired
    private IPmApproveService pmApproveService;
    @Autowired
    private CtrContractDao ctrContractDao;
    @Autowired
    private CtrContractApplyDao ctrContractApplyDao;
    @Autowired
    private IBsCompanyAccountService bsCompanyAccountService;
    @Autowired
    private ICtrContractUpdateService ctrContractUpdateService;
    @Autowired
    private IAuthOpenFacade authOpenFacade;
    @Autowired
    private PmProcessDao processDao;
    @Autowired
    private ICtrContractRelaService ctrContractRelaService;
    @Autowired
    private IBsCompanyService bsCompanyService;
    @Autowired
    private IApplyDcsxService applyDcsxService;
    @Autowired
    private IBsCompanyDcsxService bsCompanyDcsxService;
    @Resource
    private CtrProductDao ctrProductDao;
    @Autowired
    private IPmProcessClient processClient;
    @Autowired
    private IPmApproveClient approveClient;
    @Autowired
    private ICtrContractProfitService ctrContractProfitService;
    @Autowired
    private ApplyDcsxDao applyDcsxDao;
    @Autowired
    private IBsCompanyClient companyClient;

    @Override
    public BaseDao<ApplyInvoice> getBaseDao() {
        return applyInvoiceDao;
    }

    @Override
    public Class<ApplyInvoice> getEntityClazz() {
        return ApplyInvoice.class;
    }

    @Override
    @ServerTransactional
    public void updateFileId(Long id, String fileId) {
        applyInvoiceDao.updateFileId(id, fileId);
        // 更新合同保理资料收集状态
        ApplyInvoice entity = getEntity(id);
        if (Objects.nonNull(entity)){
            ctrContractUpdateService.refreshFactorStatus(entity.getContractId());
        }
    }

    @Override
    public void doStepFlow(PmApprove approve, PmApproveStep nextStep) throws ApplicationException {
        if (approve.getStatus().equals(BasConstants.APPROVE_STATUS_D)) {
            ApplyInvoice invoice = applyInvoiceDao.findOne(approve.getBizId());
            Long contractId = invoice.getContractId();
            if (invoice.getDealAmount().compareTo(BigDecimal.ZERO) == 0) {
                throw new ApplicationException("开票金额必须大于0!");
            }
            CtrContract contract = ctrContractDao.findOne(contractId);
            contractUpdateService.addBilledAmount(contractId, invoice.getDealAmount(),invoice.getInvoiceDate(), approve.getApproveNo());
            // 更新约定付款日期
            contractUpdateService.refreshAppointPayFullTimeWithInvoice(contract, invoice);
            //更新合同开票时间
            CtrContractApply contractApply = contractApplyService.findByContractId(contractId);
            if (contractApply != null) {
                Date realBillDate = contractApply.getRealBillDate();
                Date invoiceDate = invoice.getInvoiceDate();
                if (realBillDate == null || invoiceDate.after(realBillDate)) {
                    contractApply.setRealBillDate(invoiceDate);
                    contractApplyService.save(contractApply);
                }
            }

            // 更新合同保理资料收集状态
            ctrContractUpdateService.refreshFactorStatus(contractId);
            // 针对特殊链条，上光开票完成后，自动发起中上游，中游 开票申请
            addAutoInvoice(contract.getId());
        }
    }

    private BigDecimal nullToBigDecimal(BigDecimal bd) {
        return bd == null ? BigDecimal.ZERO : bd;
    }

    /**
     * 代采赊销：下游发起开票申请，中游自动发起开票申请
     * @param approveId
     */
    private void autoApplyCenterStartFlow(Long approveId) {
        ApplyCtrDCSX dcsxCtr = applyDcsxService.findByDCSXApproveId(approveId);
        if (Objects.isNull(dcsxCtr)) {
            return;
        }
        BsCompanyDcsx myCompany = bsCompanyDcsxService.findByCompanyName(dcsxCtr.getCompanyName());
        if (Objects.isNull(myCompany)) {
            return;
        }

        Set<String> funderCompany = getFunderCompany();

        if (!Boolean.TRUE.equals(myCompany.getOurCompanyFlag()) || !funderCompany.contains(dcsxCtr.getOurCompanyName()) || BigDecimal.ZERO.compareTo(nullToBigDecimal(dcsxCtr.getBilledAmount())) != 0) {
            return;
        }

        List<ApplyInvoice> applyInvoices = applyInvoiceDao.findByContractNo(dcsxCtr.getContractNo());
        if (CollectionUtils.isNotEmpty(applyInvoices) && isApplyInvoices(applyInvoices)) {
            return;
        }

        BsCompanyDcsx bsCompanyDcsx = bsCompanyDcsxService.findByCompanyName(dcsxCtr.getOurCompanyName());
        BsCompany companyDcsx = bsCompanyService.findByCompanyName(dcsxCtr.getOurCompanyName());

        if (Objects.isNull(bsCompanyDcsx)) {
            return;
        }
        logger.info("自动发起中游开票申请");

        PmProcess process = processDao.findByProcessCodeAndEnterpriseId(BasConstants.PROCESS_CTR_DCSXINVOICE, dcsxCtr.getEnterpriseId());
        ApplyInvoice centerInvoice = new ApplyInvoice();
        centerInvoice.setId(0L);
        centerInvoice.setContractId(dcsxCtr.getId());
        centerInvoice.setContractNo(dcsxCtr.getContractNo());// 合同编号
        centerInvoice.setBusinessNo(dcsxCtr.getContractNo());
        centerInvoice.setTotalAmount(dcsxCtr.getTotalAmount());// 合同总价
        centerInvoice.setReceiveAmount(dcsxCtr.getReceiveAmount());// 已收金额
        centerInvoice.setBilledAmount(dcsxCtr.getBilledAmount());// 已开金额
        centerInvoice.setMatchUserName(dcsxCtr.getMatchUserName());// 业务员
        centerInvoice.setCompanyName(dcsxCtr.getOurCompanyName());// 发票抬头
        centerInvoice.setCompanyId(Objects.isNull(companyDcsx) ? null : companyDcsx.getId());// 供方id
        centerInvoice.setOurCompanyName(dcsxCtr.getCompanyName());// 供方
        centerInvoice.setBankName(StringUtils.isBlank(dcsxCtr.getOurBankName()) ? bsCompanyDcsx.getCompanyBankName() : dcsxCtr.getOurBankName());// 开户行:优先去代采赊销合同里的
        centerInvoice.setDealAmount(dcsxCtr.getTotalAmount());// 开票金额
        centerInvoice.setInvoiceDate(new Date());// 开票日期
        centerInvoice.setBankAccount(StringUtils.isBlank(dcsxCtr.getOurBankAccount())? bsCompanyDcsx.getCompanyCardId() : dcsxCtr.getOurBankAccount());// 银行账号:优先去代采赊销合同里的
        centerInvoice.setAddress(bsCompanyDcsx.getAddress());// 公司地址
        centerInvoice.setTaxNo(bsCompanyDcsx.getCompanyTaxNo());// 税号
        centerInvoice.setCompanyPhone(bsCompanyDcsx.getCompanyPhone());// 公司电话
        centerInvoice.setStatus(BasConstants.APPROVE_STATUS_A);
        centerInvoice.setEnterpriseId(dcsxCtr.getEnterpriseId());
        centerInvoice.setDeptId(dcsxCtr.getDeptId());

        // 设置自动发起并完成审批单
        String entityJson = JsonUtil.obj2Json(centerInvoice);
        PmApproveSaveVo startVo = new PmApproveSaveVo();
        startVo.setAutoStartMessage("发起下游开票流程，自动发起代采赊销开票");
        startVo.setBizEntityJson(entityJson);
        startVo.setProcessId(process.getId());
        startVo.setDeptId(dcsxCtr.getDeptId());
        startVo.setMode(BasConstants.APPROVE_STATUS_A);
        startVo.setStatus(BasConstants.APPROVE_STATUS_A);
        startVo.setApproveId(0L);
        startVo.setUserId(dcsxCtr.getMatchUserId());
        startVo.setUserName(dcsxCtr.getMatchUserName());
        startVo.setEnterpriseId(dcsxCtr.getEnterpriseId());
        startVo.setAutoStartFlgReal(true);
        try {
            pmApproveService.startFlow(startVo);
        } catch (Exception e) {
            logger.error("自动发起中游开票申请错误 autoApplyCenterStartFlow error contractNo:{}", dcsxCtr.getContractNo(), e);
        }
    }

    private Set<String> getFunderCompany() {
        HashSet<String> result = new HashSet<>();
        result.add(BasConstants.COMPANY_NAME_ASY);
        result.add(BasConstants.COMPANY_NAME_SUGX);
        result.add(BasConstants.COMPANY_NAME_SDNH);
        return result;
    }

    private static boolean isApplyInvoices(List<ApplyInvoice> applyInvoices) {
        return applyInvoices.stream()
                .anyMatch(applyInvoice -> StringUtils.equals(applyInvoice.getStatus(), BasConstants.APPROVE_STATUS_D) || StringUtils.equals(applyInvoice.getStatus(), BasConstants.APPROVE_STATUS_A));
    }

    @Override
    public void doWithdraw(PmApproveWithdrawVo vo) throws ApplicationException {
        // 作废开票单 扣除已开票金额 添加操作记录
        ApplyInvoice invoice = applyInvoiceDao.findOne(vo.getBizId());
        PmApprove approve = pmApproveService.getEntity(invoice.getApproveId());
        Long contractId = invoice.getContractId();
        if (StringUtils.equals(BasConstants.APPROVE_STATUS_D, invoice.getStatus())) {
            contractUpdateService.addBilledAmount(contractId, invoice.getDealAmount().negate(),invoice.getInvoiceDate(), approve.getApproveNo());
        }
        rollbackContractApply(invoice);
    }


    @Override
    public IPmEntity saveEntity(IPmEntity pmEntity) throws ApplicationException {
        if (pmEntity != null) {
            ApplyInvoice entity = (ApplyInvoice) pmEntity;
            if (entity.getId() == 0) {
                entity.setApplyNo(composeContractNo(entity.getContractNo()));
            }
            // 付款单设置businessType 作为流程条件内容
            CtrContract ctr = contractService.getEntity(entity.getContractId());
            if (StringUtils.equals(ctr.getBusinessTypeDcsx(), BasConstants.BUSINESS_TYPE_DCSX)) {
                entity.setBusinessType(BasConstants.BUSINESS_TYPE_DCSX);
            }
            SysDeptSdk sysDeptSdk = authOpenFacade.findDeptById(ctr.getDeptId());
            entity.setDeptId(ctr.getDeptId());
            entity.setOwnRegion(Objects.nonNull(sysDeptSdk) && Objects.nonNull(OwnRegionEnum.getRegionEnumByName(sysDeptSdk.getDeptName()))
                    ? Objects.requireNonNull(OwnRegionEnum.getRegionEnumByName(sysDeptSdk.getDeptName())).getRegionCode()
                    : "");
            logger.info("applyInvoiceServiceImpl.saveEntity:{}", JsonUtil.obj2Json(ctr));
            if (StringUtils.equals(BasConstants.CONTRACT_TYPE_S, ctr.getContractType())) {
                CtrContract buyContract = ctrContractDao.findByApproveIdAndContractType(ctr.getApproveId(), BasConstants.CONTRACT_TYPE_B);
                Boolean commonBillFlg = commonBillFlg(buyContract, ctr);
                entity.setBillMark(Boolean.TRUE.equals(commonBillFlg) ? BasConstants.Common_Invoice_Mark : BasConstants.Special_Invoice_Mark);
            }
            return save(entity);
        }
        return null;
    }

    @Override
    public Map<String, Object> buildConditionDefaultMap(IPmEntity pmEntity) {
        ApplyInvoice entity = (ApplyInvoice) pmEntity;
        CtrContract contract = ctrContractDao.findOne(entity.getContractId());
        return BasBusinessUtil.buildConditionDefaultMap(contract);
    }

    private String composeContractNo(String contractNo) {
        List<ApplyInvoice> list = applyInvoiceDao.findByContractNo(contractNo);
        String fmt = String.format("%02d", list.size() + 1);
        return contractNo + BasConstants.APPLY_TYPE_N + fmt;
    }

    @Override
    public String getSubject(IPmEntity pmEntity, PmProcess process) {
        String subject = "";
        if (pmEntity != null) {
            ApplyInvoice entity = (ApplyInvoice) pmEntity;
            String billMark = entity.getBillMark();
            CtrContract contract = ctrContractDao.findOne(entity.getContractId());
            String receiptArrived = (Objects.nonNull(contract) && contract.getReceiptArrivedFlg()) ? "[货到票到]" : "";
            String companyName1 = RuleUtil.companyNameSubString(entity.getCompanyName());
            String companyName2 = RuleUtil.companyNameSubString(entity.getOurCompanyName());
            if (BasConstants.Special_Invoice_Mark.equals(billMark)) {
                subject = SubjectUtil.formatSubject(receiptArrived, "[特殊]" + entity.getContractNo(), companyName1, SubjectUtil.formatMoney(entity.getDealAmount(), RuleUtil.monetaryUnit), companyName2);
            } else {
                subject = SubjectUtil.formatSubject(receiptArrived, entity.getContractNo(), companyName1, SubjectUtil.formatMoney(entity.getDealAmount(), RuleUtil.monetaryUnit), companyName2);
            }
        }
        return subject;
    }

    @Override
    @ServerTransactional
    public void doStepIn(PmApprove approve) throws ApplicationException {
        ApplyInvoice entity = applyInvoiceDao.findOne(approve.getBizId());
        List<ApplyInvoice> invoiceList = applyInvoiceDao.findByContractId(approve.getContractId());
        CtrContractChooseVo byContractAmount = contractService.findByContractId(approve.getContractId());
        BigDecimal finalTotalAmount = byContractAmount.getFinalTotalAmount();
        BigDecimal totalAmount = byContractAmount.getTotalAmount();
        totalAmount = Objects.nonNull(finalTotalAmount) && finalTotalAmount.compareTo(BigDecimal.ZERO) > 0 ? finalTotalAmount : totalAmount;
        BigDecimal breachAmount = byContractAmount.getBreachAmount();
        BigDecimal receiveAmount = BigDecimal.ZERO;
        if(CollectionUtils.isNotEmpty(invoiceList)){
            for (ApplyInvoice applyInvoice : invoiceList) {
                String status = applyInvoice.getStatus();
                Long invoiceId = applyInvoice.getId();
                if (!Objects.equals(invoiceId, entity.getId()) && (StringUtils.equals("D", status) || StringUtils.equals("A", status))){
                    receiveAmount=receiveAmount.add(applyInvoice.getDealAmount());
                }
            }
            if(totalAmount.add(breachAmount).compareTo(receiveAmount)<=0){
               throw new ApplicationException("已有审批在进行中，不能重复发起审批" );
            }
        }
        //更新CtrContractApply中数据
        CtrContractApplyVo vo = new CtrContractApplyVo();
        vo.setContractId(entity.getContractId());
        vo.setDealAmount(entity.getDealAmount());
        vo.setApplyType(BasConstants.APPLY_TYPE_N);
        contractApplyService.updateCtrContractApply(vo);
        contractOphisService.addHis(BasConstants.APPLY_TYPE_N, entity.getContractId(), approve, entity.getInvoiceDate());
        //判断开票开户行是否存在
        verifyCompanyAccount(entity);

        BsDictData bsDictData = BsDictUtil.getBsDictData(BasConstants.AUTO_APPLY_CENTER_SWITCH, BasConstants.SWITCH);
        if (bsDictData == null || StringUtils.equals("0", bsDictData.getDictName())) {
            return;
        }
        // 如果是代采赊销，自动发起中游收票
        autoApplyCenterStartFlow(byContractAmount.getApproveId());
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
        //更新CtrContractApply中数据
        CtrContractApplyVo vo = new CtrContractApplyVo();
        vo.setContractId(entity.getContractId());
        vo.setDealAmount(entity.getDealAmount().negate());
        vo.setApplyType(BasConstants.APPLY_TYPE_N);
        contractApplyService.updateCtrContractApply(vo);
    }

    private void rollbackContractApply(ApplyInvoice entity) throws ApplicationException {
        //更新CtrContractApply中数据
        CtrContractApplyVo vo = new CtrContractApplyVo();
        vo.setContractId(entity.getContractId());
        vo.setDealAmount(entity.getDealAmount().negate());
        vo.setApplyType(BasConstants.APPLY_TYPE_N);
        String status = entity.getStatus();
        if (StringUtils.equals(BasConstants.APPROVE_STATUS_C, status)) {
            Date findLastBill = applyInvoiceDao.findMaxBillDate(entity.getContractId());
            vo.setRealDate(findLastBill);
        }
        contractApplyService.updateCtrContractApply(vo);
    }

    @Override
    public List<ApplyInvoice> findByContractId(Long contractId) {
        return applyInvoiceDao.findByContractId(contractId);
    }

    /**
     * 自动发起开票申请
     *
     * @param contractId
     */
    @Override
    @ServerTransactional
    public void autoInitiatedInvoice(Long contractId) {
        try {
            CtrContract contract = ctrContractDao.findOne(contractId);
            CtrContractApply contractApply = ctrContractApplyDao.findByCtrContractId(contractId);
            if (Objects.isNull(contract) || Objects.isNull(contractApply)) {
                logger.error("autoInitiatedInvoice error can't find contract");
                return;
            }
            ApplyInvoice invoice = new ApplyInvoice();
            CtrProduct ctrProduct = ctrProductDao.findOneByCtrContractId(contractId);
            if (Objects.nonNull(ctrProduct)){
                invoice.setProductName(ctrProduct.getProductName());
                invoice.setProductCd(ctrProduct.getProductCd());
                invoice.setBrandNumber(ctrProduct.getBrandNumber());
                invoice.setDealNumber(ctrProduct.getDealNumber());
                invoice.setDealPrice(ctrProduct.getDealPrice());
            }
            PmProcess process = processDao.findByProcessCodeAndEnterpriseId(BasConstants.PROCESS_APPLY_INVOICE, contract.getEnterpriseId());
            invoice.setId(0L);
            invoice.setContractNo(contract.getContractNo());
            invoice.setInvoiceDate(new Date());
            invoice.setCompanyId(contract.getCompanyId());
            invoice.setCompanyName(contract.getCompanyName());
            invoice.setReceiveAmount(contract.getDealedAmount());
            BsCompanyVo bsCompanyVo = new BsCompanyVo();
            bsCompanyVo.setId(contract.getCompanyId());
            bsCompanyVo.setEnterpriseId(BasConstants.ZG_ENTERPRISE_ID);
            BsCompanyAccount companyAccount = bsCompanyAccountService.findDefaultAccount(bsCompanyVo);
            if (Objects.nonNull(companyAccount)) {
                invoice.setBankAccount(companyAccount.getBankAccount());
                invoice.setBankName(companyAccount.getBankName());
                invoice.setTaxNo(companyAccount.getTaxNo());
            }

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

            BigDecimal applyBillAmount = Objects.isNull(contractApply.getApplyBillAmount()) ? BigDecimal.ZERO : contractApply.getApplyBillAmount();
//            // 确认收货数量
//            BigDecimal confirmReceiveNumber = contract.getConfirmReceiveNumber().setScale(5, RoundingMode.HALF_UP);
//            // 销售平均价
//            BigDecimal sellPrice = contract.getTotalAmount().divide(contract.getTotalNumber(), 5, RoundingMode.HALF_UP);
//            // 已确认收货金额
//            BigDecimal confirmReceiveAmount = confirmReceiveNumber.multiply(sellPrice).setScale(2, RoundingMode.HALF_UP);
//            logger.info("已确认收货数量 confirmReceiveNumber:{}", confirmReceiveNumber);
//            logger.info("已确认收货金额 confirmReceiveAmount:{}", confirmReceiveAmount);
//
//            // 可开票金额
//            logger.info("可开票金额 needTotalBillAmount:{}", confirmReceiveAmount);
            BigDecimal applyMaxAmount = contract.getTotalAmount().subtract(applyBillAmount);
            if (applyMaxAmount.compareTo(BigDecimal.ZERO) <= 0){
                logger.info("autoInitiatedInvoice 剩余可开票金额为0");
                return;
            }
//            if (applyMaxAmount.compareTo(confirmReceiveAmount) <= 0){
//                confirmReceiveAmount = applyMaxAmount;
//            }
            invoice.setDealAmount(applyMaxAmount);
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
            startVo.setMode("A");
            startVo.setStatus(BasConstants.APPROVE_STATUS_A);
            startVo.setApproveId(0L);
            startVo.setUserId(contract.getMatchUserId());
            startVo.setUserName(contract.getMatchUserName());
            startVo.setEnterpriseId(contract.getEnterpriseId());
            startVo.setAutoStartMessage("自动发起开票申请");
            pmApproveService.startFlow(startVo);
        } catch (Exception e) {
            logger.error("autoInitiatedInvoice error", e);
        }
    }

    @Override
    public Date findMaxBillDate(Long contractId) {
        return applyInvoiceDao.findMaxBillDate(contractId);
    }


    private Boolean commonBillFlg(CtrContract buyContract, CtrContract sellContract) {
        if (Objects.isNull(buyContract) || Objects.isNull(sellContract)) {
            return false;
        }
        // 采购合同已收票
        BigDecimal billAmount = buyContract.getBilledAmount();
        boolean flg1 = buyContract.getTotalAmount().compareTo(billAmount) <= 0;
        // 销售合同已发货
        BigDecimal warehouseNum = sellContract.getWarehouseNumber();
        boolean flg2 = sellContract.getTotalNumber().compareTo(warehouseNum) <= 0;
        // 销售合同已收款
        BigDecimal dealedAmount = sellContract.getDealedAmount();
        boolean flg3 = sellContract.getTotalAmount().compareTo(dealedAmount) <= 0;
        return flg1 && flg2 && flg3;
    }

    /**
     * 发起中上游，中游 开票申请
     *
     * @param contractId
     */
    @ServerTransactional
    public void addAutoInvoice(Long contractId) {
        CtrContract contractS = ctrContractDao.findOne(contractId);
        // 判断是否是特殊链条 上游-青光-苏高新-上光-下游
        List<CtrContract> ctrContractList = ctrContractDao.findByApproveId(contractS.getApproveId());
        Optional<CtrContract> specialChainFlg = ctrContractList.stream().filter(CtrContract::getSpecialChainFlag).findAny();
        if (specialChainFlg.isPresent() && contractS.getOurCompanyName().equals(BasConstants.COMPANY_NAME_SHZG)) {
            // 判断是否全部开票完成
            if (contractS.getBilledAmount().compareTo(contractS.getTotalAmount()) >= 0) {
                // 获取所有关联的合同数据
                List<CtrContractProfit> ctrContractProfitList = ctrContractProfitService.findByAndApproveId(contractS.getApproveId());
                // 转化为map,key 为 buycontractNo
                Map<String, String> profitMap = ctrContractProfitList.stream()
                        .collect(Collectors.toMap(CtrContractProfit::getBuyContractNo, CtrContractProfit::getSellContractNo));
                CtrContract contractB1 = specialChainFlg.get();
                String contrcatNoX = profitMap.get(contractB1.getContractNo());
                ApplyCtrDCSX contractX = applyDcsxDao.findByContractNo(contrcatNoX);
                // 自动发起开票申请
                this.autoApplyInvoice(contractB1);
                this.autoApplyDcsxInvoice(contractX);
            }
        }
    }

    public void autoApplyInvoice(CtrContract contract) {
        ApplyInvoice applyInvoice = new ApplyInvoice();
        CtrContractApply contractApply = ctrContractApplyDao.findByCtrContractId(contract.getId());
        if (Objects.isNull(contractApply)) {
            logger.error("autoInitiatedInvoice error can't find contract");
            return;
        }
        CtrProduct ctrProduct = ctrProductDao.findOneByCtrContractId(contract.getId());
        if (Objects.nonNull(ctrProduct)){
            applyInvoice.setProductName(ctrProduct.getProductName());
            applyInvoice.setProductCd(ctrProduct.getProductCd());
            applyInvoice.setBrandNumber(ctrProduct.getBrandNumber());
            applyInvoice.setDealNumber(ctrProduct.getDealNumber());
            applyInvoice.setDealPrice(ctrProduct.getDealPrice());
        }
        applyInvoice.setId(0L);
        applyInvoice.setContractId(contract.getId());
        applyInvoice.setContractNo(contract.getContractNo());
        applyInvoice.setInvoiceDate(new Date());
        applyInvoice.setCompanyId(contract.getCompanyId());
        applyInvoice.setCompanyName(contract.getOurCompanyName());
        applyInvoice.setReceiveAmount(contract.getDealedAmount());
        // 获取银行信息
        BsCompanyDcsx companyConfig = bsCompanyDcsxService.findByCompanyName(applyInvoice.getCompanyName());
        if (Objects.nonNull(companyConfig)) {
            applyInvoice.setBankName(companyConfig.getCompanyBankName());
            applyInvoice.setBankAccount(companyConfig.getCompanyCardId());
            applyInvoice.setTaxNo(companyConfig.getCompanyTaxNo());
            applyInvoice.setCompanyPhone(companyConfig.getCompanyPhone());
            applyInvoice.setAddress(companyConfig.getAddress());
        } else {
            BsCompany byCompanyName1 = companyClient.findByCompanyName(applyInvoice.getCompanyName());
            applyInvoice.setBankName(byCompanyName1.getBankName()==null ? "" : byCompanyName1.getBankName());
            applyInvoice.setBankAccount(byCompanyName1.getBankAccount()==null ? "" :byCompanyName1.getBankAccount() );
            applyInvoice.setTaxNo(byCompanyName1.getTaxNo()==null ? "" : byCompanyName1.getTaxNo());
            applyInvoice.setCompanyPhone(byCompanyName1.getCompanyPhone()==null?"":byCompanyName1.getCompanyPhone());
            applyInvoice.setAddress(byCompanyName1.getAddress()==null?"":byCompanyName1.getAddress());
        }

        BigDecimal applyBillAmount = Objects.isNull(contractApply.getApplyBillAmount()) ? BigDecimal.ZERO : contractApply.getApplyBillAmount();
        BigDecimal applyMaxAmount = contract.getTotalAmount().subtract(applyBillAmount);
        if (applyMaxAmount.compareTo(BigDecimal.ZERO) <= 0){
            logger.error("autoApplyInvoice 剩余可开票金额为0");
            return;
        }

        applyInvoice.setDealAmount(applyMaxAmount);
        applyInvoice.setEnterpriseId(contract.getEnterpriseId());
        applyInvoice.setOurCompanyName(contract.getCompanyName());
        applyInvoice.setBilledAmount(contract.getBilledAmount());
        applyInvoice.setTotalAmount(contract.getTotalAmount());
        applyInvoice.setApplyNo(composeContractNo(contract.getContractNo()));
        applyInvoice.setMatchUserName(contract.getMatchUserName());
        applyInvoice.setDeptId(contract.getDeptId());
        CtrContractRela contractRela = ctrContractRelaService.getRelaBySellContractId(contract.getId());
        applyInvoice.setBuyCompanyId(Objects.nonNull(contractRela) ? contractRela.getBuyCompanyId() : 0L);
        applyInvoice.setFileTypeId(9L);
        applyInvoice.setStatus(BasConstants.APPROVE_STATUS_A);
        // 自动发起付款流程
        try {
            this.startFlow(JsonUtil.obj2Json(applyInvoice), BasConstants.PROCESS_CTR_INVOICE, contract.getMatchUserId());
        } catch (Exception e) {
            logger.error("autoApplyInvoice error", e);
        }
    }

    public void autoApplyDcsxInvoice(ApplyCtrDCSX contract) {
        ApplyInvoice applyInvoice = new ApplyInvoice();
        applyInvoice.setId(0L);
        applyInvoice.setContractId(contract.getId());
        applyInvoice.setContractNo(contract.getContractNo());
        applyInvoice.setTotalAmount(contract.getTotalAmount());
        applyInvoice.setReceiveAmount(contract.getDealedAmount());
        applyInvoice.setBilledAmount(contract.getBilledAmount());
        applyInvoice.setMatchUserName(contract.getMatchUserName());
        applyInvoice.setDeptId(contract.getDeptId());
        applyInvoice.setOurCompanyName(contract.getCompanyName());
        applyInvoice.setCompanyId(null);
        applyInvoice.setCompanyName(contract.getOurCompanyName());
        BsCompanyDcsx companyConfig = bsCompanyDcsxService.findByCompanyName(applyInvoice.getCompanyName());
        if (Objects.nonNull(companyConfig)) {
            applyInvoice.setBankName(companyConfig.getCompanyBankName());
            applyInvoice.setBankAccount(companyConfig.getCompanyCardId());
            applyInvoice.setTaxNo(companyConfig.getCompanyTaxNo());
            applyInvoice.setAddress(companyConfig.getAddress());
            applyInvoice.setCompanyPhone(companyConfig.getCompanyPhone());
        } else {
            applyInvoice.setBankName(null);
            applyInvoice.setBankAccount(null);
        }
        applyInvoice.setInvoiceDate(new Date());
        applyInvoice.setStatus(BasConstants.APPROVE_STATUS_A);
        List<ApplyInvoice> invoiceList = applyInvoiceDao.findByContractNo(contract.getContractNo());
        BigDecimal totalAmount = contract.getTotalAmount();
        if (CollectionUtils.isNotEmpty(invoiceList)) {
            BigDecimal receiveAmount = invoiceList.stream()
                    .filter(i -> (StringUtils.equals("D", i.getStatus()) || StringUtils.equals("A", i.getStatus())))
                    .map(ApplyInvoice::getDealAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal currAmount = totalAmount.subtract(receiveAmount);
            if (currAmount.compareTo(BigDecimal.ZERO) <= 0) {
                logger.error("autoApplyDcsxInvoice 剩余可开票金额为0");
               return;
            } else {
                applyInvoice.setDealAmount(currAmount);
            }
        } else {
            applyInvoice.setDealAmount(totalAmount);
        }
        // 自动发起付款流程
        try {
            this.startFlow(JsonUtil.obj2Json(applyInvoice), BasConstants.PROCESS_CTR_DCSXINVOICE, contract.getMatchUserId());
        } catch (Exception e) {
            logger.error("autoApplyDcsxInvoice error", e);
        }
    }

    /**
     * 发起审批
     */
    private void startFlow(String bizEntityJson, String processCode, Long applyUserId) throws ApplicationException, WebApplicationException {
        PmApproveSaveVo startVo = new PmApproveSaveVo();
        startVo.setMode(BasConstants.APPROVE_STATUS_A);
        startVo.setStatus(BasConstants.APPROVE_STATUS_A);
        startVo.setEnterpriseId(BasConstants.ZG_ENTERPRISE_ID);
        PmProcessSearchVo searchVo = new PmProcessSearchVo();
        searchVo.setEnterpriseId(BasConstants.ZG_ENTERPRISE_ID);
        searchVo.setProcessCode(processCode);
        PmProcess process = processClient.findByProcessCode(searchVo);
        if (process == null) {
            throw new ApplicationException(ApiCode.ERROR_PROCESS_NOTFOUND, "找不到流程记录");
        }
        SysUserSdk userById = authOpenFacade.findUserById(applyUserId);
        startVo.setUserId(userById.getUserId());
        startVo.setDeptId(userById.getDeptId());
        startVo.setUserName(userById.getNickName());
        startVo.setProcessId(process.getId());
        startVo.setApproveId(0L);
        startVo.setBizEntityJson(bizEntityJson);
        startVo.setAutoStartFlg(true);
        startVo.setAutoStartMessage("自动发起开票申请");
        approveClient.startFlow(startVo);
    }
}

