package com.spt.bas.server.service.impl;


import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.*;
import com.spt.bas.client.vo.CtrContractApplyVo;
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
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.core.json.JsonUtil;
import com.spt.tools.data.annotation.ServiceTransactional;
import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.service.BaseService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 收票
 */
@Component("applyInvoiceReceivedService")
@Transactional(readOnly = true)
public class ApplyInvoiceReceivedServiceImpl extends BaseService<ApplyInvoiceReceived> implements IApplyInvoiceReceivedService, IPmService, IPmApproveListener {
    @Autowired
    private ApplyInvoiceReceivedDao applyInvoiceReceivedDao;
    @Autowired
    private ICtrContractUpdateService ctrContractUpdateService;
    @Autowired
    private ICtrContractService ctrContractService;
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
    private IBsCompanyDcsxService bsCompanyDcsxService;
    @Autowired
    private PmProcessDao processDao;
    @Autowired
    private IApplyDcsxService applyDcsxService;
    @Autowired
    private ApplyInvoiceDao applyInvoiceDao;
    @Autowired
    private IBsCompanyService bsCompanyService;
    @Autowired
    private ApplyInvoiceServiceImpl applyInvoiceService;
    @Autowired
    private ICtrContractProfitService ctrContractProfitService;
    @Autowired
    private ApplyDcsxDao applyDcsxDao;

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
            ctrContractUpdateService.addBilledAmount(contractId, invoice.getInvoiceAmount(),invoice.getInInvoiceDate(), approve.getApproveNo());
            //更新合同收票时间
            CtrContractApply contractApply = contractApplyService.findByContractId(contractId);
            Date realBillDate = contractApply.getRealBillDate();
            Date inInvoiceDate = invoice.getInInvoiceDate();
            if (realBillDate == null || inInvoiceDate.after(realBillDate)) {
                contractApply.setRealBillDate(inInvoiceDate);
                contractApplyService.save(contractApply);
            }

            BsDictData bsDictData = BsDictUtil.getBsDictData(BasConstants.AUTO_APPLY_CENTER_SWITCH, BasConstants.SWITCH);
            if (bsDictData == null || StringUtils.equals("0", bsDictData.getDictName())) {
                return;
            }
            // 上游收票流程完结后，自动发起中游开票
            CtrContract contract = ctrContractDao.findOne(contractId);
            if (contract.getTotalAmount().compareTo(nullToBigDecimal(invoice.getInvoiceAmount())) == 0) {
                // 如果是代采赊销，自动发起中游收票
                autoApplyCenterStartFlow(contract.getApproveId());
            }
            // 针对特殊链条，上光开票完成后，自动发起中上游，中游 开票申请
            addAutoInvoice(contract.getId());
        }
    }

    private BigDecimal nullToBigDecimal(BigDecimal bd) {
        return bd == null ? BigDecimal.ZERO : bd;
    }

    private static boolean isApplyInvoices(List<ApplyInvoice> applyInvoices) {
        return applyInvoices.stream().anyMatch(applyInvoice -> StringUtils.equals(applyInvoice.getStatus(), BasConstants.APPROVE_STATUS_D) || StringUtils.equals(applyInvoice.getStatus(), BasConstants.APPROVE_STATUS_A));
    }

    /**
     * 如果是代采赊销订单，上游收票流程完成后，自动发起中游收票
     * @param approve 审批
     */
    private void autoApplyCenterStartFlow(Long approveId) {
        ApplyCtrDCSX dcsxCtr = applyDcsxService.findByDCSXApproveId(approveId);
        if (Objects.isNull(dcsxCtr)) {
            return;
        }
        BsCompanyDcsx myCompany = bsCompanyDcsxService.findByCompanyName(dcsxCtr.getCompanyName());

        if (!Boolean.TRUE.equals(myCompany.getOurCompanyFlag()) || !StringUtils.equals(dcsxCtr.getOurCompanyName(), BasConstants.COMPANY_NAME_ASY) || BigDecimal.ZERO.compareTo(nullToBigDecimal(dcsxCtr.getBilledAmount())) != 0) {
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
        startVo.setAutoStartMessage("上游收票完成，自动发起代采赊销开票");
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
            logger.error("上游收票完结后自动发起中游开票 autoApplyCenterStartFlow error contractNo:{}", dcsxCtr.getContractNo(), e);
        }
    }

    @Override
    public void doWithdraw(PmApproveWithdrawVo vo) throws ApplicationException {
        // 作废收票单 扣除已收票金额 添加操作记录
        ApplyInvoiceReceived received = applyInvoiceReceivedDao.findOne(vo.getBizId());
        PmApprove approve = pmApproveService.getEntity(received.getApproveId());
        Long contractId = received.getContractId();
        if (StringUtils.equals(BasConstants.APPROVE_STATUS_D, received.getStatus())) {
            ctrContractUpdateService.addBilledAmount(contractId, received.getInvoiceAmount().negate(),received.getInInvoiceDate(), approve.getApproveNo());
        }
        rollbackContractApply(received);
    }

    @Override
    public IPmEntity saveEntity(IPmEntity pmEntity) throws ApplicationException {
        if (pmEntity != null) {
            ApplyInvoiceReceived entity = (ApplyInvoiceReceived) pmEntity;

            CtrContract ctr = ctrContractService.getEntity(entity.getContractId());
            logger.info("applyInvoiceReceivedServiceImpl.saveEntity:{}", JsonUtil.obj2Json(ctr));
            // 付款单设置businessType 作为流程条件内容
            if (StringUtils.equals(BasConstants.BUSINESS_TYPE_DCSX, ctr.getBusinessTypeDcsx())) {
                entity.setBusinessType(BasConstants.BUSINESS_TYPE_DCSX);
            }
            entity.setDeptId(ctr.getDeptId());
            entity.setOurCompanyName(entity.getInvoiceCompanyName());

            return save(entity);
        }
        return null;
    }

    @Override
    public Map<String, Object> buildConditionDefaultMap(IPmEntity pmEntity) {
        ApplyInvoiceReceived entity = (ApplyInvoiceReceived) pmEntity;
        CtrContract contract = ctrContractDao.findOne(entity.getContractId());
        return BasBusinessUtil.buildConditionDefaultMap(contract);
    }

    @Override
    public String getSubject(IPmEntity pmEntity, PmProcess process) {
        if (pmEntity != null) {
            ApplyInvoiceReceived entity = (ApplyInvoiceReceived) pmEntity;
            String companyName1 = RuleUtil.companyNameSubString(entity.getInvoiceCompanyName());
            String companyName2 = RuleUtil.companyNameSubString(entity.getCompanyName());
            String company = "";
            if (StringUtils.isNotBlank(companyName1) && StringUtils.isNotBlank(companyName2)) {
                company = companyName2 + "-" + companyName1;
            }
            return SubjectUtil.formatSubject(entity.getContractNo(), company, SubjectUtil.formatMoney(entity.getInvoiceAmount(), RuleUtil.monetaryUnit));
        }
        return null;
    }

    @Override
    @ServerTransactional
    public void doStepIn(PmApprove approve) throws ApplicationException {
        ApplyInvoiceReceived entity = applyInvoiceReceivedDao.findOne(approve.getBizId());
        CtrContractApply contractApply = ctrContractApplyDao.findByCtrContractId(entity.getContractId());
        CtrContract ctrContract = ctrContractDao.findOne(entity.getContractId());
        // amount = 本次开票金额 + 合同已开金额
        BigDecimal amount = entity.getInvoiceAmount().add(contractApply.getApplyBillAmount());
        // 合同总金额 - 本次开票金额 - 合同已开金额
        BigDecimal finalTotalAmount = ctrContract.getFinalTotalAmount();
        BigDecimal totalAmount = (Objects.nonNull(finalTotalAmount) && finalTotalAmount.compareTo(BigDecimal.ZERO) > 0)
                ? finalTotalAmount
                : ctrContract.getTotalAmount();
        BigDecimal subtract = totalAmount.subtract(amount);
        // 剩余可收票金额 = 合同总金额 - 合同已开金额
        BigDecimal sperAmount = totalAmount.subtract(contractApply.getApplyBillAmount());
        if (entity.getInvoiceAmount().compareTo(BigDecimal.ZERO) == 0) {
            throw new ApplicationException("收票金额必须大于0!");
        }
        ///测试暂时注释 剩余可收票金额为0
        if (subtract.compareTo(BigDecimal.ZERO) < 0) {
            throw new ApplicationException("收票金额有误,剩余可收票金额为：" + sperAmount);
        }
        //更新CtrContractApply中数据
        CtrContractApplyVo vo = new CtrContractApplyVo();
        vo.setContractId(entity.getContractId());
        vo.setDealAmount(entity.getInvoiceAmount());
        vo.setApplyType(BasConstants.APPLY_TYPE_V);
        contractApplyService.updateCtrContractApply(vo);
        contractOphisService.addHis(BasConstants.APPLY_TYPE_V, entity.getContractId(), approve, entity.getInInvoiceDate());
    }

    @Override
    @ServerTransactional
    public void doStepBack(PmApproveCurrVo approve, PmApproveStep nextStep) throws ApplicationException {
        ApplyInvoiceReceived entity = applyInvoiceReceivedDao.findOne(approve.getBizId());
        //更新CtrContractApply中数据
        CtrContractApplyVo vo = new CtrContractApplyVo();
        vo.setContractId(entity.getContractId());
        vo.setDealAmount(entity.getInvoiceAmount().negate());
        vo.setApplyType(BasConstants.APPLY_TYPE_V);
        contractApplyService.updateCtrContractApply(vo);
    }

    private void rollbackContractApply(ApplyInvoiceReceived entity) throws ApplicationException {
        //更新CtrContractApply中数据
        CtrContractApplyVo vo = new CtrContractApplyVo();
        vo.setContractId(entity.getContractId());
        vo.setDealAmount(entity.getInvoiceAmount().negate());
        vo.setApplyType(BasConstants.APPLY_TYPE_V);
        String status = entity.getStatus();
        if (StringUtils.equals(BasConstants.APPROVE_STATUS_C, status)) {
            Date findLastBill = applyInvoiceReceivedDao.findLastBill(entity.getContractId());
            vo.setRealDate(findLastBill);
        }
        contractApplyService.updateCtrContractApply(vo);
    }

    @Override
    public List<ApplyInvoiceReceived> findByContractId(Long contractId) {
        return applyInvoiceReceivedDao.findByContractId(contractId);
    }

    /**
     * 如果上游企业不是我方企业、奥顺宇、塑融云，在上游付款审批完成后，自动发起并完成收票申请
     * 自动发起收票申请并完成
     *
     * @param contract
     * @param dealAmount
     */
    @Override
    @ServiceTransactional
    public void autoInitiatedCompleteInvoiceReceived(CtrContract contract, BigDecimal dealAmount) {
        try {

            if(contract.getDealedAmount().compareTo(contract.getTotalAmount())<0){
                return;
            }

            String status = "A";
            // 判断该合同是否为赊销合同
            if (Boolean.FALSE.equals(contract.getMatchCreditFlg())){
                return;
            }

            if(StringUtils.equals(contract.getBusinessTypeDcsx(),"DCSX")){
                List<String> stringList=new ArrayList<>();
                List<String> stringList2=new ArrayList<>();
                bsCompanyDcsxService.findAll().stream().filter(ss->ss.getApproverFlag()!=null && ss.getApproverFlag()==true).forEach(c->{
                    stringList.add(c.getCompanyName());
                });
                bsCompanyDcsxService.findAll().stream().filter(ss-> ss.getApproverFlag()==null|| ss.getApproverFlag()==false).forEach(c->{
                    stringList2.add(c.getCompanyName());
                });
                ApplyCtrDCSX byDCSXApproveId = applyDcsxService.findByDCSXApproveId(contract.getApproveId());
                if(stringList.contains(byDCSXApproveId.getCompanyName())){
                    status="A";
                }
                if(stringList2.contains(byDCSXApproveId.getCompanyName())){
                    status="D";

                }
            }

            // 判断收票金额是否可发起
            CtrContractApply contractApply = ctrContractApplyDao.findByCtrContractId(contract.getId());
            if (Objects.isNull(contractApply)) {
                logger.error("autoInitiatedCompleteInvoiceReceived error can't find contract, contractNo:{}", contract.getContractNo());
                return;
            }
            BigDecimal applyBillAmount = contractApply.getApplyBillAmount();
            BigDecimal totalAmount = contract.getTotalAmount();
            if ((totalAmount.subtract(applyBillAmount).subtract(dealAmount)).compareTo(BigDecimal.ZERO) < 0) {
                logger.info("autoInitiatedInvoice contractNo:{} 剩余可收票金额为0", contract.getContractNo());
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
            invoice.setInvoiceAmount(contract.getDealedAmount());
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
            if(StringUtils.equals("A",status)){
                startVo.setAutoStartMessage("上游付全款完成，自动收票申请");
            }else{
                startVo.setAutoStartMessage("上游付全款完成，自动并完成收票申请");
            }
            startVo.setBizEntityJson(entityJson);
            startVo.setProcessId(process.getId());
            startVo.setDeptId(contract.getDeptId());
            startVo.setMode(BasConstants.APPROVE_STATUS_A);
            startVo.setStatus(status);
            startVo.setApproveId(0L);
            startVo.setUserId(contract.getMatchUserId());
            startVo.setUserName(contract.getMatchUserName());
            startVo.setEnterpriseId(contract.getEnterpriseId());
            startVo.setAutoStartFlgReal(true);
            if(StringUtils.equals("D",status)){
                contract.setBilledAmount(contract.getDealedAmount());
                ctrContractDao.save(contract);
            }
            pmApproveService.startFlow(startVo);
        } catch (Exception e) {
            logger.error("autoInitiatedCompleteInvoiceReceived error contractNo:{}", contract.getContractNo(), e);
        }
    }
    /**
     * 发起中上游，中游 开票申请
     *
     * @param contractId
     */
    @ServerTransactional
    public void addAutoInvoice(Long contractId) {
        CtrContract contractB = ctrContractDao.findOne(contractId);
        // 判断是否是特殊链条 上游-青光-苏高新-上光-下游
        List<CtrContract> ctrContractList = ctrContractDao.findByApproveId(contractB.getApproveId());
        Optional<CtrContract> specialChainFlg = ctrContractList.stream().filter(CtrContract::getSpecialChainFlag).findAny();
        Optional<CtrContract> findContractS = ctrContractList.stream().filter(it -> it.getContractType().equals("S")).findFirst();
        CtrContract contractS=null;
        if(findContractS.isPresent()){
            contractS=findContractS.get();
        }
        if (specialChainFlg.isPresent()&&contractS!=null&&contractS.getOurCompanyName().equals(BasConstants.COMPANY_NAME_SHZG)) {
            // 判断是否全部开票完成
            if (contractB.getBilledAmount().compareTo(contractB.getTotalAmount()) >= 0) {
                // 获取所有关联的合同数据
                List<CtrContractProfit> ctrContractProfitList = ctrContractProfitService.findByAndApproveId(contractB.getApproveId());
                // 转化为map,key 为 buycontractNo
                Map<String, String> profitMap = ctrContractProfitList.stream()
                        .collect(Collectors.toMap(CtrContractProfit::getBuyContractNo, CtrContractProfit::getSellContractNo));
                CtrContract contractB1 = specialChainFlg.get();
                String contrcatNoX = profitMap.get(contractB1.getContractNo());
                ApplyCtrDCSX contractX = applyDcsxDao.findByContractNo(contrcatNoX);
                applyInvoiceService.autoApplyInvoice(contractB1);
                applyInvoiceService.autoApplyDcsxInvoice(contractX);
            }
        }
    }
}

