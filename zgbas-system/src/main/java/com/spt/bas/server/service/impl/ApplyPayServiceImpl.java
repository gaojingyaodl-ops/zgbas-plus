package com.spt.bas.server.service.impl;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.constant.BsDictConstants;
import com.spt.bas.client.entity.*;
import com.spt.bas.client.vo.BatchPayApplyParam;
import com.spt.bas.client.vo.CtrContractApplyVo;
import com.spt.bas.server.annotation.ServerTransactional;
import com.spt.bas.server.cache.BsDictUtil;
import com.spt.bas.server.ctr.service.ICtrContractUpdateService;
import com.spt.bas.server.dao.ApplyPayDao;
import com.spt.bas.server.dao.CtrContractApplyDao;
import com.spt.bas.server.dao.CtrContractDao;
import com.spt.bas.server.dao.CtrProductDao;
import com.spt.bas.server.enums.FundFlowEnum;
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
import com.spt.tools.core.bean.PageSearchVo;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.core.json.JsonUtil;
import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.persistence.WebUtil;
import com.spt.tools.jpa.service.BaseService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 付款申请
 *
 * @author shengong
 */
@Component("applyPayService")
@Transactional(readOnly = true)
public class ApplyPayServiceImpl extends BaseService<ApplyPay> implements IApplyPayService, IPmService, IPmApproveListener {
    private static final ScheduledExecutorService SCHEDULED_POOL = Executors.newScheduledThreadPool(10);
    @Autowired
    private ApplyPayDao applyPayDao;
    @Autowired
    private ICtrContractUpdateService ctrContractUpdateService;
    @Autowired
    private ICtrContractService ctrContractService;
    @Autowired
    private ICtrContractOphisService contractOphisService;
    @Autowired
    private IPmApproveService pmApproveService;
    @Autowired
    private ICtrContractApplyService contractApplyService;
    @Autowired
    private CtrContractDao ctrContractDao;
    @Autowired
    private CtrContractApplyDao ctrContractApplyDao;
    @Autowired
    private IStockDetailService stockDetailService;
    @Autowired
    private RtPushServiceImpl rtPushService;
    @Autowired
    private CtrProductDao ctrProductDao;
    @Autowired
    private IBsProductConfigService bsProductConfigService;
    @Autowired
    private PmProcessDao processDao;
    @Autowired
    private IApplyDcsxService applyDcsxService;
    @Resource
    private IFundAmountFlowService fundAmountFlowService;
    @Resource
    private IBsCompanyDcsxService bsCompanyDcsxService;

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
            CtrContract contract = ctrContractService.getEntity(contractId);
            ctrContractUpdateService.addDealedAmount(contractId, pay.getPayAmount(), approve.getApproveNo(), null, pay.getPayDate());
            // 适配历史审批中的审批单添加操作记录：过段时间可以去除
            Date createdDate = approve.getCreatedDate();
            if (createdDate != null) {
                String format = DateUtil.format(createdDate, "yyyy-MM-dd");
                if (BasConstants.NEW_ADD_HIS_START_DATE.compareTo(format) >= 0) {
                    //2.添加合同操作记录
                    contractOphisService.addHis(BasConstants.APPLY_TYPE_P, contractId, approve, pay.getPayDate());
                }
            }

            //更新合同付款金额申请信息
            updateCtrContractApply(pay, contract, approve.getProcessId());

            // 推送付款信息至融拓
            rtPushService.pushPayToRt(pay, approve);


            // 赊销收票申请，如果上游企业不是我方企业、奥顺宇、塑融云，在上游付款审批完成后(旧逻辑)
            // applyInvoiceReceivedService.autoInitiatedCompleteInvoiceReceived(contract, pay.getPayAmount());

            if (StringUtils.equals("F", pay.getPayMode())){
                fundAmountFlowService.addFundFlow(pay.getOurCompanyName(), pay.getCompanyName(), pay.getPayAmount().negate(), FundFlowEnum.VirtualPayment, approve);
            }

            // 代采赊销范伦克采购合同付全款后，自动发起并完成，中游合同代采赊销付款申请
            autoInitiatedCompleteFLKDcsxPay(contract);
        }

    }

    /**
     * 检验是否可以发送人保
     * @param
     * @return
     */
    private Boolean checkCanSend(CtrContract buyContract,CtrContract sellContract) {
        if (sellContract.getSettlementType() == null) {
            logger.info("该合同不是赊销合同不发送人保,contract:{}", sellContract);
            return false;
        }
        if (!buyContract.getDealedFlg()) {
            logger.info("该合同还没有付款完成,不发送人保,contract:{}", buyContract);
            return false;
        }
        String buyOurCompanyName = buyContract.getOurCompanyName();
        String sellOurCompanyName = sellContract.getOurCompanyName();
        if (StringUtils.equals(BasConstants.COMPANY_NAME_SDNH, buyOurCompanyName) ||
                StringUtils.equals(BasConstants.COMPANY_NAME_SDNH, sellOurCompanyName)) {
            logger.info("该合同资方为能化,不发送人保,contract:{}", sellContract);
            return false;
        }
        return true;
    }

    @Override
    public void doWithdraw(PmApproveWithdrawVo vo) throws ApplicationException {
        // 作废付款单：扣除已付款金额，添加操作记录
        ApplyPay pay = applyPayDao.findOne(vo.getBizId());
        PmApprove approve = pmApproveService.getEntity(pay.getApproveId());
        Long contractId = pay.getContractId();
        List<StockDetail> list = stockDetailService.findByBuyContractId(contractId.toString());
        if (StringUtils.equals(BasConstants.APPROVE_STATUS_D, pay.getStatus())) {
            ctrContractUpdateService.addDealedAmount(contractId, pay.getPayAmount().negate(), approve.getApproveNo(), null, pay.getPayDate());
        }
        rollbackContractApply(pay);
        //付款作废后 更改库存类型
        for (StockDetail stockDetail : list) {
            //判断是否入库 未入库则更改库存类型为期货库存
            if (stockDetail.getDeliveryInNumber().compareTo(BigDecimal.ZERO) == 0) {
                stockDetail.setStockType(BasConstants.DICT_TYPE_STOCKTYPE_QH);
                stockDetail.setSpotType(null);
            }
            stockDetailService.save(stockDetail);
        }

        if (StringUtils.equals("F", pay.getPayMode())){
            fundAmountFlowService.addFundFlow(pay.getOurCompanyName(), pay.getCompanyName(), pay.getPayAmount(), FundFlowEnum.VirtualPaymentCancel, approve);
        }
    }

    /**
     * 更新合同金额申请表
     * @param pay
     * @param contract
     * @param processId
     * @throws ApplicationException
     */
    @Override
    @ServerTransactional
    public void updateCtrContractApply(ApplyPay pay, CtrContract contract, Long processId) throws ApplicationException {
        CtrContractApply contractApply = contractApplyService.findByContractId(pay.getContractId());
        List<StockDetail> list = stockDetailService.findByBuyContractId(pay.getContractId().toString());
        if (Objects.nonNull(contractApply)) {
            //当付全款后，库存类型变为现货上家货权（如果原本的类型是现货我司货权，类型就不变）；
            if (pay.getPayedAmount() == null) {
                pay.setPayedAmount(BigDecimal.ZERO);
            }
            BigDecimal payedAmount = pay.getPayedAmount().add(pay.getPayAmount());
            if (payedAmount.compareTo(pay.getTotalAmount()) >= 0) {
                for (StockDetail stockDetail : list) {
                    if (BasConstants.DICT_TYPE_STOCKTYPE_QH.equals(stockDetail.getStockType())) {
                        stockDetail.setStockType(BasConstants.DICT_TYPE_STOCKTYPE_XH);
                        stockDetail.setSpotType(BasConstants.DICT_TYPE_SPOTTYPE_S);
                    }
                    stockDetailService.save(stockDetail);
                }
            }
            //更新合同付款时间
            Date realPayDate = contractApply.getRealPayDate();
            Date payDate = pay.getPayDate();
            if (realPayDate == null || payDate.after(realPayDate)) {
                contractApply.setRealPayDate(payDate);
                contractApplyService.save(contractApply);
            }
        }
    }

    @Override
    public ApplyPay findApplyPayByContractNo(String contractNo) {
        return applyPayDao.findApplyPayByContractNo(contractNo);
    }

    /**
     * 按批次批量发起付款申请
     *
     * @param saveVo
     * @return
     */
    @Override
    @ServerTransactional
    public PmApprove startBatchPayApply(PmApproveSaveVo saveVo) throws ApplicationException {
        PmProcess process = processDao.findOne(saveVo.getProcessId());
        IPmEntity bizEntity = null;
        PmApprove pmApprove = null;
        String entityName = process.getEntityName();
        if (StringUtils.isNotBlank(entityName)) {
            try {
                bizEntity = (IPmEntity) JsonUtil.json2Object(Class.forName(entityName), saveVo.getBizEntityJson());
            } catch (Exception e) {
                logger.error("startBatchPayApply getBizEntity", e);
            }
        }
        if (bizEntity instanceof ApplyPay) {
            ApplyPay applyPay = (ApplyPay) bizEntity;
            applyPay.setEnterpriseId(saveVo.getEnterpriseId());

            List<BigDecimal> batchAmountList = getBatchPayAmountList(applyPay);
            for (BigDecimal payAmount : batchAmountList) {
                pmApprove = autoStartPayApply(saveVo, applyPay, payAmount);
            }
        }
        return pmApprove;
    }

    @Override
    public ApplyPay getBrushBrushAmount(ApplyPay applyPay) {
        Long contractId = applyPay.getContractId();
        applyPay.setPayAmount(BigDecimal.ZERO);
        if (Objects.isNull(contractId) || contractId == 0L) {
            return applyPay;
        }
        CtrContract entity = ctrContractService.getEntity(contractId);
        CtrContractApply contractApply = ctrContractApplyDao.findByCtrContractId(contractId);
        boolean batchPayApplyFlg = bsProductConfigService.verifyBatchPayApply(contractId);
        if (Boolean.FALSE.equals(batchPayApplyFlg)) {
            applyPay.setPayAmount(entity.getTotalAmount().subtract(contractApply.getApplyPayAmount()));
            return applyPay;
        }
        List<BigDecimal> batchList = getBatchPayAmountList(applyPay);
        if (CollectionUtils.isNotEmpty(batchList)) {
            applyPay.setPayAmount(batchList.get(0));
            return applyPay;
        }
        return applyPay;
    }

    /**
     * 发起付款申请
     *
     * @param startVo
     * @param applyPay
     * @param payAmount
     */
    private PmApprove autoStartPayApply(PmApproveSaveVo startVo, ApplyPay applyPay, BigDecimal payAmount) throws ApplicationException {
        applyPay.setPayAmount(payAmount);
        String entityJson = JsonUtil.obj2Json(applyPay);
        startVo.setBizEntityJson(entityJson);
        startVo.setMode("A");
        startVo.setStatus(BasConstants.APPROVE_STATUS_A);
        startVo.setApproveId(0L);
        startVo.setAutoStartMessage("批量发起付款申请");
        return pmApproveService.startFlow(startVo);
    }

    /**
     * 计算每批次付款申请金额
     *
     * @param applyPay
     * @return
     */
    private List<BigDecimal> getBatchPayAmountList(ApplyPay applyPay) {
        List<BigDecimal> batchAmountList = new ArrayList<>();
        BatchPayApplyParam param = bsProductConfigService.findBatchPayApplyParam(applyPay.getEnterpriseId());
        BigDecimal batchNumber = param.getBatchNumber();
        CtrProduct product = ctrProductDao.findByCtrContractId(applyPay.getContractId()).get(0);
        BigDecimal dealPrice = product.getDealPrice();
        BigDecimal[] batchValue = product.getDealNumber().divideAndRemainder(batchNumber);
        BigDecimal batchSize = batchValue[0];
        BigDecimal lastBatchNumber = batchValue[1];
        for (int i = 0; i < batchSize.intValue(); i++) {
            BigDecimal batchAmount = batchNumber.multiply(dealPrice).setScale(2, RoundingMode.HALF_UP);
            batchAmountList.add(batchAmount);
        }
        if (lastBatchNumber.compareTo(BigDecimal.ZERO) > 0) {
            batchAmountList.add(lastBatchNumber.multiply(dealPrice).setScale(2, RoundingMode.HALF_UP));
        }
        return batchAmountList;
    }

    @Override
    @ServerTransactional
    public void doStepBack(PmApproveCurrVo approve, PmApproveStep nextStep) throws ApplicationException {
        ApplyPay entity = applyPayDao.findOne(approve.getBizId());
        rollbackContractApply(entity);
    }

    @Override
    @ServerTransactional
    public void rollbackContractApply(ApplyPay entity) throws ApplicationException {
        //更新CtrContractApply中数据
        CtrContractApplyVo vo = new CtrContractApplyVo();
        vo.setContractId(entity.getContractId());
        vo.setDealAmount(entity.getPayAmount().negate());
        vo.setApplyType(BasConstants.APPLY_TYPE_P);
        String status = entity.getStatus();
        if (StringUtils.equals(BasConstants.APPROVE_STATUS_C, status)) {
            Date findLastPay = applyPayDao.findLastPay(entity.getContractId());
            vo.setRealDate(findLastPay);
        }
        contractApplyService.updateCtrContractApply(vo);
    }

    @Override
    @ServerTransactional
    public IPmEntity saveEntity(IPmEntity pmEntity) throws ApplicationException {
        if (pmEntity != null) {
            ApplyPay entity = (ApplyPay) pmEntity;
            if (entity.getId() == null || entity.getId() == 0) {
                entity.setApplyNo(composeContractNo(entity.getContractNo()));
            }
            CtrContract contract = ctrContractService.getEntity(entity.getContractId());
            BigDecimal unPayedAmount = contract.getTotalAmount().subtract(contract.findRealDealedAmount());
            entity.setUnpayedAmount(unPayedAmount);
            entity.setDeptId(contract.getDeptId());
            entity.setBusinessType(contract.getBusinessType());
            List<CtrContract> contractList = ctrContractService.findByApproveId(contract.getApproveId());
            ApplyCtrDCSX dcsxContract = applyDcsxService.findByDCSXApproveId(contract.getApproveId());
            // 是否全部双签
            entity.setAllSealFlg(verifyAllSealFlg(contractList, dcsxContract));
            // 设置所属区域
            entity.setOwningRegion(contract.getOwningRegion());
            return save(entity);

        }
        return null;
    }

    @Override
    public Map<String, Object> buildConditionDefaultMap(IPmEntity pmEntity) {
        ApplyPay entity = (ApplyPay) pmEntity;
        Map<String, Object> conditionDefaultMap = new HashMap<>();
        try {
            CtrContract contract = ctrContractDao.findOne(entity.getContractId());
            conditionDefaultMap = BasBusinessUtil.buildConditionDefaultMap(contract);
            CtrContract sellContract = ctrContractDao.findByApproveIdAndContractType(contract.getApproveId(), BasConstants.CONTRACT_TYPE_S);
            if (Objects.nonNull(sellContract)) {
                conditionDefaultMap.put("sellOurCompanyName", sellContract.getOurCompanyName());
            }
        } catch (Exception e) {
            logger.error("applyPayService buildConditionDefaultMap", e);
        }
        return conditionDefaultMap;
    }

    private boolean verifyAllSealFlg(List<CtrContract> contractList, ApplyCtrDCSX dcsxContract){
        boolean dcsxSealFlg = Objects.isNull(dcsxContract) || Boolean.TRUE.equals(dcsxContract.getSealFlg());
        boolean allSealFlg = contractList.stream().allMatch(CtrContract::getSealFlg);
        return dcsxSealFlg && allSealFlg;
    }

    @Override
    public String getSubject(IPmEntity pmEntity, PmProcess process) {
        if (pmEntity != null) {
            ApplyPay pay = (ApplyPay) pmEntity;
            String contractNo = pay.getContractNo();
            String companyName = pay.getCompanyName();
            BigDecimal currPayAmount = pay.getPayAmount();
            String title = BsDictUtil.getValue(pay.getEnterpriseId(), BsDictConstants.DICT_TYPE_PAYTYPE, pay.getPayType());
            String companyName1 = RuleUtil.companyNameSubString(companyName);
            String companyName2 = RuleUtil.companyNameSubString(pay.getOurCompanyName());
            String companyTitle = companyName;
            if (StringUtils.isNotBlank(companyName1) && StringUtils.isNotBlank(companyName2)) {
                companyTitle = companyName1 + "-" + companyName2;
            }
            CtrContract contract = ctrContractService.getEntity(pay.getContractId());
            // this.overagePayVerify(pay, contract);
            pay = applyPayDao.save(pay);
            String overageVerify = Boolean.TRUE.equals(pay.getOverageFlg()) ? "[超额]" : "";
            String businessName = SubjectUtil.getBusinessName(contract.getBusinessType(), contract.getMatchCreditFlg());
            String payAmountStr = SubjectUtil.formatMoney(currPayAmount, RuleUtil.monetaryUnit);
            String subjectPrefix = overageVerify + contractNo;
            return SubjectUtil.formatSubject(subjectPrefix, businessName, companyTitle, payAmountStr, title);
        }
        return null;
    }

//    private void overagePayVerify(ApplyPay applyPay, CtrContract contract) {
//        try {
//            applyPay.setOverageFlg(false);
//            applyPay.setOverageMessage("");
//            List<CtrContract> contractList = ctrContractService.findByApproveId(contract.getApproveId());
//            if (CollectionUtils.isNotEmpty(contractList)) {
//                CtrContract buyContract = contractList.stream().filter(c -> StringUtils.equals(BasConstants.CONTRACT_TYPE_B, c.getContractType())).findAny().orElse(null);
//                CtrContract sellContract = contractList.stream().filter(c -> StringUtils.equals(BasConstants.CONTRACT_TYPE_S, c.getContractType())).findAny().orElse(null);
//                if (Objects.isNull(buyContract) || Objects.isNull(sellContract)) {
//                    return;
//                }
//                CtrContractApply contractApply = ctrContractApplyDao.findByCtrContractId(applyPay.getContractId());
//                if (Boolean.FALSE.equals(sellContract.getMatchCreditFlg()) && StringUtils.equals(BasConstants.BUSINESS_TYPE_ZY_BB, buyContract.getBusinessType())) {
//                    BigDecimal buyPayAmount = contractApply.getApplyPayAmount();
//                    BigDecimal sellReceiveAmount = sellContract.getDealedAmount();
//                    BigDecimal currPayAmount = applyPay.getPayAmount();
//                    BigDecimal compareValue = buyPayAmount.add(currPayAmount).subtract(sellReceiveAmount);
//                    if (compareValue.compareTo(BigDecimal.ZERO) > 0) {
//                        String message = String.format("超额：收款金额%s，超出%s。", SubjectUtil.formatMoney(sellReceiveAmount, RuleUtil.monetaryUnit),
//                                SubjectUtil.formatMoney(compareValue, RuleUtil.monetaryUnit));
//                        applyPay.setOverageFlg(true);
//                        applyPay.setOverageMessage(message);
//                    }
//                }
//            }
//        } catch (Exception e) {
//            logger.error("overagePayVerify error", e);
//        }
//    }

    @Override
    @ServerTransactional
    public void doStepIn(PmApprove approve) throws ApplicationException {
        ApplyPay entity = applyPayDao.findOne(approve.getBizId());
        CtrContractApply contractApply = ctrContractApplyDao.findByCtrContractId(entity.getContractId());
        if (contractApply != null) {
            if (entity.getPayAmount().compareTo(BigDecimal.ZERO) == 0) {
                throw new ApplicationException("付款金额必须大于0!");
            }
            CtrContract ctrContract = ctrContractDao.findOne(entity.getContractId());
            String businessType = ctrContract.getBusinessType();
            if (StringUtils.equals(BasConstants.BUSINESS_TYPE_ZY_BB, businessType) && StringUtils.equals(BasConstants.COMPANY_NAME_FLK, ctrContract.getCompanyName())){
                CtrContract targetContract = ctrContractDao.findByApproveIdAndContractType(ctrContract.getApproveId(), BasConstants.CONTRACT_TYPE_B);
                if (Objects.nonNull(targetContract)) {
                    Date maxPayDate = targetContract.getPayFullTime();
                    if (Boolean.TRUE.equals(targetContract.getMatchCreditFlg())){
                        maxPayDate = applyPayDao.findLastPay(targetContract.getId());
                        if (Objects.isNull(maxPayDate)) {
                            throw new ApplicationException("【范伦克付款流程】- 上游合同付款二天后才可发起!");
                        }
                    }
                    DateTime completeDate = DateUtil.beginOfDay(maxPayDate);
                    Date approveDate = DateUtil.offsetDay(completeDate, 2);
                    if (new Date().before(approveDate)) {
                        throw new ApplicationException("【范伦克付款流程】- 上游合同付款二天后（即："+ DateUtil.formatDate(approveDate) +"）才可发起!");
                    }
                }
            }

            BigDecimal finalTotalAmount = ctrContract.getFinalTotalAmount();
            BigDecimal totalAmount = (Objects.nonNull(finalTotalAmount) && finalTotalAmount.compareTo(BigDecimal.ZERO) > 0) ? finalTotalAmount : ctrContract.getTotalAmount();
            // amount = 本次支付金额(payAmount) + 该采购合同已支付金额(applyPayAmount) - 退款金额
            BigDecimal amount = entity.getPayAmount().add(contractApply.getApplyPayAmount())
                    .subtract(ctrContract.getRefundAmount());
            // subtract(加上本次支付金额的还需支付金额) = 合同总金额 - 本次支付金额(payAmount) - 该采购合同已支付金额(applyPayAmount) + 退款金额
            BigDecimal subtract = totalAmount.subtract(amount);
            // sperAmount(剩余支付金额) = 合同总金额 - 已支付金额 + 退款金额
            BigDecimal sperAmount = totalAmount.subtract(contractApply.getApplyPayAmount())
                    .add(ctrContract.getRefundAmount());

            if (subtract.compareTo(BigDecimal.ZERO) < 0) {
                throw new ApplicationException("付款金额有误,剩余可付款金额为：" + sperAmount);
            }
            //更新CtrContractApply中数据
            CtrContractApplyVo vo = new CtrContractApplyVo();
            vo.setContractId(entity.getContractId());
            vo.setDealAmount(entity.getPayAmount());
            vo.setApplyType(BasConstants.APPLY_TYPE_P);
            contractApplyService.updateCtrContractApply(vo);
            contractOphisService.addHis(BasConstants.APPLY_TYPE_P, entity.getContractId(), approve, entity.getPayDate());
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

    @Override
    public ApplyPay findPageSum(PageSearchVo searchVo) {
        Specification<ApplyPay> spe = WebUtil.buildSpecification(searchVo.getSearchParams());
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<?> query = cb.createQuery();
        Root<ApplyPay> root = query.from(ApplyPay.class);
        CriteriaQuery<?> cq = query.where(spe.toPredicate(root, query, cb)).multiselect(
                cb.sum(root.get("payAmount")));
        TypedQuery<?> tq = em.createQuery(cq);
        Object result = tq.getSingleResult();
        ApplyPay sum = new ApplyPay();
        BigDecimal payedAmount = (BigDecimal) result;
        sum.setPayAmount(payedAmount);
        return sum;
    }

    private String composeContractNo(String contractNo) {
        List<ApplyPay> deliveryIn = applyPayDao.findByContractNo(contractNo);
        String fmt = String.format("%02d", deliveryIn.size() + 1);
        return contractNo + BasConstants.APPLY_TYPE_P + fmt;
    }


    /**
     * 代采赊销范伦克采购合同付全款后，自动发起并完成，中游合同代采赊销付款申请
     *
     * @param contract 采购合同
     */
    @Override
    @ServerTransactional
    public void autoInitiatedCompleteFLKDcsxPay(CtrContract contract) {
        logger.info("autoInitiatedCompleteDcsxPay contract:{}", contract.getContractNo());
        SCHEDULED_POOL.schedule(() -> {
            try {
                // 赊销标识
                Boolean matchCreditFlg = contract.getMatchCreditFlg();
                // 我方抬头
                String ourCompanyName = contract.getOurCompanyName();
                // 代采赊销范伦克采购合同付全款后，自动发起并完成，中游合同代采赊销付款申请
                if (!Boolean.TRUE.equals(matchCreditFlg)) {
                    return;
                }
                if (!StringUtils.equals(BasConstants.COMPANY_NAME_FLK, ourCompanyName)) {
                    return;
                }
                CtrContract buyContract = ctrContractDao.findOne(contract.getId());
                if (buyContract.getDealedAmount().compareTo(buyContract.getTotalAmount()) < 0) {
                    return;
                }
                ApplyCtrDCSX applyCtrDCSX = applyDcsxService.findByDCSXApproveId(buyContract.getApproveId());
                if (Objects.isNull(applyCtrDCSX) || !StringUtils.equals(BasConstants.COMPANY_NAME_SUGX, applyCtrDCSX.getOurCompanyName())) {
                    return;
                }
                // 判断是否可以发起中游付款申请
                BigDecimal totalAmount = applyCtrDCSX.getTotalAmount();
                List<ApplyPay> dcsxPayList = applyPayDao.findByContractNo(applyCtrDCSX.getContractNo());
                BigDecimal applyPayAmount = dcsxPayList.stream()
                        .filter(d -> (StringUtils.equals(BasConstants.APPROVE_STATUS_A, d.getStatus())
                                || StringUtils.equals(BasConstants.APPROVE_STATUS_A, d.getStatus())))
                        .map(ApplyPay::getPayAmount)
                        .filter(Objects::nonNull)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                BigDecimal targetPayAmount = totalAmount.subtract(applyPayAmount);
                if (targetPayAmount.compareTo(BigDecimal.ZERO) <= 0) {
                    return;
                }
                PmApproveSaveVo saveVo = buildFLKDcsxPayVo(applyCtrDCSX, targetPayAmount);
                pmApproveService.startFlow(saveVo);
            } catch (Exception e) {
                logger.error("autoInitiatedCompleteDcsxPay error!", e);
            }

        }, 5, TimeUnit.SECONDS);
    }
    private PmApproveSaveVo buildFLKDcsxPayVo(ApplyCtrDCSX entity, BigDecimal payAmount){
        PmApproveSaveVo startVo = new PmApproveSaveVo();
        PmProcess process = processDao.findByProcessCodeAndEnterpriseId(BasConstants.PROCESS_CODE_DCSX_PAY, entity.getEnterpriseId());

        ApplyPay pay = new ApplyPay();
        pay.setId(0L);
        pay.setContractId(entity.getId());
        pay.setContractNo(entity.getContractNo());
        pay.setPayDate(new Date());
        pay.setTotalAmount(entity.getTotalAmount());
        pay.setPayedAmount(entity.getDealedAmount());
        pay.setUnpayedAmount(entity.getTotalAmount().subtract(entity.getDealedAmount()));
        pay.setPayType("A");
        pay.setPayAmount(payAmount);
        pay.setCompanyId(entity.getCompanyId());
        pay.setCompanyName(entity.getCompanyName());
        BsCompanyDcsx companyConfig = bsCompanyDcsxService.findByCompanyName(entity.getCompanyName());
        if (Objects.nonNull(companyConfig)) {
            pay.setBankName(companyConfig.getCompanyBankName());
            pay.setBankAccount(companyConfig.getCompanyCardId());
        }
        pay.setPayMode("F");
        pay.setStatus("D");
        pay.setEnterpriseId(entity.getEnterpriseId());
        pay.setOurCompanyName(entity.getOurCompanyName());
        pay.setDeptId(entity.getDeptId());
        pay.setAllSealFlg(true);

        // 设置自动发起并完成审批单
        String entityJson = JsonUtil.obj2Json(pay);
        startVo.setAutoStartMessage("上游付全款完成，自动发起代采赊销付款申请");
        startVo.setBizEntityJson(entityJson);
        startVo.setProcessId(process.getId());
        startVo.setDeptId(entity.getDeptId());
        startVo.setMode(BasConstants.APPROVE_STATUS_A);
        startVo.setStatus(BasConstants.APPROVE_STATUS_D);
        startVo.setApproveId(0L);
        startVo.setUserId(entity.getMatchUserId());
        startVo.setUserName(entity.getMatchUserName());
        startVo.setEnterpriseId(entity.getEnterpriseId());
        startVo.setAutoStartFlgReal(true);
        return startVo;
    }
}

