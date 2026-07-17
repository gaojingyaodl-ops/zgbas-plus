package com.spt.bas.server.service.impl;

import com.spt.auth.sdk.entity.SysDeptSdk;
import com.spt.auth.sdk.open.IAuthOpenFacade;
import com.spt.auth.sdk.vo.DeptSearchVo;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.constant.OwnRegionEnum;
import com.spt.bas.client.entity.*;
import com.spt.bas.client.vo.*;
import com.spt.bas.report.client.remote.IRptBisPmApproveClient;
import com.spt.bas.report.client.vo.RptCtrContractSettlementDateSearchVo;
import com.spt.bas.report.client.vo.RptCtrContractSettlementDateVo;
import com.spt.bas.server.dao.*;
import com.spt.bas.server.service.*;
import com.spt.bas.server.util.BasCollectionUtils;
import com.spt.bas.server.util.CommissionCalculateUtil;
import com.spt.pm.annotation.ServerTransactional;
import com.spt.pm.constant.PmConstants;
import com.spt.pm.entity.PmApprove;
import com.spt.pm.service.IBsKeySequenceService;
import com.spt.pm.service.IPmApproveService;
import com.spt.tools.core.bean.PageSearchVo;
import com.spt.tools.core.date.DateOperator;
import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.persistence.WebUtil;
import com.spt.tools.jpa.service.BaseService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
import java.util.concurrent.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@Transactional(readOnly = true)
public class CtrContractSettlementServiceImpl extends BaseService<CtrContractSettlement> implements ICtrContractSettlementService {
    @Autowired
    private CtrContractSettlementDao ctrContractSettlementDao;
    @Autowired
    private CtrContractSettlementAmountDao settlementAmountDao;
    @Autowired
    private ICtrContractSettlementAmountService settlementAmountService;
    @Autowired
    private IBsProductConfigService bsProductConfigService;
    @Autowired
    private ICtrContractService ctrContractService;
    @Autowired
    private CtrContractDao ctrContractDao;
    @Autowired
    private IBsKeySequenceService bsKeySequenceService;
    @Autowired
    private IBsMatchProfitsConfigService bsMatchProfitsConfigService;
    @Autowired
    private CtrProductDao ctrProductDao;
    @Autowired
    private CommissionCalculateUtil calculateUtil;
    @Autowired
    private IBsCompanyService bsCompanyService;
    @Autowired
    private ApplyReceiveDao applyReceiveDao;
    @Autowired
    private BudgetSettlementOphisDao budgetSettlementOphisDao;
    @Autowired
    private IAuthOpenFacade authOpenFacade;
    @Autowired
    private ICtrContractApplyService ctrContractApplyService;
    @Autowired
    private IPmApproveService pmApproveService;
    @Resource
    private StockVirtualDao stockVirtualDao;
    @Resource
    private IRptBisPmApproveClient bisPmApproveClient;
    @Resource
    private IBsCompanyConfigService bsCompanyConfigService;

    @Override
    public BaseDao<CtrContractSettlement> getBaseDao() {
        return ctrContractSettlementDao;
    }

    @Override
    public Class<CtrContractSettlement> getEntityClazz() {
        return CtrContractSettlement.class;
    }

    @Override
    @ServerTransactional
    public void saveSettlement(CtrProduct buyProduct, CtrProduct sellProduct) {
        CtrContract sellContract = ctrContractService.getEntity(sellProduct.getCtrContractId());
        CtrContract buyContract = ctrContractService.getEntity(buyProduct.getCtrContractId());

        // 设置合同结算单字段
        CtrContractSettlement settlement = setDefaultParam(buyProduct, sellProduct, sellContract, buyContract);

        // 根据账期获取保费税率
        Map<String, Object> mapDefault = new HashMap<>();
        mapDefault.put("creditCycle", sellContract.getCreditCycle());
        List<CtrCalCulateInsuranceParam> insuranceParamList = bsProductConfigService.findCtrCalculateInsuranceRates(sellContract.getEnterpriseId());
        BigDecimal insuranceRate = calculateUtil.getInsuranceRate(sellContract, mapDefault, insuranceParamList);
        logger.info("saveSettlement insuranceRate:{}", insuranceRate);
        settlement.setInsuranceRate(insuranceRate);

        String settlementCode = bsKeySequenceService.getNextKey(BasConstants.KEY_SETTLEMENT_NO, sellContract.getEnterpriseId());
        settlement.setSettlementCode(settlementCode);
        ctrContractSettlementDao.save(settlement);

        CtrContractSettlementAmount settlementAmount = parseSettlementAmount(settlement, sellContract);
        settlementAmountDao.save(settlementAmount);

        CtrProduct product = ctrProductDao.findOne(sellProduct.getId());
        if (StringUtils.isBlank(product.getSettlementCode())) {
            product.setSettlementCode(settlementCode);
            ctrProductDao.save(product);
        }

        sellContract.setInsuranceRate(insuranceRate);
        if (insuranceRate != null) {
            BigDecimal insuranceAmount = sellContract.getTotalAmount().multiply(insuranceRate).setScale(2, RoundingMode.HALF_UP);
            sellContract.setInsuranceAmount(insuranceAmount);
        }
        ctrContractDao.save(sellContract);
    }

    private CtrContractSettlement setDefaultParam(CtrProduct buyProduct, CtrProduct sellProduct, CtrContract sellContract, CtrContract buyContract) {
        //保存结算记录
        CtrContractSettlement settlement = new CtrContractSettlement();
        settlement.setContractTime(sellContract.getContractTime());
        settlement.setBusinessType(sellContract.getBusinessType());
        settlement.setSellContractId(sellProduct.getCtrContractId());
        settlement.setSellContractNo(sellContract.getContractNo());
        settlement.setBuyContractId(buyProduct.getCtrContractId());
        settlement.setBuyContractNo(buyContract.getContractNo());
        settlement.setBuyCompanyId(buyContract.getCompanyId());
        settlement.setBuyCompanyName(buyContract.getCompanyName());
        settlement.setSellCompanyId(sellContract.getCompanyId());
        settlement.setSellCompanyName(sellContract.getCompanyName());
        settlement.setBuyOurCompanyName(buyContract.getOurCompanyName());
        settlement.setSellOurCompanyName(sellContract.getOurCompanyName());
        settlement.setProductName(sellProduct.getProductName());
        settlement.setBrandNumber(sellProduct.getBrandNumber());
        settlement.setFactoryName(sellProduct.getFactoryName());
        settlement.setDealNumber(sellProduct.getDealNumber());
        settlement.setSellPrice(sellProduct.getDealPrice());
        settlement.setBuyPrice(buyProduct.getDealPrice());
        settlement.setTransportAmount(buyContract.getTransportAmount().add(settlement.verifyFLK() ? BigDecimal.ZERO : sellContract.getTransportAmount()));
        settlement.setWarehouseAmount(buyContract.getWarehouseAmount().add(settlement.verifyFLK() ? BigDecimal.ZERO : sellContract.getWarehouseAmount()));
        settlement.setSteveDorageAmount(buyContract.getStevedorage().add(settlement.verifyFLK() ? BigDecimal.ZERO : sellContract.getStevedorage()));
        settlement.setBuyMatchUserId(buyContract.getMatchUserId());
        settlement.setBuyMatchUserName(buyContract.getMatchUserName());
        settlement.setSellMatchUserId(sellContract.getMatchUserId());
        settlement.setSellMatchUserName(sellContract.getMatchUserName());
        if (settlement.verifyAfterJuneFlg()){
            settlement.setBuyHeadUserId(BasConstants.WU_FAN_USER_ID);
        }
        if (Objects.isNull(settlement.getBuyHeadUserId())) {
            settlement.setBuyHeadUserId(getLeaderId(buyContract.getMatchUserId(), buyContract.getEnterpriseId()));
        }
        if (Objects.isNull(settlement.getSellHeadUserId())) {
            settlement.setSellHeadUserId(getLeaderId(sellContract.getMatchUserId(), sellContract.getEnterpriseId()));
        }
        settlement.setStatus(BasConstants.SETTLEMENT_STATUS_I);
        settlement.setDeliveryFee(sellContract.getDeliveryFee());
        settlement.setPayFullTime(buyContract.getPayFullTime());
        settlement.setAppointPayDate(sellContract.getAppointPayFullTime());
        settlement.setBreachDay(0L);
        settlement.setCreditCycle(Objects.isNull(sellContract.getCreditCycle()) ? 0L : sellContract.getCreditCycle());
        settlement.setFinancialCreditDays(Objects.isNull(sellContract.getCreditCycle()) ? 0L : sellContract.getCreditCycle());
        settlement.setBuyTotalAmount(buyContract.getTotalAmount());
        settlement.setSellTotalAmount(sellContract.getTotalAmount());
        settlement.setBreachAmount(BigDecimal.ZERO);
        settlement.setEnterpriseId(sellContract.getEnterpriseId());
        settlement.setMatchCreditFlg(sellContract.getMatchCreditFlg());
        settlement.setVirtualFlg(Objects.nonNull(buyContract.getVirtualContractId()));
        settlement.setEnableFlg(true);
        settlement.setReceiveFlg(false);
        settlement.setConfirmFlg(false);
        settlement.setBillFlg(false);
        settlement.setSettleStatus(BasConstants.SETTLEMENT_ENUM.SETTLEMENT_STATUS_0);
        settlement.setSettleTotalFlg(false);
        settlement.setHasSettlementAmount(BigDecimal.ZERO);
        settlement.setNoneSettlementAmount(BigDecimal.ZERO);

        settlement.setApproveId(sellContract.getApproveId());
        PmApprove pmApprove = pmApproveService.findApproveNoByApproveId(sellContract.getApproveId());
        if (Objects.nonNull(pmApprove)) {
            settlement.setApproveNo(pmApprove.getApproveNo());
        }
        settlement.setDeptId(sellContract.getDeptId());
        SysDeptSdk dept = authOpenFacade.findDeptById(sellContract.getDeptId());
        if (Objects.nonNull(dept)) {
            settlement.setDeptName(dept.getDeptName());
        }
        settlement.setBusinessTypeDcsx(sellContract.getBusinessTypeDcsx());
        settlement.setContractStatus(sellContract.getContractStatus());
        settlement.setFinalFlag(Boolean.FALSE.equals(settlement.getMatchCreditFlg()) ||
                (Boolean.TRUE.equals(settlement.getMatchCreditFlg()) && Boolean.TRUE.equals(settlement.getVirtualFlg())));
        settlement.setBuyPrice(settlement.dealWithFLKPrice());
        settlement.setBuyTotalAmount(settlement.dealWithFLKAmount());

        settlement.setMarkSupplierFlag(false);
        settlement.setSupplierManagerUserId(null);
        settlement.setSupplierManagerCommission(BigDecimal.ZERO);
        settlement.setSupplierManagerAmount(BigDecimal.ZERO);
        BsCompany buyCompany = bsCompanyService.getEntity(settlement.getBuyCompanyId());
        if (Objects.nonNull(buyCompany) && BooleanUtils.isTrue(buyCompany.getMarkSupplierFlag())) {
            settlement.setMarkSupplierFlag(true);
            settlement.setSupplierManagerUserId(buyCompany.getSupplierManagerUserId());
            settlement.setSupplierManagerCommission(new BigDecimal("0.03"));
            if (StringUtils.equals(OwnRegionEnum.REGION_KH.getRegionCode(), sellContract.getOwningRegion())){
                settlement.setSellHeadCommission(new BigDecimal("0.07"));
            }
        }
        if (!StringUtils.equals(OwnRegionEnum.REGION_KH.getRegionCode(), sellContract.getOwningRegion())){
            settlement.setSellHeadCommission(new BigDecimal("0.03"));
        }
        if (Objects.nonNull(buyCompany) && BooleanUtils.isFalse(buyCompany.getBuyCommissionFlag())){
            settlement.setBuyCommission(BigDecimal.ZERO);
            settlement.setBuyHeadCommission(BigDecimal.ZERO);
            settlement.setBuyMatchAmount(BigDecimal.ZERO);
            settlement.setBuyHeadCommissionAmount(BigDecimal.ZERO);
        }
        return settlement;
    }

    /**
     * 获取默认计算配置项
     *
     * @param settlement
     * @return
     */
    @Override
    public CtrCalCulateParam getCalculateParamByUserId(CtrContractSettlement settlement) {
        // 默认计算配置项
        Long matchUserId = settlement.getSellMatchUserId();
        Long enterpriseId = settlement.getEnterpriseId();
        Long companyId = settlement.getSellCompanyId();
        CtrCalCulateParam ctrCalculateParam = bsProductConfigService.findCtrCalculateParam(enterpriseId);
        if (Objects.isNull(ctrCalculateParam)) {
            return new CtrCalCulateParam();
        }

        // 业务员利润配置
        List<BsMatchProfitsConfig> matchProfitsConfigs = bsMatchProfitsConfigService.findByMathUserId(matchUserId);
        if (CollectionUtils.isNotEmpty(matchProfitsConfigs)) {
            BsMatchProfitsConfig matchConfig = matchProfitsConfigs.get(0);
            ctrCalculateParam.setBuyCommissionRate(matchConfig.getBuyCommissionRate());
            ctrCalculateParam.setSellCommissionRate(matchConfig.getSellCommissionRate());
            ctrCalculateParam.setMarketingRate(matchConfig.getMarketingRate());
            ctrCalculateParam.setCompanyRate(matchConfig.getCompanyRate());
            ctrCalculateParam.setBuyHeadCommission(matchConfig.getBuyHeadCommissionRate());
            ctrCalculateParam.setSellHeadCommission(matchConfig.getSellHeadCommissionRate());
        }

        // 判断是否参与采购提成计算
        BsCompany buyCompany = bsCompanyService.getEntity(settlement.getBuyCompanyId());
        if (Objects.nonNull(buyCompany) && Objects.nonNull(buyCompany.getBuyCommissionFlag()) && BooleanUtils.isFalse(buyCompany.getBuyCommissionFlag())){
            logger.info("采购方不参与采购提成计算，采购方:{}", settlement.getBuyCompanyName());
            ctrCalculateParam.setBuyCommissionRate(BigDecimal.ZERO);
            ctrCalculateParam.setBuyCommissionRateDc(BigDecimal.ZERO);
            ctrCalculateParam.setBuyHeadCommission(BigDecimal.ZERO);
            ctrCalculateParam.setBuyHeadCommissionDc(BigDecimal.ZERO);
            ctrCalculateParam.setBuyCommissionFlag(false);
            settlement.setBuyCommission(BigDecimal.ZERO);
            settlement.setBuyHeadCommission(BigDecimal.ZERO);
        }

        // 根据企业等级获取服务费率
        BsCompany entity = bsCompanyService.getEntity(companyId);
        if (Objects.nonNull(entity) && StringUtils.isNotBlank(entity.getCompanyGrade())) {
            Map<String, Object> mapDefault = new HashMap<>();
            mapDefault.put("companyGrade", entity.getCompanyGrade());
            List<ParamByCompanyGrade> companyGradeList = bsProductConfigService.getParamByCompanyGrade(entity.getEnterpriseId());
            ParamByCompanyGrade param = calculateUtil.getParamByCompanyGrand(mapDefault, companyGradeList);
            if (Objects.nonNull(param) && Objects.nonNull(param.getServeRate())) {
                logger.info("getCalculateParamByUserId companyName:{},companyGrade:{}, serveRate:{}", entity.getCompanyName(),
                        entity.getCompanyGrade(), param.getServeRate());
                ctrCalculateParam.setServeRate(param.getServeRate());
            }
        }
        if (settlement.verifyAfterJulyFlg() && settlement.getMatchCreditFlg()
                && !settlement.getVirtualFlg() && Objects.nonNull(settlement.getSellCommission())
                && settlement.getSellCommission().compareTo(BigDecimal.ZERO) != 0) {
            ctrCalculateParam.setSellCommissionRate(settlement.getSellCommission());
        }
        // 特户企业特殊逻辑
        try {
            BsCompanyConfig companyConfig = bsCompanyConfigService.findByBsCompanyIdAndMatchUserId(companyId, matchUserId);
            if (Objects.nonNull(companyConfig) && settlement.verifyAfterJuneFlg()) {
                BigDecimal serviceRate = companyConfig.getServiceRate();
                BigDecimal sellCommissionRate = companyConfig.getSellCommissionRate();
                if (Objects.nonNull(serviceRate) && serviceRate.compareTo(BigDecimal.ZERO) != 0) {
                    ctrCalculateParam.setServeRate(serviceRate);
                }
                if (Objects.nonNull(sellCommissionRate) && sellCommissionRate.compareTo(BigDecimal.ZERO) != 0) {
                    ctrCalculateParam.setSellCommissionRate(sellCommissionRate);
                }
            }
        } catch (Exception e) {
            logger.error("getCalculateParamByUserId", e);
        }
        return ctrCalculateParam;
    }

    /**
     * 更新结算单
     */
    @Override
    @ServerTransactional
    public void calculateCommission(List<CtrContractSettlement> settlementList) throws InterruptedException, ExecutionException {
        logger.info("更新结算单Begin: num：{}", settlementList.size());
        if (CollectionUtils.isEmpty(settlementList)) {
            logger.info("结算单已全部完成,任务终止!");
            return;
        }
        ExecutorService executorService = Executors.newCachedThreadPool();
        ExecutorCompletionService<String> execu = new ExecutorCompletionService<>(executorService);
        int taskSize = settlementList.size();
        float bathSize = 50F;
        int bath = (int) Math.ceil((taskSize / bathSize));
        for (int i = 0; i < bath; i++) {
            int start = (int) (bathSize * i);
            int end = (int) (start + bathSize);
            end = Math.min(end, taskSize);
            List<CtrContractSettlement> list = settlementList.subList(start, end);
            execu.submit(() -> {
                updateSettlement(list);
                return "OK";
            });
        }
        for (int i = 0; i < bath; i++) {
            Future<String> future = execu.take();
            logger.info("result:{},{}", i, future.get());
        }
        executorService.shutdown();
        logger.info("更新结算单End");
    }

    /**
     * 手动标识结算
     *
     * @param settlementIds
     */
    @Override
    @ServerTransactional
    public void markSettlement(List<Long> settlementIds) {
        logger.info("markSettlement settlementIds:{}", settlementIds);
        ctrContractSettlementDao.markSettlement(BasConstants.SETTLEMENT_ENUM.SETTLEMENT_STATUS_D, new Date(), settlementIds);
    }

    /**
     * 刷新结算数据
     *
     * @param settlementIds
     */
    @Override
    @ServerTransactional
    public void refreshSettlement(List<Long> settlementIds) throws ExecutionException, InterruptedException {
        List<CtrContractSettlement> settlementList = ctrContractSettlementDao.findBySettlementIds(settlementIds);
        calculateCommission(settlementList);
        ctrContractSettlementDao.updateSettleStatus(BasConstants.SETTLEMENT_ENUM.SETTLEMENT_STATUS_0, null, settlementIds);
    }

    @Override
    @ServerTransactional
    public void refreshAllSettlement() throws ExecutionException, InterruptedException {
        List<CtrContractSettlement> settlementList = ctrContractSettlementDao.findCalculateCommissionList();
        calculateCommission(settlementList);
    }

    /**
     * 决算
     *
     * @param settlementQuery 决算Vo
     */
    @Override
    @ServerTransactional
    public void finalAccount(CtrContractSettlement settlementQuery) throws ExecutionException, InterruptedException {
        Date finalDate = settlementQuery.getSummaryDate();
        Long sellMatchUserId = settlementQuery.getSellMatchUserId();
        String sellMatchUserName = settlementQuery.getSellMatchUserName();
        logger.info("finalAccount finalDate:{}, sellMatchUserId:{}, sellMatchUserName:{}", finalDate, sellMatchUserId, sellMatchUserName);
        if (Objects.isNull(finalDate)) {
            return;
        }
        if (finalDate.before(DateOperator.parse("2024-07","yyyy-MM"))) {
            logger.info("finalAccount finalDate:{} can't less than 2024-07", finalDate);
            return;
        }
        // 正常结算单列表
        List<CtrContractSettlement> finalAccountList;
        // 标记供应商结算单列表
        List<CtrContractSettlement> markFinalAccountList;
        // 改性塑料事业部结算单列表
        List<CtrContractSettlement> khFinalAccountList;
        if (Objects.nonNull(sellMatchUserId)) {
            finalAccountList = ctrContractSettlementDao.getfinalAccountList(finalDate, sellMatchUserId);
            markFinalAccountList = ctrContractSettlementDao.getMarkFinalAccountList(finalDate, sellMatchUserId);
            khFinalAccountList = ctrContractSettlementDao.getKhFinalAccountList(finalDate, sellMatchUserId);
        } else {
            finalAccountList = ctrContractSettlementDao.getfinalAccountList(finalDate);
            markFinalAccountList = ctrContractSettlementDao.getMarkFinalAccountList(finalDate);
            khFinalAccountList = ctrContractSettlementDao.getKhFinalAccountList(finalDate);
        }
        if (CollectionUtils.isEmpty(finalAccountList) && CollectionUtils.isEmpty(markFinalAccountList)) {
            return;
        }
        logger.info("finalAccount size:{}", finalAccountList.size());
        logger.info("markFinalAccountList size:{}", markFinalAccountList.size());
        if (CollectionUtils.isNotEmpty(finalAccountList)){
            Map<Long, List<CtrContractSettlement>> settlementMap = finalAccountList.stream().collect(Collectors.groupingBy(CtrContractSettlement::getSellMatchUserId));
            Map<Long, SettlementCommissionVo> finalMatchMap = new HashMap<>();
            settlementMap.forEach((k, v) -> {
                BigDecimal grossProfit = v.stream()
                        .map(settlement -> settlement.getSellTotalAmount()
                                .subtract(settlement.getBuyTotalAmount())
                                .subtract(settlement.getWarehouseAmount())
                                .subtract(settlement.getTransportAmount())
                                .subtract(settlement.getDeliveryFee())
                                .subtract(settlement.getSteveDorageAmount()))
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                logger.info("finalAccountList sellMatchUserId:{}, grossProfit:{}", k, grossProfit);
                BigDecimal commissionRate = calculateUtil.getMatchCommissionRate(grossProfit);
                BigDecimal commissionRate2 = calculateUtil.getMatchCommissionRate2(grossProfit);
                finalMatchMap.put(k, new SettlementCommissionVo(commissionRate, commissionRate2));
            });
            finalAccountList.forEach(settlement -> {
                BigDecimal sellCommission = settlement.getSellCommission();
                SettlementCommissionVo commissionVo = finalMatchMap.get(settlement.getSellMatchUserId());
                settlement.setFinalFlag(true);
                BigDecimal commissionRate = commissionVo.getCommissionRate();
                BigDecimal commissionRate2 = commissionVo.getCommissionRate2();
                BigDecimal realCommissionRate = settlement.verifyAfterJuneFlg() ? commissionRate2 : commissionRate;
                if (sellCommission.compareTo(realCommissionRate) != 0) {
                    settlement.setSettlementDate(null);
                    settlement.setSellCommission(realCommissionRate);
                    settlement.setSettleStatus(BasConstants.SETTLEMENT_ENUM.SETTLEMENT_STATUS_0);
                }
            });
            ctrContractSettlementDao.saveAll(finalAccountList);
            this.calculateCommission(finalAccountList);
        }

        if (CollectionUtils.isNotEmpty(markFinalAccountList)){
            Map<Long, List<CtrContractSettlement>> markSettlementMap = markFinalAccountList.stream().collect(Collectors.groupingBy(CtrContractSettlement::getSellMatchUserId));
            Map<Long, SettlementCommissionVo> finalMarkMatchMap = new HashMap<>();
            markSettlementMap.forEach((k, v) -> {
                BigDecimal grossProfit = v.stream()
                        .map(settlement -> settlement.getSellTotalAmount()
                                .subtract(settlement.getBuyTotalAmount())
                                .subtract(settlement.getWarehouseAmount())
                                .subtract(settlement.getTransportAmount())
                                .subtract(settlement.getDeliveryFee())
                                .subtract(settlement.getSteveDorageAmount()))
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                logger.info("markFinalAccountList sellMatchUserId:{}, grossProfit:{}", k, grossProfit);
                BigDecimal commissionRate2 = calculateUtil.getMatchCommissionRate2(grossProfit);
                finalMarkMatchMap.put(k, new SettlementCommissionVo(commissionRate2));
            });
            markFinalAccountList.forEach(settlement -> {
                BigDecimal sellCommission = settlement.getSellCommission();
                SettlementCommissionVo commissionVo = finalMarkMatchMap.get(settlement.getSellMatchUserId());
                settlement.setFinalFlag(true);
                BigDecimal commissionRate2 = commissionVo.getCommissionRate2();
                if (sellCommission.compareTo(commissionRate2) != 0) {
                    settlement.setSettlementDate(null);
                    settlement.setSellCommission(commissionRate2);
                    settlement.setSettleStatus(BasConstants.SETTLEMENT_ENUM.SETTLEMENT_STATUS_0);
                }
            });
            ctrContractSettlementDao.saveAll(markFinalAccountList);
            this.calculateCommission(markFinalAccountList);
        }

        if (CollectionUtils.isNotEmpty(khFinalAccountList)){
            Map<Long, List<CtrContractSettlement>> khSettlementMap = khFinalAccountList.stream().collect(Collectors.groupingBy(CtrContractSettlement::getSellMatchUserId));
            Map<Long, SettlementCommissionVo> finalMarkMatchMap = new HashMap<>();
            khSettlementMap.forEach((k, v) -> {
                BigDecimal grossProfit = v.stream()
                        .map(settlement -> settlement.getSellTotalAmount()
                                .subtract(settlement.getBuyTotalAmount())
                                .subtract(settlement.getWarehouseAmount())
                                .subtract(settlement.getTransportAmount())
                                .subtract(settlement.getDeliveryFee())
                                .subtract(settlement.getSteveDorageAmount()))
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                logger.info("khFinalAccountList sellMatchUserId:{}, grossProfit:{}", k, grossProfit);
                BigDecimal commissionRate2 = calculateUtil.getMatchCommissionRateKH(grossProfit);
                finalMarkMatchMap.put(k, new SettlementCommissionVo(commissionRate2));
            });
            khFinalAccountList.forEach(settlement -> {
                BigDecimal sellCommission = settlement.getSellCommission();
                SettlementCommissionVo commissionVo = finalMarkMatchMap.get(settlement.getSellMatchUserId());
                settlement.setFinalFlag(true);
                BigDecimal commissionRate2 = commissionVo.getCommissionRate2();
                if (sellCommission.compareTo(commissionRate2) != 0) {
                    settlement.setSettlementDate(null);
                    settlement.setSellCommission(commissionRate2);
                    settlement.setSettleStatus(BasConstants.SETTLEMENT_ENUM.SETTLEMENT_STATUS_0);
                }
            });
            ctrContractSettlementDao.saveAll(khFinalAccountList);
            this.calculateCommission(khFinalAccountList);
        }
    }

    @Override
    @ServerTransactional
    public void updateSettlementOphis(BudgetSettlementOphisVo ophisVo) {
        List<Long> settlementIds = ophisVo.getSettlementIds();
        String status = ophisVo.getStatus();
        String settleStatus = ophisVo.getSettleStatus();
        ctrContractSettlementDao.markSettlement(status, new Date(), settlementIds);
        ctrContractSettlementDao.updateSettleStatus(settleStatus, new Date(), settlementIds);
        // 修改汇总标识
        ctrContractSettlementDao.updateSettleTotalFlg(settlementIds, false);

        // 添加操作记录
        List<CtrContractSettlement> settlementIdList = ctrContractSettlementDao.findBySettlementIds(settlementIds);
        List<BudgetSettlementOphis> insert = settlementIdList.stream().map(packageBudget(ophisVo)).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(insert)) {
            budgetSettlementOphisDao.saveAll(insert);
        }
        if (StringUtils.equals(BasConstants.SETTLEMENT_ENUM.SETTLEMENT_STATUS_3, settleStatus)) {
            // 标记结算金额表数据为已结算
            List<Long> ids = settlementIdList.stream().map(CtrContractSettlement::getId).collect(Collectors.toList());
            settlementAmountService.makeComplete(ids);
            // 已结算
            settlementIdList.forEach(entity -> settlementAmountService.refreshSettlementAmount(entity));
            ctrContractSettlementDao.saveAll(settlementIdList);
        }
    }

    private Function<CtrContractSettlement, BudgetSettlementOphis> packageBudget(BudgetSettlementOphisVo ophisVo) {
        return settlement -> {
            // 添加操作记录
            BudgetSettlementOphis ophis = new BudgetSettlementOphis();
            ophis.setId(0L);
            ophis.setBudgetSettlementId(settlement.getId());
            ophis.setCreateUserId(ophisVo.getMatchUserId());
            ophis.setCreateUserName(ophisVo.getMatchUserName());
            ophis.setSettleStatus(ophisVo.getSettleStatus());
            return ophis;
        };
    }

    @Override
    public CtrContractSettlement sumPageSettlement(PageSearchVo searchVo) {
        Specification<CtrContractSettlement> spe = WebUtil.buildSpecification(searchVo.getSearchParams());
        // 下面部分原封不动提取成一个方法
        return getCtrContractSettlement(spe);
    }

    /**
     * 本方法提取自sumPageSettlement，没做任何改动！
     *
     * @param spe
     * @return
     */
    private CtrContractSettlement getCtrContractSettlement(Specification<CtrContractSettlement> spe) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<?> query = cb.createQuery();
        Root<CtrContractSettlement> root = query.from(CtrContractSettlement.class);
        CriteriaQuery<?> cq = query.where(spe.toPredicate(root, query, cb)).multiselect(
                cb.sum(root.get("dealNumber")), cb.sum(root.get("sellTotalAmount")), cb.sum(root.get("buyTotalAmount")),
                cb.sum(root.get("vatSpreadAmount")), cb.sum(root.get("vatAmount")), cb.sum(root.get("printAmount")),
                cb.sum(root.get("taxesSurchargesAmount")), cb.sum(root.get("afterTaxSpreadAmount")),
                cb.sum(root.get("sellHeadCommissionAmount")), cb.sum(root.get("buyHeadCommissionAmount")),
                cb.sum(root.get("sellMatchAmount")), cb.sum(root.get("buyMatchAmount")), cb.sum(root.get("supplierManagerAmount")),
                cb.sum(root.get("sellHeadCommissionTotalAmount")), cb.sum(root.get("buyHeadCommissionTotalAmount")),
                cb.sum(root.get("sellMatchTotalAmount")), cb.sum(root.get("buyMatchTotalAmount")), cb.sum(root.get("supplierManagerTotalAmount")));
        TypedQuery<?> tq = em.createQuery(cq);
        Object[] result = ((Object[]) tq.getSingleResult());
        CtrContractSettlement sum = new CtrContractSettlement();
        BigDecimal dealNumber = (BigDecimal) result[0];
        BigDecimal sellTotalAmount = (BigDecimal) result[1];
        BigDecimal buyTotalAmount = (BigDecimal) result[2];
        BigDecimal vatSpreadAmount = (BigDecimal) result[3];
        BigDecimal vatAmount = (BigDecimal) result[4];
        BigDecimal printAmount = (BigDecimal) result[5];
        BigDecimal taxesSurchargesAmount = (BigDecimal) result[6];
        BigDecimal afterTaxSpreadAmount = (BigDecimal) result[7];
        BigDecimal sellHeadCommissionAmount = (BigDecimal) result[8];
        BigDecimal buyHeadCommissionAmount = (BigDecimal) result[9];
        BigDecimal sellMatchAmount = (BigDecimal) result[10];
        BigDecimal buyMatchAmount = (BigDecimal) result[11];
        BigDecimal supplierManagerAmount = (BigDecimal) result[12];
        BigDecimal sellHeadCommissionTotalAmount = (BigDecimal) result[13];
        BigDecimal buyHeadCommissionTotalAmount = (BigDecimal) result[14];
        BigDecimal sellMatchTotalAmount = (BigDecimal) result[15];
        BigDecimal buyMatchTotalAmount = (BigDecimal) result[16];
        BigDecimal supplierManagerTotalAmount = (BigDecimal) result[17];
        sum.setDealNumber(dealNumber);
        sum.setSellTotalAmount(sellTotalAmount);
        sum.setBuyTotalAmount(buyTotalAmount);
        sum.setVatSpreadAmount(vatSpreadAmount);
        sum.setVatAmount(vatAmount);
        sum.setPrintAmount(printAmount);
        sum.setTaxesSurchargesAmount(taxesSurchargesAmount);
        sum.setAfterTaxSpreadAmount(afterTaxSpreadAmount);
        sum.setSellHeadCommissionAmount(sellHeadCommissionAmount);
        sum.setBuyHeadCommissionAmount(buyHeadCommissionAmount);
        sum.setSellMatchAmount(sellMatchAmount);
        sum.setBuyMatchAmount(buyMatchAmount);
        sum.setSupplierManagerAmount(supplierManagerAmount);
        sum.setSellHeadCommissionTotalAmount(sellHeadCommissionTotalAmount);
        sum.setBuyHeadCommissionTotalAmount(buyHeadCommissionTotalAmount);
        sum.setSellMatchTotalAmount(sellMatchTotalAmount);
        sum.setBuyMatchTotalAmount(buyMatchTotalAmount);
        sum.setSupplierManagerTotalAmount(supplierManagerTotalAmount);
        return sum;
    }


    @Override
    @ServerTransactional
    public void updateSettleTotalFlg(List<Long> settlementId) {
        ctrContractSettlementDao.updateSettleTotalFlg(settlementId, true);
    }

    private void updateSettlement(List<CtrContractSettlement> settlementList) {
        List<Long> buyContractIdList = BasCollectionUtils.convertList(settlementList, CtrContractSettlement::getBuyContractId);
        List<Long> sellContractIdList = BasCollectionUtils.convertList(settlementList, CtrContractSettlement::getSellContractId);

        Map<Long, CtrContract> buyContractMap = BasCollectionUtils.convertMap(ctrContractService.findByIds(buyContractIdList), CtrContract::getId);
        Map<Long, CtrContract> sellContractMap = BasCollectionUtils.convertMap(ctrContractService.findByIds(sellContractIdList), CtrContract::getId);

        RptCtrContractSettlementDateSearchVo searchVo = new RptCtrContractSettlementDateSearchVo();
        searchVo.setBuyContractIdList(buyContractIdList);
        searchVo.setSellContractIdList(sellContractIdList);
        List<RptCtrContractSettlementDateVo> businessDateList = bisPmApproveClient.getSettlementBusinessDate(searchVo);
        Map<Long, RptCtrContractSettlementDateVo> businessDateMap = BasCollectionUtils.convertMap(businessDateList, RptCtrContractSettlementDateVo::getApproveId);
        settlementList.forEach(settlement -> {
            RptCtrContractSettlementDateVo businessDate = businessDateMap.get(settlement.getApproveId());
            CtrContract buyContract = buyContractMap.get(settlement.getBuyContractId());
            CtrContract sellContract = sellContractMap.get(settlement.getSellContractId());

            refreshCommissionField(settlement, sellContract, buyContract, businessDate);
            CtrCalCulateParam param = this.getCalculateParamByUserId(settlement);
            // 保理业务服务费费率按照0.0002计算
            BigDecimal realServeRate = this.getRealServeRate(sellContract, param);

            // 业务员罚息金额
            settlement.setMatchBreachAmount(calculateUtil.calculateBreachAmount(sellContract, settlement, param));

            // 金融服务费
            settlement.setFinancialServiceAmount(this.getFinancialServiceAmount(settlement, realServeRate, sellContract, buyContract));

            // 印花税
            settlement.setPrintAmount(calculateUtil.calculatePrintAmount(settlement, param));

            // 增值税税后差价
            settlement.setVatSpreadAmount(calculateUtil.calculateVatSpreadAmount(settlement));

            // 增值税
            settlement.setVatAmount(calculateUtil.calculateVatAmount(settlement, param));

            // 附加税
            settlement.setSurchargeAmount(calculateUtil.calculateSurchargeAmount(settlement, param));

            // 税金及附加
            settlement.setTaxesSurchargesAmount(calculateUtil.calculateSurchargesAmount(settlement));

            // 税后差价收入（利润）
            settlement.setAfterTaxSpreadAmount(calculateUtil.calculateAfterTaxSpreadAmount(settlement));

            // 更新合同结算金额、提成数据
            settlementAmountService.initSaveSettlementAmount(settlement, param);
            // 更新 已结算金额、未结算金额
            settlementAmountService.refreshSettlementAmount(settlement);
        });
        ctrContractSettlementDao.saveAll(settlementList);
    }

    private CtrContractSettlementAmount parseSettlementAmount(CtrContractSettlement settlement, CtrContract sellContract) {
        CtrContractSettlementAmount settlementAmount = new CtrContractSettlementAmount();
        settlementAmount.setSettlementId(settlement.getId());
        settlementAmount.setContractId(sellContract.getId());
        settlementAmount.setContractNo(sellContract.getContractNo());
        settlementAmount.setSettlementAmount(settlement.getAfterTaxSpreadAmount());
        settlementAmount.setSettlementStatus(BasConstants.SETTLEMENT_AMOUNT_ENUM.SETTLEMENT_STATUS_0);
        settlementAmount.setSettlementType(BasConstants.SETTLEMENT_AMOUNT_ENUM.SETTLEMENT_TYPE_0);
        return settlementAmount;
    }

    /**
     * 按照实际业务获取服务费费率
     *
     * @param sellContract
     * @param param
     * @return
     */
    private BigDecimal getRealServeRate(CtrContract sellContract, CtrCalCulateParam param) {
        BigDecimal realServeRate = param.getServeRate();
        if (BasConstants.BL_BUSINESS_CODE.contains(sellContract.getBusinessTypeDcsx())) {
            // 保理按照万2
            realServeRate = BigDecimal.valueOf(0.0002);
        } else if (sellContract.getContractTime().before(DateOperator.parse("2022-05-01 00:00:00"))) {
            // 2022年五月一日之前签订的非保理合同统一按照万3
            realServeRate = BigDecimal.valueOf(0.0003);
        }
        logger.info("contractNo:{},realServeRate:{}", sellContract.getContractNo(), realServeRate);
        return realServeRate;
    }

    /**
     * 金融服务费计算
     * 分别计算每批收款金额对应的付款金额所占用的金融服务费
     *
     * @param settlement
     * @param realServeRate
     * @param sellContract
     * @param buyContract
     * @return
     */
    private BigDecimal getFinancialServiceAmount(CtrContractSettlement settlement, BigDecimal realServeRate, CtrContract sellContract, CtrContract buyContract) {
        BigDecimal financialServiceAmount = BigDecimal.ZERO;
        BigDecimal discountTotalAmount = BigDecimal.ZERO;
        if (Boolean.FALSE.equals(settlement.getMatchCreditFlg())) {
            return financialServiceAmount;
        }
        List<ApplyReceive> receiveList = applyReceiveDao.findAppReceiveComplete(settlement.getSellContractId());
        if (CollectionUtils.isEmpty(receiveList)) {
            return financialServiceAmount;
        }
        for (ApplyReceive receive : receiveList) {
            BigDecimal discountAmount = receive.getDiscountAmount();
            String discountTarget = receive.getDiscountTarget();
            if (StringUtils.equals("Y", discountTarget) && Objects.nonNull(discountAmount)) {
                discountTotalAmount = discountTotalAmount.add(discountAmount);
            }
        }
        settlement.setDiscountAmount(discountTotalAmount);
        Date compareDate = Objects.isNull(sellContract.getAppointPayFullTime()) ? sellContract.getPayFullTime() : sellContract.getAppointPayFullTime();
        Long creditCycle = sellContract.getCreditCycle();
        BigDecimal sellTotalAmount = sellContract.getTotalAmount();
        BigDecimal buyTotalAmount = buyContract.getTotalAmount();
        BigDecimal breachAmount = sellContract.getBreachAmount();
        BigDecimal receiveBreachAmount = sellContract.getReceiveBreachAmount();
        for (ApplyReceive applyReceive : receiveList) {
            BigDecimal receiveAmount = applyReceive.getReceiveAmount();
            Date receiveDate = applyReceive.getReceiveDate();
            Date billDueTime = applyReceive.getBillDueTime();
            if (StringUtils.equals(BasConstants.PAY_MODE_H, applyReceive.getReceiveMode()) && Objects.nonNull(billDueTime)){
                receiveDate = billDueTime;
            }
            if (receiveBreachAmount.compareTo(breachAmount) >= 0 && receiveAmount.compareTo(breachAmount) == 0) {
                continue;
            }
            if (receiveAmount.compareTo(sellTotalAmount) > 0) {
                receiveAmount = sellTotalAmount;
            }
            BigDecimal calculateAmount = receiveAmount.divide(sellTotalAmount, 4, RoundingMode.HALF_UP).multiply(buyTotalAmount);
            long creditDays = DateOperator.compareDays(DateOperator.truncDate(compareDate), DateOperator.truncDate(receiveDate));
            creditDays = creditCycle + creditDays;
            // 每笔回款账期 = 逾期天数（提前还款为负数）+ 合同实际账期
            // creditDays = creditDays > creditCycle ? creditCycle : creditDays;
            creditDays = Math.max(creditDays, 1L);
            BigDecimal financial = calculateUtil.getFinancialServiceAmount2(calculateAmount, realServeRate, creditDays);
            financialServiceAmount = financialServiceAmount.add(financial);
            settlement.setFinancialCreditDays(creditDays);
            logger.info("getFinancialServiceAmount contractNo:{},calculateAmount:{},realServeRate:{},creditDays:{},financial:{}",
                    sellContract.getContractNo(), calculateAmount, realServeRate, creditDays, financial);
        }
        logger.info("getFinancialServiceAmount contractNo:{},financialServiceAmount:{}", sellContract.getContractNo(), financialServiceAmount);
        return financialServiceAmount;
    }


    /**
     * 更新业务结算单参数例如 交货日期、付款日期、约定付款日期等
     *
     * @param settlement
     * @param sellContract
     * @param buyContract
     */
    private void refreshCommissionField(CtrContractSettlement settlement, CtrContract sellContract,
                                        CtrContract buyContract, RptCtrContractSettlementDateVo businessDate) {
        settlement.setBuyTotalAmount(buyContract.getTotalAmount());
        settlement.setSellTotalAmount(sellContract.getTotalAmount());
        settlement.setBuyPrice(buyContract.getDealPrice());
        settlement.setSellPrice(sellContract.getDealPrice());
        settlement.setDealNumber(sellContract.getTotalNumber());
        settlement.setPayFullTime(buyContract.getPayFullTime());
        settlement.setReceiveDate(sellContract.getRealPayFullTime());
        settlement.setAppointPayDate(sellContract.getAppointPayFullTime());
        settlement.setConfirmReceiptDate(sellContract.getConfirmDate());
        settlement.setBreachDay(sellContract.getBreachDays());
        settlement.setBreachAmount(sellContract.getBreachAmount());

        settlement.setBuyPrice(settlement.dealWithFLKPrice());
        settlement.setBuyTotalAmount(settlement.dealWithFLKAmount());
        //采购销售 运输费 仓储费
        settlement.setWarehouseAmount(sellContract.getWarehouseAmount().add(settlement.verifyFLK() ? BigDecimal.ZERO : buyContract.getWarehouseAmount()));
        settlement.setTransportAmount(sellContract.getTransportAmount().add(settlement.verifyFLK() ? BigDecimal.ZERO : buyContract.getTransportAmount()));
        settlement.setDeliveryFee(sellContract.getDeliveryFee().add(settlement.verifyFLK() ? BigDecimal.ZERO : buyContract.getDeliveryFee()));
        settlement.setSteveDorageAmount(sellContract.getStevedorage().add(settlement.verifyFLK() ? BigDecimal.ZERO : buyContract.getStevedorage()));

        BigDecimal dealedAmount = Objects.isNull(sellContract.getDealedAmount()) ? BigDecimal.ZERO : sellContract.getDealedAmount();
        BigDecimal confirmReceiveNumber = Objects.isNull(sellContract.getConfirmReceiveNumber()) ? BigDecimal.ZERO : sellContract.getConfirmReceiveNumber();
        settlement.setReceiveFlg(dealedAmount.compareTo(sellContract.getTotalAmount()) >= 0);
        settlement.setConfirmFlg(confirmReceiveNumber.compareTo(sellContract.getTotalNumber()) >= 0);
        settlement.setBillFlg(buyContract.getBilledAmount().compareTo(buyContract.getTotalAmount()) >= 0);

        // 2025年6月1日成交的合同，采购负责人统一为【吴凡】
        if (settlement.verifyAfterJuneFlg()){
            settlement.setBuyHeadUserId(BasConstants.WU_FAN_USER_ID);
        }

        // 合同作废则结算数据置为无效
        String status = sellContract.getStatus();
        String contractStatus = sellContract.getContractStatus();
        if (StringUtils.equals(BasConstants.CONTRACTSTATUS_C, status) || StringUtils.equals(BasConstants.CONTRACTSTATUS_C, contractStatus)) {
            settlement.setEnableFlg(false);
        }

        // 更新收票日期
        Long buyContractId = Objects.nonNull(buyContract.getVirtualContractId()) ? buyContract.getVirtualContractId() : buyContract.getId();
        CtrContractApply contractApply = ctrContractApplyService.findByContractId(buyContractId);
        if (Objects.nonNull(contractApply)) {
            settlement.setReceiveBillDate(contractApply.getRealBillDate());
        }
        if (Objects.nonNull(buyContract.getVirtualContractId())) {
            CtrContract virtualContract = ctrContractService.getEntity(buyContract.getVirtualContractId());
            if (Objects.nonNull(virtualContract) && Objects.nonNull(virtualContract.getVirtualId())) {
                StockVirtual stockVirtual = stockVirtualDao.findOne(virtualContract.getVirtualId());
                settlement.setSellGuidePrice(Objects.nonNull(stockVirtual) ? stockVirtual.getMinSellPrice() : null);
            }
        }

        // 更新汇总日期
        if (Boolean.TRUE.equals(settlement.getBillFlg())
                && Boolean.TRUE.equals(settlement.getConfirmFlg())
                && Boolean.TRUE.equals(settlement.getReceiveFlg())
                && businessDate != null) {
            List<Date> compareDate = getDates(businessDate);
            if (CollectionUtils.isNotEmpty(compareDate)) {
                settlement.setSummaryDate(Collections.max(compareDate));
            }
        } else {
            settlement.setSummaryDate(null);
        }

        BsCompany buyCompany = bsCompanyService.getEntity(settlement.getBuyCompanyId());
        if (Objects.nonNull(buyCompany) && BooleanUtils.isTrue(buyCompany.getMarkSupplierFlag())) {
            settlement.setMarkSupplierFlag(true);
            settlement.setSupplierManagerUserId(buyCompany.getSupplierManagerUserId());
            settlement.setSupplierManagerCommission(new BigDecimal("0.03"));
            if (StringUtils.equals(OwnRegionEnum.REGION_KH.getRegionCode(), sellContract.getOwningRegion())){
                settlement.setSellHeadCommission(new BigDecimal("0.07"));
            }
        } else {
            settlement.setMarkSupplierFlag(false);
            settlement.setSupplierManagerUserId(null);
            settlement.setSupplierManagerCommission(BigDecimal.ZERO);
            settlement.setSupplierManagerAmount(BigDecimal.ZERO);
        }

        if (!StringUtils.equals(OwnRegionEnum.REGION_KH.getRegionCode(), sellContract.getOwningRegion())){
            settlement.setSellHeadCommission(new BigDecimal("0.03"));
        }

        if (Objects.nonNull(buyCompany) && BooleanUtils.isFalse(buyCompany.getBuyCommissionFlag())){
            settlement.setBuyCommission(BigDecimal.ZERO);
            settlement.setBuyHeadCommission(BigDecimal.ZERO);
            settlement.setBuyMatchAmount(BigDecimal.ZERO);
            settlement.setBuyHeadCommissionAmount(BigDecimal.ZERO);
        }
    }

    private static List<Date> getDates(RptCtrContractSettlementDateVo businessDate) {
        List<Date> compareDate = new ArrayList<>();
        Date receiveBillApproveDate = businessDate.getReceiveBillApproveDate();
        Date receiveApproveDate = businessDate.getReceiveApproveDate();
        Date confirmReceiptApproveDate = businessDate.getConfirmReceiptApproveDate();
        if (Objects.nonNull(receiveBillApproveDate)) {
            compareDate.add(receiveBillApproveDate);
        }
        if (Objects.nonNull(receiveApproveDate)) {
            compareDate.add(receiveApproveDate);
        }
        if (Objects.nonNull(confirmReceiptApproveDate)) {
            compareDate.add(confirmReceiptApproveDate);
        }
        return compareDate;
    }

    private Long getLeaderId(Long matchUserId, Long enterpriseId) {
        Long deptLeaderId = 0L;
        try {
            DeptSearchVo searchVo = new DeptSearchVo();
            searchVo.setUserId(matchUserId);
            searchVo.setDeptType(PmConstants.NODE_TYPE_DEPT);
            searchVo.setEnterpriseId(enterpriseId);
            deptLeaderId = authOpenFacade.findDeptLeader(searchVo);
        } catch (Exception e) {
            logger.error("getLeaderId error matchUserId:{} e :{}", matchUserId, e);
        }
        return deptLeaderId;
    }

    @Override
    public Page<CtrContractSettlement> findIndexPage(BudgetSettlementOphisSearchVo searchVo) {
        Map<String, Object> searchParams = searchVo.getSearchParams();
        if (searchParams.containsKey("EQS_sellMatchUserId")) {
            searchVo.getSearchParams().remove("EQS_sellMatchUserId");
        }
        Specification<CtrContractSettlement> spe = WebUtil.buildSpecification(searchParams);
        Specification<CtrContractSettlement> end;
        if (searchVo.getMatchUserId() != null) {
            Specification<CtrContractSettlement> sellMatchUserId = WebUtil.buildSpecification("EQL_sellMatchUserId", searchVo.getMatchUserId());
            Specification<CtrContractSettlement> buyMatchUserId = WebUtil.buildSpecification("EQL_buyMatchUserId", searchVo.getMatchUserId());
            Specification<CtrContractSettlement> sellHeadMatchUserId = WebUtil.buildSpecification("EQL_sellHeadUserId", searchVo.getMatchUserId());
            Specification<CtrContractSettlement> buyHeadMatchUserId = WebUtil.buildSpecification("EQL_buyHeadUserId", searchVo.getMatchUserId());
            Specification<CtrContractSettlement> allUser = sellMatchUserId.or(buyMatchUserId).or(sellHeadMatchUserId).or(buyHeadMatchUserId);
            end = Specification.where(spe).and(allUser);
        } else {
            end = spe;
        }
        PageRequest pageRequest = PageRequest.of(searchVo.getPage() - 1, searchVo.getRows());//分页
        return ctrContractSettlementDao.findAll(end, pageRequest);
    }

    @Override
    public CtrContractSettlement sumIndexPage(BudgetSettlementOphisSearchVo searchVo) {
        Map<String, Object> searchParams = searchVo.getSearchParams();
        if (searchParams.containsKey("EQS_sellMatchUserId")) {
            searchVo.getSearchParams().remove("EQS_sellMatchUserId");
        }
        Specification<CtrContractSettlement> spe = WebUtil.buildSpecification(searchParams);
        Specification<CtrContractSettlement> end;
        if (searchVo.getMatchUserId() != null) {
            Specification<CtrContractSettlement> sellMatchUserId = WebUtil.buildSpecification("EQL_sellMatchUserId", searchVo.getMatchUserId());
            Specification<CtrContractSettlement> buyMatchUserId = WebUtil.buildSpecification("EQL_buyMatchUserId", searchVo.getMatchUserId());
            Specification<CtrContractSettlement> sellHeadMatchUserId = WebUtil.buildSpecification("EQL_sellHeadUserId", searchVo.getMatchUserId());
            Specification<CtrContractSettlement> buyHeadMatchUserId = WebUtil.buildSpecification("EQL_buyHeadUserId", searchVo.getMatchUserId());
            Specification<CtrContractSettlement> allUser = sellMatchUserId.or(buyMatchUserId).or(sellHeadMatchUserId).or(buyHeadMatchUserId);
            end = Specification.where(spe).and(allUser);
        } else {
            end = spe;
        }
        return getCtrContractSettlement(end);
    }

    @Override
    public Page<CtrContractSettlement> findContractSettlementPage(Pageable page) {
        return ctrContractSettlementDao.findAll(page);
    }

    @Override
    public Integer selectAllCount() {
        return ctrContractSettlementDao.selectAllCount();
    }
}

