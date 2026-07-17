package com.spt.bas.server.service.impl;

import cn.hutool.core.date.DateUtil;
import com.google.common.base.Stopwatch;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.*;
import com.spt.bas.client.vo.OverdueInterestDto;
import com.spt.bas.client.vo.OverdueInterestVo;
import com.spt.bas.server.dao.*;
import com.spt.bas.server.service.IBsCompanyDcsxService;
import com.spt.tools.core.date.DateOperator;
import com.spt.tools.core.json.JsonUtil;
import com.spt.tools.data.annotation.ServiceTransactional;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * 中游逾期罚息处理类
 *
 * @Author MoonLight
 * @Date 2023/10/18 9:57
 * @Version 1.0
 */
@Slf4j
@Component
public class OverdueInterestProcessor {
    @Resource
    private CtrContractDao ctrContractDao;
    @Resource
    private ApplyDcsxDao applyDcsxDao;
    @Resource
    private ApplyReceiveDao applyReceiveDao;
    @Resource
    private ApplyPayDao applyPayDao;
    @Resource
    private ApplyPayRefundDao applyPayRefundDao;
    @Resource
    private ApplyReceiveRefundDcsxDao applyReceiveRefundDcsxDao;
    @Resource
    private ApplyReceiveRefundDao applyReceiveRefundDao;
    @Resource
    private IBsCompanyDcsxService bsCompanyDcsxService;

    private static final BigDecimal YEAR_DAYS_365 = new BigDecimal(365);
    private static final BigDecimal YEAR_DAYS_360 = new BigDecimal(360);
    private static final BigDecimal PARENT_0_02 = new BigDecimal("0.02");
    private static final BigDecimal PARENT_0_001 = new BigDecimal("0.001");
    private static final BigDecimal ANNUALIZED_REVENUE_RATE_01 = new BigDecimal("0.1");
    private static final BigDecimal ANNUALIZED_REVENUE_RATE_011 = new BigDecimal("0.11");
    private static final BigDecimal ANNUALIZED_REVENUE_RATE_0065 = new BigDecimal("0.065");
    private static final BigDecimal ANNUALIZED_REVENUE_RATE_0365 = new BigDecimal("0.365");

    /**
     * 更新中游逾期罚息
     *
     * @param contractNo 中游合同编号
     */
    @ServiceTransactional
    public void refreshOverdueInterest(String contractNo) throws Exception {
        Stopwatch started = Stopwatch.createStarted();
        List<ApplyCtrDCSX> contractList = new ArrayList<>();
        if (StringUtils.isNotBlank(contractNo)) {
            contractList.add(applyDcsxDao.findByContractNo(contractNo));
        } else {
            contractList = applyDcsxDao.findComputeList();
        }

        if (CollectionUtils.isEmpty(contractList)) {
            return;
        }
        log.info("refreshOverdueInterest size:{}", contractList.size());
        Map<String, BsCompanyDcsx> companyConfigMap = bsCompanyDcsxService.getCompanyConfigMap();
        int numThreads = Runtime.getRuntime().availableProcessors();
        ExecutorService executorService = Executors.newFixedThreadPool(numThreads);
        ExecutorCompletionService<String> execu = new ExecutorCompletionService<>(executorService);
        int taskSize = contractList.size();
        float bathSize = 30F;
        int bath = (int) Math.ceil((taskSize / bathSize));
        for (int i = 0; i < bath; i++) {
            int start = (int) (bathSize * i);
            int end = (int) (start + bathSize);
            end = Math.min(end, taskSize);
            List<ApplyCtrDCSX> syncList = contractList.subList(start, end);
            execu.submit(() -> {
                computeOverdueInterest(syncList, companyConfigMap);
                return "refreshOverdueInterest OK";
            });
        }
        for (int i = 0; i < bath; i++) {
            Future<String> future = execu.take();
            log.info("result:{},{}", i, future.get());
        }
        executorService.shutdown();
        log.info("总耗时:{}", started.elapsed(TimeUnit.SECONDS));
    }

    /**
     * 逾期罚息计算逻辑
     *
     * @param contractList     中游合同列表
     * @param companyConfigMap 代采方配置
     */
    @ServiceTransactional
    public void computeOverdueInterest(List<ApplyCtrDCSX> contractList, Map<String, BsCompanyDcsx> companyConfigMap) {
        Map<String, String> contractNoMap = convertContractNo(contractList);
        List<String> sellContractNos = new ArrayList<>(contractNoMap.keySet());
        List<String> buyContractNos = convertBuyContractNo(sellContractNos);
        List<String> contractNoList = new ArrayList<>(contractNoMap.values());
        List<Long> approveIds = contractList.stream().map(ApplyCtrDCSX::getApproveId).collect(Collectors.toList());

        List<CtrContract> sellContractList = ctrContractDao.findByApproveIdInAndContractType(approveIds, BasConstants.CONTRACT_TYPE_S);

        // [上游] 付款，退款明细列表
        List<ApplyPay> buyPayList = applyPayDao.findByContractNoIn(buyContractNos);
        List<ApplyPayRefund> buyPayRefundList = applyPayRefundDao.findByContractNoIn(buyContractNos);

        // [中游] 代采赊销付款、退款明细列表
        List<ApplyPay> payList = applyPayDao.findByContractNoIn(contractNoList);
        List<ApplyReceiveRefundDcsx> payRefundList = applyReceiveRefundDcsxDao.findByContractNoIn(contractNoList);

        // [中游] 代采赊销收款
        List<ApplyReceive> receiveDcsxList = applyReceiveDao.findByContractNoIn(contractNoList);

        // [下游] 收款，退款明细列表
        List<ApplyReceive> receiveList = applyReceiveDao.findByContractNoIn(sellContractNos);
        List<ApplyReceiveRefund> receiveRefundList = applyReceiveRefundDao.findByContractNoIn(sellContractNos);

        Map<String, List<ApplyPay>> buyPayMap = buyPayList.stream().collect(Collectors.groupingBy(ApplyPay::getContractNo));
        Map<String, BigDecimal> buyPayRefundMap = buyPayRefundList.stream().collect(Collectors.toMap(ApplyPayRefund::getContractNo, ApplyPayRefund::getRefundAmount, BigDecimal::add));

        Map<String, List<ApplyPay>> payMap = payList.stream().collect(Collectors.groupingBy(ApplyPay::getContractNo));
        Map<String, BigDecimal> payRefundMap = payRefundList.stream().collect(Collectors.toMap(ApplyReceiveRefundDcsx::getContractNo, ApplyReceiveRefundDcsx::getRefundAmount, BigDecimal::add));

        Map<String, List<ApplyReceive>> receiveMap = receiveList.stream().collect(Collectors.groupingBy(ApplyReceive::getContractNo));
        Map<String, List<ApplyReceive>> receiveDcsxMap = receiveDcsxList.stream().collect(Collectors.groupingBy(ApplyReceive::getContractNo));
        Map<String, BigDecimal> receiveRefundMap = receiveRefundList.stream().collect(Collectors.toMap(ApplyReceiveRefund::getContractNo, ApplyReceiveRefund::getRefundAmount, BigDecimal::add));

        Map<Long, CtrContract> contractMap = sellContractList.stream().collect(Collectors.toMap(CtrContract::getApproveId, c -> c, (c1, c2) -> c2));
        receiveMap = convertReceiveMapKey(receiveMap, contractNoMap);
        receiveRefundMap = convertReceiveRefundMapKey(receiveRefundMap, contractNoMap);

        List<ApplyCtrDCSX> updateEntityList = new ArrayList<>();
        for (ApplyCtrDCSX entity : contractList) {
            Boolean specialSGXFlag = verifySpecialSGX(entity);
            Boolean specialHBFlag = verifySpecialHB(entity);
            String contractNo = specialSGXFlag ? getSpecialSellContractNo(entity.getContractNo()) : entity.getContractNo();
            List<ApplyPay> applyPays = payMap.get(contractNo);
            BigDecimal payRefundAmount = payRefundMap.get(contractNo);
            payRefundAmount = Objects.isNull(payRefundAmount) ? BigDecimal.ZERO : payRefundAmount;

            List<ApplyReceive> applyReceives = receiveMap.get(contractNo);
            BigDecimal receiveRefundAmount = receiveRefundMap.get(contractNo);
            receiveRefundAmount = Objects.isNull(receiveRefundAmount) ? BigDecimal.ZERO : receiveRefundAmount;

            if (BooleanUtils.isTrue(specialHBFlag)) {
                if (buyPayMap.containsKey(dcscNoToBuyNo(contractNo))) {
                    applyPays = buyPayMap.get(dcscNoToBuyNo(contractNo));
                }
                if (buyPayRefundMap.containsKey(dcscNoToBuyNo(contractNo))) {
                    payRefundAmount = buyPayRefundMap.get(dcscNoToBuyNo(contractNo));
                    payRefundAmount = Objects.isNull(payRefundAmount) ? BigDecimal.ZERO : payRefundAmount;
                }
                if (receiveDcsxMap.containsKey(contractNo)) {
                    applyReceives = receiveDcsxMap.get(contractNo);
                    receiveRefundAmount = BigDecimal.ZERO;
                }
            }
            String targetCompanyName = (specialSGXFlag || specialHBFlag) ? entity.getCompanyName() : entity.getOurCompanyName();
            BsCompanyDcsx companyConfig = companyConfigMap.get(targetCompanyName);
            if (CollectionUtils.isEmpty(applyPays) || CollectionUtils.isEmpty(applyReceives)) {
                log.info("contractNo:{} , 中游合同未付款，不存在逾期，计算中止!", contractNo);
            } else {
                BigDecimal configValue = Objects.nonNull(companyConfig) && Objects.nonNull(companyConfig.getAnnualizedRevenue())
                        ? companyConfig.getAnnualizedRevenue()
                        : ANNUALIZED_REVENUE_RATE_01;
                updateEntityList.add(calculateOverdueAmount(entity, applyPays, applyReceives, payRefundAmount, receiveRefundAmount, contractMap, configValue));
            }
        }
        if (CollectionUtils.isNotEmpty(updateEntityList)) {
            applyDcsxDao.saveAll(updateEntityList);
        }
    }

    /**
     * 逾期金额 * 10%/365*（下游回款日-资方付款日）-（下游销售金额 - 中游合同金额）- 资方已收下游逾期罚息
     * 我方支付逾期罚息 = 资金占用成本 -（下游销售金额 - 中游合同金额）- 资方已收下游逾期罚息
     *
     * @param entity        中游合同
     * @param applyPays     中游合同付款单列表
     * @param applyReceives 下游合同收款单列表
     * @param contractMap   下游合同
     * @param ledgerParam   中游逾期罚息计算参数
     * @return
     */
    private ApplyCtrDCSX calculateOverdueAmount(ApplyCtrDCSX entity, List<ApplyPay> applyPays, List<ApplyReceive> applyReceives,
                                                BigDecimal payRefundAmount, BigDecimal receiveRefundAmount,
                                                Map<Long, CtrContract> contractMap, BigDecimal ledgerParam) {
        try {
            CtrContract buyContract = null;
            Boolean specialSGXFlag = verifySpecialSGX(entity);
            Boolean specialHBFlag = verifySpecialHB(entity);
            String contractNo = specialSGXFlag ? getSpecialSellContractNo(entity.getContractNo()) : entity.getContractNo();
            if (Boolean.TRUE.equals(specialSGXFlag) && StringUtils.isNotBlank(contractNo)){
                buyContract = ctrContractDao.findByContractNo(contractNo);
            }
            if (Boolean.TRUE.equals(specialHBFlag)) {
                buyContract = ctrContractDao.findByContractNo(dcscNoToBuyNo(entity.getContractNo()));
            }
            CtrContract sellContract = contractMap.get(entity.getApproveId());
            if (Objects.isNull(sellContract)) {
                log.error("contractNo:{} sellContract is null!", contractNo);
                return null;
            }
            log.info("begin calculate overdueAmount contractNo:{}, applyPaySize:{}, applyReceiveSize:{}", contractNo, applyPays.size(), applyPays.size());
            List<OverdueInterestVo> paramList = disassembleParams(entity, sellContract, buyContract, applyPays, applyReceives, payRefundAmount, receiveRefundAmount);
//            if (BooleanUtils.isTrue(specialHBFlag)) {
//                paramList = disassembleParams2(entity, sellContract, buyContract, applyPays, applyReceives, payRefundAmount, receiveRefundAmount);
//            } else {
//                paramList = disassembleParams(entity, sellContract, buyContract, applyPays, applyReceives, payRefundAmount, receiveRefundAmount);
//            }
            log.info(JsonUtil.obj2Json(paramList));
            if (CollectionUtils.isEmpty(paramList)) {
                log.warn("contractNo:{} paramList is empty", contractNo);
                return null;
            }

            BigDecimal overdueAmount = BigDecimal.ZERO;
            for (OverdueInterestVo param : paramList) {
                Long compareDays = DateOperator.compareDays(formatterDate(param.getPayDate()), formatterDate(param.getReceiveDate()));
                log.info("逾期天数:{}, 逾期金额:{}, 年化收益:{}", compareDays, param.getTargetAmount(), ledgerParam);
                // 资方为山东能化云链供应链发展有限公司的，资方付款日4月及以后得利率是11%，资金使用天数为（下游回款日-资方付款日+1）
                String targetOurCompanyName = Boolean.TRUE.equals(specialSGXFlag) ? entity.getCompanyName() : entity.getOurCompanyName();
                if (StringUtils.equals(BasConstants.COMPANY_NAME_SDNH, targetOurCompanyName) && param.verifyAfterAprilFlg()) {
                    compareDays = compareDays + 1;
                    ledgerParam = ANNUALIZED_REVENUE_RATE_011;
                }
                if (StringUtils.equals(BasConstants.COMPANY_NAME_SUGX, targetOurCompanyName)) {
                    compareDays = compareDays + 1;
                }
                if (StringUtils.equals(BasConstants.COMPANY_NAME_YYHB, targetOurCompanyName)) {
                    if (compareDays > 60){
                        ledgerParam = ANNUALIZED_REVENUE_RATE_01;
                    } else {
                        ledgerParam = ANNUALIZED_REVENUE_RATE_0065;
                    }
                }
                if (BooleanUtils.isTrue(specialHBFlag)){
                    if (compareDays <= 3){
                        ledgerParam = ANNUALIZED_REVENUE_RATE_0365;
                    } else if (compareDays <= 60){
                        ledgerParam = ANNUALIZED_REVENUE_RATE_0065;
                    } else {
                        ledgerParam = ANNUALIZED_REVENUE_RATE_01;
                    }
                }
                BigDecimal calculateAmount = param.getTargetAmount().multiply(new BigDecimal(compareDays)).multiply(ledgerParam.divide(YEAR_DAYS_365, 30, RoundingMode.HALF_UP)).setScale(2, RoundingMode.HALF_UP);
                log.info("calculateAmount:{}", calculateAmount);
                overdueAmount = overdueAmount.add(calculateAmount);

                param.setCompareDays(compareDays);
                param.setOverdueInterestAmount(calculateAmount);
            }
            BigDecimal compareAmount = sellContract.getTotalAmount().subtract(entity.getTotalAmount());
            if (Boolean.TRUE.equals(specialSGXFlag) || Boolean.TRUE.equals(specialHBFlag)){
                compareAmount = entity.getTotalAmount().subtract(buyContract.getTotalAmount());
            }
            BigDecimal receiveBreachAmount = sellContract.getReceiveBreachAmount();
            BigDecimal resultAmount = overdueAmount.subtract(compareAmount);
            BigDecimal extraCost = calculateExtraCost(entity, applyPays, applyReceives, specialSGXFlag, buyContract);
            BigDecimal acceptDiscountCost = calculateAcceptDiscountCost(entity, applyReceives, sellContract, specialSGXFlag, specialHBFlag);
            entity.setExtraCost(extraCost);
            entity.setAcceptDiscountCost(acceptDiscountCost);
            entity.setOverdueInterest(resultAmount);
            log.info("contractNo:{} 中游应收利息 = 资金占用成本 - 中游销售利润 = {} - {} = {}", contractNo, overdueAmount, compareAmount, resultAmount);
            log.info("contractNo:{}, overdueAmount:{}, compareAmount:{}, receiveBreachAmount:{}, resultAmount:{}", contractNo, overdueAmount, compareAmount, receiveBreachAmount, resultAmount);
            entity.setCalculateDetail(JsonUtil.obj2Json(buildCalculateDetails(paramList, entity, sellContract, buyContract, specialSGXFlag, specialHBFlag, overdueAmount, ledgerParam)));
        } catch (Exception e) {
            log.error("calculateOverdueAmount error", e);
        }
        return entity;
    }

    /**
     * 计算合同额外成本
     * 资方为苏高新时才有 合同额外成本
     * (下游最后一笔回款日 - 苏高新第一笔付款日 + 1)<= 15，加合同金额的千1
     *
     * @param entity        中游合同
     * @param applyPays     付款列表
     * @param applyReceives 收款列表
     * @return
     */
    private BigDecimal calculateExtraCost(ApplyCtrDCSX entity, List<ApplyPay> applyPays, List<ApplyReceive> applyReceives,
                                          Boolean specialSGXFlag, CtrContract buyContract) {
        BigDecimal extraCost = BigDecimal.ZERO;
        String ourCompanyName = Boolean.TRUE.equals(specialSGXFlag) ? entity.getCompanyName() : entity.getOurCompanyName();
        if (!StringUtils.equals(BasConstants.COMPANY_NAME_SUGX, ourCompanyName)) {
            return extraCost;
        }
        if (CollectionUtils.isEmpty(applyPays)) {
            return extraCost;
        }
        String contractNo = entity.getContractNo();
        Date minPayDate = applyPays.stream().map(ApplyPay::getPayDate).min(Date::compareTo).orElse(null);
        Date maxReceiveDate = applyReceives.stream().map(ApplyReceive::getReceiveDate).max(Date::compareTo).orElse(null);
        if (Objects.isNull(maxReceiveDate) || Objects.isNull(minPayDate)) {
            log.info("contractNo:{} minPayDate or maxReceiveDate is null", contractNo);
            return extraCost;
        }
        long compareDays = DateOperator.compareDays(minPayDate, maxReceiveDate) + 1L;
        log.info("contractNo:{} minPayDate:{}, maxReceiveDate:{}, compareDays:{}", contractNo, minPayDate, maxReceiveDate, compareDays);
        if (compareDays <= 15) {
            extraCost = entity.getTotalAmount().multiply(PARENT_0_001).setScale(2, RoundingMode.HALF_UP);
            if (Objects.nonNull(buyContract) && Boolean.TRUE.equals(specialSGXFlag)){
                extraCost = buyContract.getTotalAmount().multiply(PARENT_0_001).setScale(2, RoundingMode.HALF_UP);
            }
        }
        log.info("contractNo:{} extraCost:{}", contractNo, extraCost);
        return extraCost;
    }

    /**
     * 承兑贴息成本 = （承兑到期日 - 收承兑日期 + 1）* 0.02/360 - 已收客户贴息成本
     *
     * @param entity        中游合同
     * @param applyReceives 收款列表
     * @param sellContract  下游合同
     * @return
     */
    private BigDecimal calculateAcceptDiscountCost(ApplyCtrDCSX entity, List<ApplyReceive> applyReceives, CtrContract sellContract, Boolean specialSGXFlag, Boolean specialHBFlag) {
        String ourCompanyName = Boolean.TRUE.equals(specialSGXFlag) ? entity.getCompanyName() : entity.getOurCompanyName();
        if (!StringUtils.equals(BasConstants.COMPANY_NAME_SUGX, ourCompanyName)) {
            return BigDecimal.ZERO;
        }
        if (BooleanUtils.isTrue(specialHBFlag)) {
            return BigDecimal.ZERO;
        }
        if (CollectionUtils.isEmpty(applyReceives)) {
            return BigDecimal.ZERO;
        }
        String contractNo = entity.getContractNo();
        List<ApplyReceive> discountReceiveList = applyReceives.stream()
                .filter(r -> StringUtils.equals(BasConstants.PAY_MODE_H, r.getReceiveMode()))
                .filter(r -> Objects.nonNull(r.getBillDueTime()))
                .filter(r -> Objects.nonNull(r.getDueTime()))
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(discountReceiveList)) {
            log.info("contractNo:{} discountReceiveList is null", contractNo);
            return BigDecimal.ZERO;
        }
        BigDecimal totalDiscount = BigDecimal.ZERO;
        for (ApplyReceive receive : discountReceiveList) {
            totalDiscount = totalDiscount.add(getReceiveDiscount(receive));
        }
        return totalDiscount.subtract(sellContract.getDiscountReceiveAmount());
    }

    private BigDecimal getReceiveDiscount(ApplyReceive entity) {
        Date billDueTime = entity.getBillDueTime();
        Date dueTime = entity.getDueTime();
        BigDecimal receiveAmount = entity.getReceiveAmount();
        long compareDays = DateOperator.compareDays(billDueTime, dueTime) + 1L;
        if (compareDays <= 0) {
            return BigDecimal.ZERO;
        }
        return receiveAmount.multiply(BigDecimal.valueOf(compareDays)).multiply(PARENT_0_02).divide(YEAR_DAYS_360, 2, RoundingMode.HALF_UP);
    }

    private OverdueInterestDto buildCalculateDetails(List<OverdueInterestVo> paramList, ApplyCtrDCSX entity, CtrContract sellContract,
                                                     CtrContract buyContract, Boolean specialSGXFlag, Boolean specialHBFlag,
                                                     BigDecimal overdueAmount, BigDecimal ledgerParam) {
        OverdueInterestDto dto = new OverdueInterestDto();
        dto.setInterestList(paramList);
        dto.setAnnualizedRevenueRate(ledgerParam);
        dto.setPayTotalAmount((Boolean.TRUE.equals(specialSGXFlag)) || Boolean.TRUE.equals(specialHBFlag) ? buyContract.getDealedAmount() : entity.getDealedAmount());
        dto.setReceiveTotalAmount((Boolean.TRUE.equals(specialSGXFlag)) || Boolean.TRUE.equals(specialHBFlag) ? entity.getDealedAmount() : sellContract.getDealedAmount());
        dto.setCostCapital(overdueAmount);
        dto.setProfit(sellContract.getTotalAmount().subtract(entity.getTotalAmount()));
        if (Boolean.TRUE.equals(specialSGXFlag) || Boolean.TRUE.equals(specialHBFlag)){
            dto.setProfit(entity.getTotalAmount().subtract(buyContract.getTotalAmount()));
        }
        dto.setOverdueInterest(entity.getOverdueInterest());
        dto.setReceiveBreachAmount(sellContract.getReceiveBreachAmount());
        dto.setNeedPayInterestAmount(entity.getOverdueInterest().subtract(entity.getReceiveOverdueInterest()).subtract(sellContract.getReceiveBreachAmount()));
        if (Boolean.TRUE.equals(specialHBFlag)){
            dto.setNeedPayInterestAmount(entity.getOverdueInterest().subtract(entity.getReceiveOverdueInterest()));
        }
        return dto;
    }

    /**
     * 组装、拆分 计算参数
     *
     * @param entity        中游合同
     * @param sellContract  下游合同
     * @param applyPays     中游付款单列表
     * @param applyReceives 下游收款单列表
     * @return 组装计算参数
     */
    private List<OverdueInterestVo> disassembleParams(ApplyCtrDCSX entity, CtrContract sellContract, CtrContract buyContract,
                                                      List<ApplyPay> applyPays, List<ApplyReceive> applyReceives,
                                                      BigDecimal payRefundAmount, BigDecimal receiveRefundAmount) {
        List<OverdueInterestVo> resultList = new ArrayList<>();
        applyPays = buildPayList(applyPays, payRefundAmount);
        applyReceives = buildReceiveList(applyReceives, receiveRefundAmount);
        log.info("contractNo:{}-------------------------------------------------------------------------", entity.getContractNo());
        BigDecimal dealedAmount = Objects.nonNull(buyContract) ? buyContract.getDealedAmount() : sellContract.getDealedAmount();
        BigDecimal currAmount = BigDecimal.ZERO;
        for (int i = 0; i < applyPays.size(); i++) {
            ApplyPay currPay = applyPays.get(i);
            BigDecimal payAmount = currPay.getPayAmount();
            Date payDate = currPay.getPayDate();
            log.info("中游付款金额:{}, 中游付款日期:{}", payAmount, DateUtil.formatDate(payDate));
            if (CollectionUtils.isEmpty(applyReceives) || applyReceives.size() <= i || Objects.isNull(applyReceives.get(i))) {
                log.info("无对应下游收款数据");
                resultList.add(new OverdueInterestVo(payAmount, payDate));
            } else {
                ApplyReceive currReceive = applyReceives.get(i);
                BigDecimal receiveAmount = currReceive.getReceiveAmount();
                Date receiveDate = currReceive.getReceiveDate();
                currAmount = currAmount.add(receiveAmount);
                log.info("下游收款金额:{}, 下游收款日期:{}", receiveAmount, DateUtil.formatDate(receiveDate));
                if (payAmount.compareTo(receiveAmount) > 0) {
                    resultList.add(new OverdueInterestVo(receiveAmount, payDate, receiveDate));
                    ApplyPay nextPay = new ApplyPay();
                    BeanUtils.copyProperties(currPay, nextPay);
                    nextPay.setPayAmount(payAmount.subtract(receiveAmount));
                    applyPays.add(i + 1, nextPay);
                    log.info("addPay, payAmount:{}", nextPay.getPayAmount());
                    log.info(">>>>1. currAmount:{}, currPayDate:{}, currReceiveDate:{}", receiveAmount, DateUtil.formatDate(payDate), DateUtil.formatDate(receiveDate));
                } else if (payAmount.compareTo(receiveAmount) < 0) {
                    resultList.add(new OverdueInterestVo(payAmount, payDate, (currAmount.compareTo(dealedAmount) < 0 ? payAmount : receiveAmount), receiveDate));
                    ApplyReceive nextReceive = new ApplyReceive();
                    BeanUtils.copyProperties(currReceive, nextReceive);
                    nextReceive.setReceiveAmount(receiveAmount.subtract(payAmount));
                    applyReceives.add(i + 1, nextReceive);
                    log.info("addReceive, receiveAmount:{}", nextReceive.getReceiveAmount());
                    log.info(">>>>2. currAmount:{}, currPayDate:{}, currReceiveDate:{}", payAmount, DateUtil.formatDate(payDate), DateUtil.formatDate(receiveDate));
                } else {
                    log.info(">>>>3. currAmount:{}, currPayDate:{}, currReceiveDate:{}", receiveAmount, DateUtil.formatDate(payDate), DateUtil.formatDate(receiveDate));
                    resultList.add(new OverdueInterestVo(receiveAmount, payDate, receiveDate));
                }
            }
        }
        return resultList;
    }

    /**
     * 组装、拆分 计算参数
     *
     * @param entity        中游合同
     * @param sellContract  下游合同
     * @param applyPays     中游付款单列表
     * @param applyReceives 下游收款单列表
     * @return 组装计算参数
     */
    private List<OverdueInterestVo> disassembleParams2(ApplyCtrDCSX entity, CtrContract sellContract, CtrContract buyContract,
                                                      List<ApplyPay> applyPays, List<ApplyReceive> applyReceives,
                                                      BigDecimal payRefundAmount, BigDecimal receiveRefundAmount) {
        List<OverdueInterestVo> resultList = new ArrayList<>();
        applyPays = buildPayList(applyPays, payRefundAmount);
        applyReceives = buildReceiveList(applyReceives, receiveRefundAmount);
        log.info("contractNo:{}-------------------------------------------------------------------------", entity.getContractNo());
        BigDecimal dealedAmount = Objects.nonNull(buyContract) ? buyContract.getDealedAmount() : sellContract.getDealedAmount();
        BigDecimal currAmount = BigDecimal.ZERO;
        for (int i = 0; i < applyReceives.size(); i++) {
            ApplyReceive currReceive = applyReceives.get(i);
            BigDecimal receiveAmount = currReceive.getReceiveAmount();
            Date receiveDate = currReceive.getReceiveDate();
            log.info("上游收款金额:{}, 上游收款日期:{}", receiveAmount, DateUtil.formatDate(receiveDate));
            if (CollectionUtils.isEmpty(applyPays) || applyPays.size() <= i || Objects.isNull(applyPays.get(i))) {
                log.info("无对应中游付款数据");
                resultList.add(new OverdueInterestVo(receiveAmount, receiveDate));
            } else {
                ApplyPay currPay = applyPays.get(i);
                BigDecimal payAmount = currPay.getPayAmount();
                Date payDate = currPay.getPayDate();
                currAmount = currAmount.add(payAmount);
                log.info("中游付款金额:{}, 中游付款日期:{}", payAmount, DateUtil.formatDate(payDate));
                if (receiveAmount.compareTo(payAmount) > 0) {
                    resultList.add(new OverdueInterestVo(payAmount, receiveDate, payDate));
                    ApplyReceive nextReceive = new ApplyReceive();
                    BeanUtils.copyProperties(currReceive, nextReceive);
                    currReceive.setReceiveAmount(receiveAmount.subtract(payAmount));
                    applyReceives.add(i + 1, nextReceive);
                    log.info("addReceive, receiveAmount:{}", nextReceive.getReceiveAmount());
                    log.info(">>>>1. currAmount:{}, currReceiveDate:{}, currPayDate:{}", payAmount, DateUtil.formatDate(receiveDate), DateUtil.formatDate(payDate));
                } else if (receiveAmount.compareTo(payAmount) < 0) {
                    resultList.add(new OverdueInterestVo(receiveAmount, receiveDate, (currAmount.compareTo(dealedAmount) < 0 ? receiveAmount : payAmount), payDate));
                    ApplyPay nextPay = new ApplyPay();
                    BeanUtils.copyProperties(currPay, nextPay);
                    nextPay.setPayAmount(payAmount.subtract(receiveAmount));
                    applyPays.add(i + 1, nextPay);
                    log.info("addPay, payAmount:{}", nextPay.getPayAmount());
                    log.info(">>>>2. currAmount:{}, currReceiveDate:{}, currPayDate:{}", receiveAmount, DateUtil.formatDate(receiveDate), DateUtil.formatDate(payDate));
                } else {
                    log.info(">>>>3. currAmount:{}, currReceiveDate:{}, currPayDate:{}", payAmount, DateUtil.formatDate(receiveDate), DateUtil.formatDate(payDate));
                    resultList.add(new OverdueInterestVo(payAmount, receiveDate, payDate));
                }
            }
        }
        return resultList;
    }

    private List<String> convertBuyContractNo(List<String> sellContracts){
        return sellContracts.stream()
                .map(t -> t.replaceAll("SPTS", "SPTB"))
                .map(t -> t.replaceAll("KCS", "KCB"))
                .map(t -> t.replaceAll("XYS", "XYB"))
                .collect(Collectors.toList());
    }

    private Map<String, String> convertContractNo(List<ApplyCtrDCSX> contractList) {
        Map<String, String> contractNoMap = new HashMap<>();
        contractList.forEach(t -> {
            String contractNo = t.getContractNo();
            if (verifySpecialSGX(t)){
                contractNoMap.put(contractNo, getSpecialSellContractNo(contractNo));
            } else {
                String sellContractNo = contractNo.replaceAll("SPTX", "SPTS");
                sellContractNo = sellContractNo.replaceAll("KCX", "KCS");
                sellContractNo = sellContractNo.replaceAll("XYX", "XYS");
                contractNoMap.put(sellContractNo, contractNo);
            }
        });
        return contractNoMap;
    }

    private Boolean verifySpecialSGX(ApplyCtrDCSX entity) {
        return StringUtils.equals(BasConstants.COMPANY_NAME_SUGX, entity.getCompanyName())
                && StringUtils.equals(BasConstants.COMPANY_NAME_SHZG, entity.getOurCompanyName());
    }

    private Boolean verifySpecialHB(ApplyCtrDCSX entity) {
        return StringUtils.equals(BasConstants.COMPANY_NAME_YYHB, entity.getCompanyName())
                && StringUtils.equals(BasConstants.COMPANY_NAME_ZJWS, entity.getOurCompanyName());
    }

    private String dcscNoToBuyNo(String contractNo){
        String buyContractNo = contractNo.replaceAll("SPTX", "SPTB");
        buyContractNo = buyContractNo.replaceAll("KCX", "KCB");
        buyContractNo = buyContractNo.replaceAll("XYX", "XYB");
        return buyContractNo;
    }

    private String getSpecialSellContractNo(String contractNo) {
        String buyContractNo = contractNo.replaceAll("SPTX", "SPT1");
        buyContractNo = buyContractNo.replaceAll("KCX", "KC1");
        buyContractNo = buyContractNo.replaceAll("XYX", "XY1");
        return buyContractNo;
    }

    private Map<String, List<ApplyReceive>> convertReceiveMapKey(Map<String, List<ApplyReceive>> receiveMap, Map<String, String> contractNoMap) {
        Map<String, List<ApplyReceive>> resultMap = new HashMap<>();
        receiveMap.forEach((k, v) -> {
            String newKey = contractNoMap.get(k);
            if (StringUtils.isNotBlank(newKey)) {
                resultMap.put(newKey, v);
            }
        });
        return resultMap;
    }

    private Map<String, BigDecimal> convertReceiveRefundMapKey(Map<String, BigDecimal> receiveRefundMap, Map<String, String> contractNoMap) {
        Map<String, BigDecimal> resultMap = new HashMap<>();
        receiveRefundMap.forEach((k, v) -> {
            String newKey = contractNoMap.get(k);
            if (StringUtils.isNotBlank(newKey)) {
                resultMap.put(newKey, v);
            }
        });
        return resultMap;
    }

    private List<ApplyPay> buildPayList(List<ApplyPay> applyPays, BigDecimal payRefundAmount){
        applyPays = applyPays.stream().sorted(Comparator.comparing(ApplyPay::getPayDate)).collect(Collectors.toList());
        if (Objects.isNull(payRefundAmount) || payRefundAmount.compareTo(BigDecimal.ZERO) <= 0){
            return applyPays;
        }
        for (int i = applyPays.size() - 1; i >= 0; i--) {
            if (payRefundAmount.compareTo(BigDecimal.ZERO) <= 0) {
                break;
            }
            BigDecimal payAmount = applyPays.get(i).getPayAmount();
            if (payAmount.compareTo(payRefundAmount) > 0) {
                applyPays.get(i).setPayAmount(payAmount.subtract(payRefundAmount));
            } else {
                applyPays.remove(i);
                payRefundAmount = payRefundAmount.subtract(payAmount);
            }
        }
        return applyPays;
    }

    private List<ApplyReceive> buildReceiveList(List<ApplyReceive> applyReceives, BigDecimal receiveRefundAmount){
        if (CollectionUtils.isNotEmpty(applyReceives)) {
            applyReceives = applyReceives.stream().sorted(Comparator.comparing(ApplyReceive::getReceiveDate)).collect(Collectors.toList());
            for (int i = applyReceives.size() - 1; i > 0; i--) {
                if (receiveRefundAmount.compareTo(BigDecimal.ZERO) <= 0) {
                    break;
                }
                BigDecimal receiveAmount = applyReceives.get(i).getReceiveAmount();
                if (receiveAmount.compareTo(receiveRefundAmount) > 0) {
                    applyReceives.get(i).setReceiveAmount(receiveAmount.subtract(receiveRefundAmount));
                } else {
                    applyReceives.remove(i);
                    receiveRefundAmount = receiveRefundAmount.subtract(receiveAmount);
                }
            }
        }
        return applyReceives;
    }

    private static Date formatterDate(Date date) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(DateOperator.FORMAT_STR);
            if (date == null) {
                date = new Date();
            }
            String format = sdf.format(date);
            date = sdf.parse(format);
        } catch (ParseException e) {
            log.error("formatterDate error:{}", date);
        }
        return date;
    }
}
