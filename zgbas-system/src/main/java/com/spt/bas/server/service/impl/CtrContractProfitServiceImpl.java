package com.spt.bas.server.service.impl;

import com.google.common.base.Stopwatch;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.*;
import com.spt.bas.client.vo.CtrContractProfitVo;
import com.spt.bas.server.dao.*;
import com.spt.bas.server.service.ICtrContractProfitService;
import com.spt.pm.dao.PmApproveDao;
import com.spt.pm.dao.PmProcessDao;
import com.spt.pm.entity.PmApprove;
import com.spt.pm.entity.PmProcess;
import com.spt.tools.data.annotation.ServiceTransactional;
import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.service.BaseService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * 风控合同利润统计表逻辑处理类
 *
 * @author MoonLight
 */
@Component
@Transactional(readOnly = true)
public class CtrContractProfitServiceImpl extends BaseService<CtrContractProfit> implements ICtrContractProfitService {
    private static final Logger log = LoggerFactory.getLogger(CtrContractProfitServiceImpl.class);
    @Autowired
    private CtrContractDao ctrContractDao;
    @Autowired
    private CtrProductDao ctrProductDao;
    @Autowired
    private ApplyDcsxDao applyDcsxDao;
    @Autowired
    private CtrContractProfitDao ctrContractProfitDao;
    @Autowired
    private PmApproveDao pmApproveDao;
    @Autowired
    private PmProcessDao pmProcessDao;
    @Autowired
    private ApplyMatchDao applyMatchDao;
    @Autowired
    private CtrContractApplyDao ctrContractApplyDao;
    @Autowired
    private ApplyMatchDetailDao applyMatchDetailDao;

    @Override
    public BaseDao<CtrContractProfit> getBaseDao() {
        return ctrContractProfitDao;
    }

    /**
     * 初始化保存合同利润统计数据
     *
     * @param approve
     * @param applyMatch
     */
    @Override
    @ServiceTransactional
    public void initContractProfit(PmApprove approve, ApplyMatch applyMatch) {
        CtrContractProfitVo profitVo = getContractProfitVo(approve.getId(), applyMatch);
        CtrProduct buyProduct = profitVo.getBuyProduct();
        CtrProduct sellProduct = profitVo.getSellProduct();
        if (Objects.isNull(buyProduct) || Objects.isNull(sellProduct)) {
            log.error("initContractProfit approveNo:{} error buyProduct or sellProduct is null!!!", approve.getApproveNo());
            return;
        }
        if (Boolean.TRUE.equals(profitVo.isChargeFlg())) {
            if (Objects.nonNull(profitVo.getSpecialBuyContract())) {
                CtrContractProfit buyMidProfit = initSpecialBuyMidProfit(approve, applyMatch, profitVo);
                buyMidProfit.setBusinessType(applyMatch.getBusinessType());
                ctrContractProfitDao.save(buyMidProfit);

                CtrContractProfit specialBUyMidProfit = initSpecialBuyMidProfit2(approve, applyMatch, profitVo);
                specialBUyMidProfit.setBusinessType(applyMatch.getBusinessType());
                ctrContractProfitDao.save(specialBUyMidProfit);
            } else {
                CtrContractProfit buyMidProfit = initBuyMidProfit(approve, applyMatch, profitVo);
                buyMidProfit.setBusinessType(applyMatch.getBusinessType());
                ctrContractProfitDao.save(buyMidProfit);
            }

            CtrContractProfit sellMidProfit = initSellMidProfit(approve, applyMatch, profitVo);
            sellMidProfit.setBusinessType(applyMatch.getBusinessType());
            ctrContractProfitDao.save(sellMidProfit);
        } else {
            CtrContractProfit profit = initOurProfit(approve, applyMatch, profitVo);
            profit.setBusinessType(applyMatch.getBusinessType());
            ctrContractProfitDao.save(profit);
        }
    }

    /**
     * 历史数据处理入库
     */
    @Override
    @ServiceTransactional
    public void initHistoryProfit() throws InterruptedException, ExecutionException {
        String[] profitProcess = {BasConstants.PROCESS_APPLY_MATCH, BasConstants.PROCESS_APPLY_MATCH_IOUS, BasConstants.PROCESS_APPLY_CHARGE_SALES};
        List<PmProcess> processList = pmProcessDao.findByProcessCodeInAndEnterpriseId(profitProcess, BasConstants.ZG_ENTERPRISE_ID);
        List<Long> profitProcessIdList = processList.stream().map(PmProcess::getId).collect(Collectors.toList());

        List<PmApprove> profitApproveList = pmApproveDao.findByProcessIdAndEffective(profitProcessIdList);
        List<Long> profitApproveIdList = profitApproveList.stream().map(PmApprove::getId).collect(Collectors.toList());
        List<ApplyMatch> applyMatchList = applyMatchDao.findByApproveIdList(profitApproveIdList);
        Map<Long, ApplyMatch> matchMap = applyMatchList.stream().collect(Collectors.toMap(ApplyMatch::getApproveId, m -> m, (a, b) -> b));

        ExecutorService executorService = Executors.newCachedThreadPool();
        ExecutorCompletionService<String> execu = new ExecutorCompletionService<>(executorService);
        int taskSize = profitApproveList.size();
        float bathSize = 25F;
        int bath = (int) Math.ceil((taskSize / bathSize));
        for (int i = 0; i < bath; i++) {
            int start = (int) (bathSize * i);
            int end = (int) (start + bathSize);
            end = Math.min(end, taskSize);
            List<PmApprove> syncList = profitApproveList.subList(start, end);
            execu.submit(() -> {
                syncList.forEach(approve -> initContractProfit(approve, matchMap.get(approve.getId())));
                return "initHistoryProfit OK";
            });
        }
        for (int i = 0; i < bath; i++) {
            Future<String> future = execu.take();
            log.info("result:{},{}", i, future.get());
        }
        executorService.shutdown();
    }

    @Override
    public List<CtrContractProfit> findByApproveIdAndProfitTypeAndLevel(Long approId, String profitType, Long level) {
        return ctrContractProfitDao.findByApproveIdAndProfitTypeAndLevel(approId, profitType, level);
    }

    @Override
    public List<CtrContractProfit> findByAndApproveId(Long approId) {
        return ctrContractProfitDao.findByAndApproveId(approId);
    }

    @Override
    public CtrContractProfit findBySellContractNo(String contractNo) {
        return ctrContractProfitDao.findBySellContractNo(contractNo);
    }

    @Override
    public CtrContractProfit findByBuyContractNo(String contractNo) {
        return ctrContractProfitDao.findByBuyContractNo(contractNo);
    }

    /**
     * 有approveNo即更新指定对应合同数据
     * 无approveNo参数就查询最近48小时存在变更的合同进行更新
     * 刷新利润统计数据
     */
    @Override
    @ServiceTransactional
    public void refreshProfitData(String approveNo) throws Exception {
        List<CtrContract> changeContractList;
        List<ApplyCtrDCSX> changeDCSXList;
        log.info("refreshProfitData approveNo:{}", approveNo);
        if (StringUtils.isNotBlank(approveNo)) {
            PmApprove pmApprove = pmApproveDao.findByApproveNo(approveNo);
            changeContractList = ctrContractDao.findAllContractByApproveId(Objects.nonNull(pmApprove) ? pmApprove.getId() : 0L);
            changeDCSXList = applyDcsxDao.findByApproveId(Objects.nonNull(pmApprove) ? pmApprove.getId() : 0L);
        } else {
            changeContractList = ctrContractDao.findChangeContractList();
            changeDCSXList = applyDcsxDao.findChangeDCSXList();
        }
        log.info("refreshProfitData 上下游合同更新条数:{},中游合同更新条数:{}", changeContractList.size(), changeDCSXList.size());

        this.executorUpdateContract(changeContractList);

        this.executorDcsxContract(changeDCSXList);
    }

    /**
     * 刷新多个审批编号
     * 有approveNo即更新指定对应合同数据
     * 无approveNo参数就查询最近48小时存在变更的合同进行更新
     * 刷新利润统计数据
     */
    @Override
    public void refreshProfitData(List<String> approveList) throws Exception {
        List<CtrContract> changeContractList = new ArrayList<>();
        List<ApplyCtrDCSX> changeDCSXList = new ArrayList<>();
        log.info("refreshProfitData approveNo:{}", approveList);
        if (CollectionUtils.isNotEmpty(approveList)) {
            approveList.forEach(it -> {
                PmApprove pmApprove = pmApproveDao.findByApproveNo(it);
                List<CtrContract> ctrContractList = ctrContractDao.findAllContractByApproveId(Objects.nonNull(pmApprove) ? pmApprove.getId() : 0L);
                List<ApplyCtrDCSX> applyCtrDCSXList = applyDcsxDao.findByApproveId(Objects.nonNull(pmApprove) ? pmApprove.getId() : 0L);
                changeContractList.addAll(ctrContractList);
                changeDCSXList.addAll(applyCtrDCSXList);
            });
            log.info("refreshProfitData 上下游合同更新条数:{},中游合同更新条数:{}", changeContractList.size(), changeDCSXList.size());
            this.executorUpdateContract(changeContractList);
            this.executorDcsxContract(changeDCSXList);
        }
    }

    private void executorUpdateContract(List<CtrContract> changeContractList) throws Exception {
        Stopwatch started = Stopwatch.createStarted();
        int numThreads = Runtime.getRuntime().availableProcessors();
        ExecutorService executorService = Executors.newFixedThreadPool(numThreads / 2);
        ExecutorCompletionService<String> execu = new ExecutorCompletionService<>(executorService);
        int taskSize = changeContractList.size();
        float bathSize = 30F;
        int bath = (int) Math.ceil((taskSize / bathSize));
        for (int i = 0; i < bath; i++) {
            int start = (int) (bathSize * i);
            int end = (int) (start + bathSize);
            end = Math.min(end, taskSize);
            List<CtrContract> syncList = changeContractList.subList(start, end);
            execu.submit(() -> {
                updateContractProfit(syncList);
                return "executorUpdateContract OK";
            });
        }
        for (int i = 0; i < bath; i++) {
            Future<String> future = execu.take();
            log.info("result:{},{}", i, future.get());
        }
        executorService.shutdown();
        log.info("总耗时:{}", started.elapsed(TimeUnit.SECONDS));
    }

    private void executorDcsxContract(List<ApplyCtrDCSX> changeDCSXList) throws Exception {
        Stopwatch started = Stopwatch.createStarted();
        int numThreads = Runtime.getRuntime().availableProcessors();
        ExecutorService executorService = Executors.newFixedThreadPool(numThreads / 2);
        ExecutorCompletionService<String> execu = new ExecutorCompletionService<>(executorService);
        int taskSize = changeDCSXList.size();
        float bathSize = 30F;
        int bath = (int) Math.ceil((taskSize / bathSize));
        for (int i = 0; i < bath; i++) {
            int start = (int) (bathSize * i);
            int end = (int) (start + bathSize);
            end = Math.min(end, taskSize);
            List<ApplyCtrDCSX> syncList = changeDCSXList.subList(start, end);
            execu.submit(() -> {
                updateDcsxProfit(syncList);
                return "executorUpdateDcsx OK";
            });
        }
        for (int i = 0; i < bath; i++) {
            Future<String> future = execu.take();
            log.info("result:{},{}", i, future.get());
        }
        executorService.shutdown();
        log.info("总耗时:{}", started.elapsed(TimeUnit.SECONDS));
    }


    private void updateContractProfit(List<CtrContract> changeContractList) {
        if (CollectionUtils.isNotEmpty(changeContractList)) {
            List<CtrContract> buyContractList = changeContractList.stream().
                    filter(c -> (StringUtils.equals(BasConstants.CONTRACT_TYPE_B, c.getContractType()) || Boolean.TRUE.equals(c.getSpecialChainFlag())))
                    .collect(Collectors.toList());
            List<CtrContract> sellContractList = changeContractList.stream().
                    filter(c -> (StringUtils.equals(BasConstants.CONTRACT_TYPE_S, c.getContractType()) || Boolean.TRUE.equals(c.getSpecialChainFlag())))
                    .collect(Collectors.toList());

            refreshBuyContract(buyContractList);

            refreshSellContract(sellContractList);
        }
    }

    private void updateDcsxProfit(List<ApplyCtrDCSX> changeDCSXList) {
        if (CollectionUtils.isNotEmpty(changeDCSXList)) {
            refreshBuyDcsx(changeDCSXList);

            refreshSellDcsx(changeDCSXList);
        }
    }

    /**
     * 1.1 初始化保存: 供应商-我方-客户 [适用于代采及普通赊销业务]
     *
     * @param approve
     * @param applyMatch
     * @param profitVo
     * @return
     */
    private CtrContractProfit initOurProfit(PmApprove approve, ApplyMatch applyMatch, CtrContractProfitVo profitVo) {
        // 初始化利润统计基础数据
        CtrContractProfit profit = initBasicProfit(approve, applyMatch, profitVo.getSellProduct());
        // 上游合同参数合并利润统计数据
        setBuyContractAsUpProfit(profit, profitVo.getBuyContract(), profitVo.getBuyProduct(), profitVo.getBuyContractApply());
        // 下游合同参数合并利润统计数据
        setSellContractAsDownProfit(profit, profitVo.getSellContract(), profitVo.getSellProduct(), profitVo.getSellContractApply());
        // 我方抬头
        profit.setOurCompanyName(profitVo.getSellContract().getOurCompanyName());
        return profit;
    }

    /**
     * 1.2 初始化保存: 供应商-代采方-我方[适用于代采赊销业务]
     *
     * @param approve
     * @param profitVo
     * @return
     */
    private CtrContractProfit initBuyMidProfit(PmApprove approve, ApplyMatch applyMatch, CtrContractProfitVo profitVo) {

        // 初始化利润统计基础数据
        CtrContractProfit profit = initBasicProfit(approve, applyMatch, profitVo.getSellProduct());
        // 上游合同参数合并利润统计数据
        setBuyContractAsUpProfit(profit, profitVo.getBuyContract(), profitVo.getBuyProduct(), profitVo.getBuyContractApply());
        // 中游合同参数合并利润统计数据
        setDcsxAsDownProfit(profit, profitVo.getApplyCtrDCSX(), profitVo.getSellContract(), profitVo.getSellContractApply());
        // 我方抬头
        profit.setOurCompanyName(profitVo.getBuyContract().getOurCompanyName());
        profit.setLevel(1L);
        return profit;
    }

    /**
     * 1.3 初始化保存: 供应商-中游代采方-我方[适用于代采赊销业务]
     *
     * @param approve
     * @param profitVo
     * @return
     */
    private CtrContractProfit initSpecialBuyMidProfit(PmApprove approve, ApplyMatch applyMatch, CtrContractProfitVo profitVo) {

        // 初始化利润统计基础数据
        CtrContractProfit profit = initBasicProfit(approve, applyMatch, profitVo.getSellProduct());
        // 上游合同参数合并利润统计数据
        setBuyContractAsUpProfit(profit, profitVo.getBuyContract(), profitVo.getBuyProduct(), profitVo.getBuyContractApply());
        // SPT1合同
        setSellContractAsDownProfit(profit, profitVo.getSpecialBuyContract(), profitVo.getSpecialBuyProduct(), profitVo.getSpecialBuyContractApply());
        // 我方抬头
        profit.setOurCompanyName(profitVo.getBuyContract().getOurCompanyName());
        profit.setSellCompanyName(profitVo.getSpecialBuyContract().getOurCompanyName());
        profit.setProfitType(BasConstants.PROFIT_TYPE_5);
        profit.setLevel(1L);
        return profit;
    }

    /**
     * 1.4 初始化保存: 供应商-中游代采方-我方[适用于代采赊销业务]
     *
     * @param approve
     * @param profitVo
     * @return
     */
    private CtrContractProfit initSpecialBuyMidProfit2(PmApprove approve, ApplyMatch applyMatch, CtrContractProfitVo profitVo) {

        // 初始化利润统计基础数据
        CtrContractProfit profit = initBasicProfit(approve, applyMatch, profitVo.getSellProduct());
        // 上游合同参数合并利润统计数据
        setBuyContractAsUpProfit(profit, profitVo.getSpecialBuyContract(), profitVo.getSpecialBuyProduct(), profitVo.getSpecialBuyContractApply());
        // 中游合同参数合并利润统计数据
        setDcsxAsDownProfit(profit, profitVo.getApplyCtrDCSX(), profitVo.getSellContract(), profitVo.getSellContractApply());
        // 我方抬头
        profit.setOurCompanyName(profitVo.getSpecialBuyContract().getOurCompanyName());
        profit.setLevel(2L);
        return profit;
    }

    /**
     * 1.3 初始化保存: 代采方-我方-客户[适用于代采赊销业务]
     *
     * @param approve
     * @param profitVo
     * @return
     */
    private CtrContractProfit initSellMidProfit(PmApprove approve, ApplyMatch applyMatch, CtrContractProfitVo profitVo) {
        // 初始化利润统计基础数据
        CtrContractProfit profit = initBasicProfit(approve, applyMatch, profitVo.getSellProduct());
        // 中游合同参数合并利润统计数据
        setDcsxAsUpProfit(profit, profitVo.getApplyCtrDCSX(), profitVo.getSellContract(), profitVo.getBuyContract(), profitVo.getSellContractApply());
        // 下游合同参数合并利润统计数据
        setSellContractAsDownProfit(profit, profitVo.getSellContract(), profitVo.getSellProduct(), profitVo.getSellContractApply());
        // 我方抬头
        if (profitVo.getApplyCtrDCSX() != null) {
            profit.setOurCompanyName(profitVo.getApplyCtrDCSX().getOurCompanyName());
        }
        profit.setProfitType(BasConstants.PROFIT_TYPE_5);
        return profit;
    }

    /**
     * 中游合同参数合并至利润统计数据-采购
     *
     * @param profit
     * @param applyCtrDcsx
     */
    private void setDcsxAsUpProfit(CtrContractProfit profit, ApplyCtrDCSX applyCtrDcsx, CtrContract sellContract, CtrContract buyContract, CtrContractApply sellContractApply) {
        if (applyCtrDcsx != null) {
            //采购附件id
            profit.setBuyContentFileId(applyCtrDcsx.getBuyContentFileId());
            //补充条款
            profit.setBuyExtraTerm(applyCtrDcsx.getExtraTerm());
            //不含税单价
            profit.setDealAmountNoTax(applyCtrDcsx.getDealAmountNoTax());
            //运输费
            profit.setTransportAmount(applyCtrDcsx.getTransportAmount());
            //仓储费
            profit.setWarehouseAmount(applyCtrDcsx.getWarehouseAmount());
            //交货日期
            profit.setDeliveryDate(applyCtrDcsx.getDeliveryDateFrom());
            //交货方式
            profit.setDeliveryMode(applyCtrDcsx.getDeliveryMode());
            //付全款日期
            profit.setPayFullDate(applyCtrDcsx.getPayFullTime());
            //定金
            profit.setBuyBondAmount(applyCtrDcsx.getBondAmount());
            //定金比例
            profit.setBuyBondRate(applyCtrDcsx.getBondRate());
            //结算方式
            profit.setSettlementType(applyCtrDcsx.getSettlementType());
            //支付方式
            profit.setPayMode(applyCtrDcsx.getPayType());
            // 采购合同编号
            profit.setBuyContractNo(applyCtrDcsx.getContractNo());
            // 采购-合同状态
            profit.setBuyContractStatus(applyCtrDcsx.getStatus());
            // 供应商
            profit.setBuyCompanyName(applyCtrDcsx.getCompanyName());
            // 采购交货方式
            profit.setBuyDeliveryType(applyCtrDcsx.getDeliveryType());
            // 采购交货地址
            profit.setBuyDeliveryAddr(applyCtrDcsx.getDeliveryAddr());
            // 采购单价
            profit.setBuyPrice(applyCtrDcsx.getDealPrice());
            // 采购合同金额
            profit.setBuyTotalAmount(applyCtrDcsx.getTotalAmount());
            // 应付金额
            profit.setBalancePayable(applyCtrDcsx.getTotalAmount().subtract(applyCtrDcsx.getDealedAmount()));
            // 付款金额
            profit.setPayAmount(applyCtrDcsx.getDealedAmount());
            // 付全款日期
            profit.setPayFullDate(applyCtrDcsx.getPayFullTime());
            // 采购定金比例
            profit.setBuyBondRate(applyCtrDcsx.getBondRate());
            // 采购定金金额
            profit.setBuyBondAmount(applyCtrDcsx.getBondAmount());
        }
        //详细地址
        profit.setBuyContactAddr(sellContract.getContactAddr());
        //交货地点
        profit.setBuyDeliveryAddr(sellContract.getDeliveryAddr());
        //备注
        profit.setBuyRemark(sellContract.getRemark());
        profit.setAttachDeliveryTime(sellContract.getAttachDeliveryTime());
        // 入库数量
        profit.setDeliveryInNumber(sellContract.getWarehouseNumber());
        // 入库日期
        profit.setDeliveryInDate(sellContractApply.getRealWarehoseDate());

    }

    /**
     * 中游合同参数合并至利润统计数据-销售
     *
     * @param profit
     * @param applyCtrDcsx
     */
    private void setDcsxAsDownProfit(CtrContractProfit profit, ApplyCtrDCSX applyCtrDcsx, CtrContract sellContract, CtrContractApply sellContractApply) {

        if (applyCtrDcsx != null) {
            //服务合同id
            String serviceContractId = applyCtrDcsx.getServiceContractId() == null ? null : applyCtrDcsx.getServiceContractId().toString();
            profit.setServiceContractId(serviceContractId);
            //销售合同id
            profit.setSellContentFileId(applyCtrDcsx.getSellContentFileId());
            //加价
            profit.setPremium(applyCtrDcsx.getPremium());
            //资金服务费
            profit.setServiceAmount(applyCtrDcsx.getServiceAmount());
            //结算方式
            profit.setSellSettlementType(sellContract.getSettlementType());
            //实际付款日期
            profit.setRealPayDate(applyCtrDcsx.getLastPayDate());
//        //付全款日期
//        profit.setPayFullDate(applyCtrDcsx.getPayFullTime());
            //备注
            profit.setSellRemark(applyCtrDcsx.getRemark());
            //补充条款
            profit.setSellExtraTerm(applyCtrDcsx.getExtraTerm());
            //运输费
            profit.setSellTransportAmount(applyCtrDcsx.getTransportAmount());
            //仓储费
            profit.setSellWarehouseAmount(applyCtrDcsx.getWarehouseAmount());
            //联系地址
            profit.setSellContactAddr(applyCtrDcsx.getContactAddr());
            //交货日期
            profit.setSellDeliveryDate(applyCtrDcsx.getDeliveryDateFrom());
            //交货方式
            profit.setSellDeliveryType(applyCtrDcsx.getDeliveryMode());
            // 合同签订日期
            profit.setContractTime(applyCtrDcsx.getContractTime());
            // 业务员ID
            profit.setMatchUserId(applyCtrDcsx.getMatchUserId());
            // 业务员
            profit.setMatchUserName(applyCtrDcsx.getMatchUserName());
            // 销售合同编号
            profit.setSellContractNo(applyCtrDcsx.getContractNo());
            // 销售-合同状态
            profit.setSellContractStatus(applyCtrDcsx.getStatus());
            // 客户
            profit.setSellCompanyName(applyCtrDcsx.getOurCompanyName());
            // 销售交货方式
            profit.setSellDeliveryType(applyCtrDcsx.getDeliveryType());
            // 销售交货地址
            profit.setSellDeliveryAddr(applyCtrDcsx.getDeliveryAddr());
            // 销售合同金额
            profit.setSellTotalAmount(applyCtrDcsx.getTotalAmount());
            // 销售收款金额
            profit.setReceiveAmount(applyCtrDcsx.getDealedAmount());
            // 销售单价
            profit.setSellPrice(applyCtrDcsx.getDealPrice());
            // 约定付全款日期
            profit.setAppointPayFullTime(applyCtrDcsx.getPayFullTime());
            // 销售付全款日期
            profit.setSellPayFullDate(applyCtrDcsx.getPayFullTime());
            // 回款周期
            profit.setCreditDays(applyCtrDcsx.getCreditCycle());
            // 应收金额
            profit.setBalanceReceivable(applyCtrDcsx.getTotalAmount().subtract(applyCtrDcsx.getDealedAmount()));
            // 销售定金比例
            profit.setSellBondRate(applyCtrDcsx.getBondRate());
            // 销售定金金额
            profit.setSellBondAmount(applyCtrDcsx.getBondAmount());
            // 开票日期
            profit.setInvoiceBillDate(applyCtrDcsx.getInvoiceDate());
            // 开票金额
            profit.setInvoiceBillAmount(applyCtrDcsx.getBilledAmount());
        }
        //约定付全款日期
        profit.setAppointPayFullTime(sellContract.getAppointPayFullTime());
        // 合同类型
        profit.setProfitType(BasConstants.PROFIT_TYPE_5);
        // 出库数量
        profit.setDeliveryOutNumber(sellContract.getWarehouseNumber());
        // 出库日期
        profit.setDeliveryOutDate(sellContractApply.getRealWarehoseDate());
        //附加销售时间
        profit.setAttachDeliveryTime(sellContract.getAttachDeliveryTime());
    }

    /**
     * 上游合同参数合并至利润统计数据
     *
     * @param profit
     * @param buyContract
     * @param buyProduct
     */
    private void setBuyContractAsUpProfit(CtrContractProfit profit, CtrContract buyContract, CtrProduct buyProduct, CtrContractApply buyContractApply) {
        //不含税单价
        profit.setDealAmountNoTax(buyContract.getDealAmountNoTax());
        //采购附件id
        profit.setBuyContentFileId(buyContract.getBuyContentFileId());
        //附加銷售時間
        profit.setAttachDeliveryTime(buyContract.getAttachDeliveryTime());
        //备注
        profit.setBuyRemark(buyContract.getRemark());
        //补充条款
        profit.setBuyExtraTerm(buyContract.getExtraTerm());
        //不含税单价
        profit.setDealAmountNoTax(buyContract.getDealAmountNoTax());
        //运输费
        profit.setTransportAmount(buyContract.getTransportAmount());
        //仓储费
        profit.setWarehouseAmount(buyContract.getWarehouseAmount());
        //详细地址
        profit.setBuyContactAddr(buyContract.getContactAddr());
        //交货地点
        profit.setBuyDeliveryAddr(buyContract.getDeliveryAddr());
        //交货日期
        profit.setDeliveryDate(buyContract.getDeliveryDateFrom());
        //交货方式
        profit.setDeliveryMode(buyContract.getDeliveryMode());
        //定金
        profit.setBuyBondAmount(buyContract.getBondAmount());
        //定金比例
        profit.setBuyBondRate(buyContract.getBondRate());
        //结算方式
        profit.setSettlementType(buyContract.getSettlementType());
        //支付方式
        profit.setPayMode(buyContract.getPayType());
        // 采购合同编号
        profit.setBuyContractNo(buyContract.getContractNo());
        // 采购-合同状态
        profit.setBuyContractStatus(buyContract.getContractStatus());
        // 供应商
        profit.setBuyCompanyName(buyContract.getCompanyName());
        // 采购交货方式
        profit.setBuyDeliveryType(buyContract.getDeliveryType());
        // 采购交货地址
        profit.setBuyDeliveryAddr(buyContract.getDeliveryAddr());
        // 采购单价
        profit.setBuyPrice(buyProduct.getDealPrice());
        // 采购合同金额
        profit.setBuyTotalAmount(buyContract.getTotalAmount());
        // 应付金额
        profit.setBalancePayable(buyContract.getTotalAmount().subtract(buyContract.getDealedAmount()).add(buyContract.getLossAmount()));
        // 付款金额
        profit.setPayAmount(buyContract.getDealedAmount());
        // 付全款日期
        profit.setPayFullDate(buyContract.getPayFullTime());
        // 采购定金比例
        profit.setBuyBondRate(buyContract.getBondRate());
        // 采购定金金额
        profit.setBuyBondAmount(buyContract.getBondAmount());
        // 收票金额
        profit.setReceiptBillAmount(buyContract.getBilledAmount());
        // 收票日期
        profit.setReceiptBillDate(buyContractApply.getRealBillDate());
        // 入库数量
        profit.setDeliveryInNumber(buyContract.getWarehouseNumber());
        // 入库日期
        profit.setDeliveryInDate(buyContractApply.getRealWarehoseDate());
    }

    /**
     * 下游合同参数合并至利润统计数据
     *
     * @param profit
     * @param sellContract
     * @param sellProduct
     */
    private void setSellContractAsDownProfit(CtrContractProfit profit, CtrContract sellContract, CtrProduct sellProduct, CtrContractApply sellContractApply) {
        //服务合同id
        String serviceContractId = sellContract.getServiceContractId() == null ? null : sellContract.getServiceContractId().toString();
        profit.setServiceContractId(serviceContractId);
        ///附加銷售時間
        profit.setAttachDeliveryTime(sellContract.getAttachDeliveryTime());
        //销售合同id
        profit.setSellContentFileId(sellContract.getSellContentFileId());
        //加价
        profit.setPremium(sellContract.getPremium());
        //资金服务费
        profit.setServiceAmount(sellContract.getServiceAmount());
        //结算方式
        profit.setSellSettlementType(sellContract.getSettlementType());
        //实际付款日期
        profit.setRealPayDate(sellContract.getRealPayFullTime());
        //约定付全款日期
        profit.setAppointPayFullTime(sellContract.getAppointPayFullTime());
        //付全款日期
        profit.setSellPayFullDate(sellContract.getPayFullTime());
        //备注
        profit.setSellRemark(sellContract.getRemark());
        //补充条款
        profit.setSellExtraTerm(sellContract.getExtraTerm());
        //运输费
        profit.setSellTransportAmount(sellContract.getTransportAmount());
        //仓储费
        profit.setSellWarehouseAmount(sellContract.getWarehouseAmount());
        //联系地址
        profit.setSellContactAddr(sellContract.getContactAddr());
        //交货日期
        profit.setSellDeliveryDate(sellContract.getDeliveryDateFrom());
        //交货方式
        profit.setSellDeliveryType(sellContract.getDeliveryMode());

        // 我方抬头
        profit.setOurCompanyName(sellContract.getOurCompanyName());
        // 合同签订日期
        profit.setContractTime(sellContract.getContractTime());
        // 业务员ID
        profit.setMatchUserId(sellContract.getMatchUserId());
        // 业务员
        profit.setMatchUserName(sellContract.getMatchUserName());
        // 销售合同编号
        profit.setSellContractNo(sellContract.getContractNo());
        // 销售-合同状态
        profit.setSellContractStatus(sellContract.getContractStatus());
        // 客户
        profit.setSellCompanyName(sellContract.getCompanyName());
        // 销售交货方式
        profit.setSellDeliveryType(sellContract.getDeliveryType());
        // 销售交货地址
        profit.setSellDeliveryAddr(sellContract.getDeliveryAddr());
        // 销售合同金额
        profit.setSellTotalAmount(sellContract.getTotalAmount());
        // 已收金额
        profit.setReceiveAmount(sellContract.getDealedAmount());
        // 销售单价
        profit.setSellPrice(sellProduct.getDealPrice());
        // 约定付全款日期
        profit.setAppointPayFullTime(sellContract.getAppointPayFullTime());
        // 销售付全款日期
        profit.setSellPayFullDate(sellContract.getPayFullTime());
        // 回款周期
        profit.setCreditDays(sellContract.getCreditCycle());
        // 应收金额
        profit.setBalanceReceivable(sellContract.getTotalAmount().subtract(sellContract.getDealedAmount()).add(sellContract.getLossAmount()));
        // 销售定金比例
        profit.setSellBondRate(sellContract.getBondRate());
        // 销售定金金额
        profit.setSellBondAmount(sellContract.getBondAmount());
        // 开票金额
        profit.setInvoiceBillAmount(sellContract.getBilledAmount());
        // 开票日期
        profit.setInvoiceBillDate(sellContractApply.getRealBillDate());
        // 出库数量
        profit.setDeliveryOutNumber(sellContract.getWarehouseNumber());
        // 出库日期
        profit.setDeliveryOutDate(sellContractApply.getRealWarehoseDate());
        // 确认收货日期
        profit.setConfirmDate(sellContract.getConfirmDate());
        // 违约天数
        profit.setBreachDays(sellContract.getBreachDays());
        // 逾期罚息金额
        profit.setBreachAmount(sellContract.getBreachAmount());
        // 已收逾期罚息金额
        profit.setReceiveBreachAmount(sellContract.getReceiveBreachAmount());
        // 签约标识
        profit.setSealFlg(sellContract.getSealFlg());
        // 开票标识
        profit.setBillFlg(sellContract.getBillFlg());
        // 合同类型
        profit.setProfitType(Boolean.TRUE.equals(sellContract.getMatchCreditFlg()) ? BasConstants.PROFIT_TYPE_1 : BasConstants.PROFIT_TYPE_2);
    }

    /**
     * 初始化利润基础数据
     *
     * @param approve
     * @param ctrProduct
     * @return
     */
    private CtrContractProfit initBasicProfit(PmApprove approve, ApplyMatch applyMatch, CtrProduct ctrProduct) {
        CtrContractProfit profit = new CtrContractProfit();
        profit.setApproveNo(approve.getApproveNo());
        profit.setApproveId(approve.getId());
        String productName = StringUtils.isNotBlank(ctrProduct.getProductName()) ? ctrProduct.getProductName() : "";
        String brandNumber = StringUtils.isNotBlank(ctrProduct.getBrandNumber()) ? ctrProduct.getBrandNumber() : "";
        String factoryName = StringUtils.isNotBlank(ctrProduct.getFactoryName()) ? ctrProduct.getFactoryName() : "";
        profit.setProductName(productName + "/" + brandNumber + "/" + factoryName);
        profit.setBrandNumber(ctrProduct.getBrandNumber());
        profit.setFactoryName(ctrProduct.getFactoryName());
        profit.setWrapSpecs(applyMatch.getWrapSpecs());
        profit.setQualityStandard(applyMatch.getQualityStandard());
        profit.setTotalNumber(ctrProduct.getDealNumber());
        List<ApplyMatchDetail> applyMatchDetails = applyMatchDetailDao.findByApplyMatchId(applyMatch.getId());
        ApplyMatchDetail buyMatchDetail = null;
        for (ApplyMatchDetail applyMatchDetail : applyMatchDetails) {
            if (BasConstants.CONTRACT_TYPE_B.equals(applyMatchDetail.getContractType())) {
                buyMatchDetail = applyMatchDetail;
            }
        }
        profit.setBuySource(buyMatchDetail.getBuySource());
        return profit;
    }


    /**
     * 查询获取合同利润统计参数
     *
     * @param approveId
     * @return
     */
    private CtrContractProfitVo getContractProfitVo(Long approveId, ApplyMatch applyMatch) {
        CtrContractProfitVo profitVo = new CtrContractProfitVo();
        List<CtrContract> contractList = ctrContractDao.findByApproveId(approveId);
        if (CollectionUtils.isEmpty(contractList)) {
            return profitVo;
        }
        String buyOurCompanyName = applyMatch.getBuyOurCompanyName();
        String sellOurCompanyName = applyMatch.getSellOurCompanyName();
        boolean chargeFlg = false;
        if (StringUtils.isNotBlank(buyOurCompanyName) || StringUtils.isNotBlank(sellOurCompanyName)) {
            chargeFlg = true;
            List<ApplyCtrDCSX> dcsxList = applyDcsxDao.findByApproveId(approveId);
            profitVo.setDcsxList(dcsxList);
            if (CollectionUtils.isNotEmpty(dcsxList)) {
                ApplyCtrDCSX applyCtrDcsx = dcsxList.stream()
                        .filter(d -> StringUtils.equals(BasConstants.BUSINESS_TYPE_ZY_BB, d.getBusinessType()) || StringUtils.equals(BasConstants.BUSINESS_TYPE_ZY_TP, d.getBusinessType()))
                        .findFirst().orElse(null);
                profitVo.setApplyCtrDCSX(applyCtrDcsx);
            } else {
                chargeFlg = false;
            }
        }

        CtrContract buyContract = contractList.stream().
                filter(c -> StringUtils.equals(BasConstants.CONTRACT_TYPE_B, c.getContractType())).
                findFirst().orElse(null);
        CtrContract sellContract = contractList.stream().
                filter(c -> StringUtils.equals(BasConstants.CONTRACT_TYPE_S, c.getContractType())).
                findFirst().orElse(null);
        CtrContract specialBuyContract = ctrContractDao.findSpecialChainByApproveId(approveId);
        if (Objects.nonNull(buyContract)) {
            CtrProduct buyProduct = ctrProductDao.findOneByCtrContractId(buyContract.getId());
            profitVo.setBuyProduct(buyProduct);
            CtrContractApply buyContractApply = ctrContractApplyDao.findByCtrContractId(buyContract.getId());
            profitVo.setBuyContractApply(Objects.nonNull(buyContractApply) ? buyContractApply : new CtrContractApply());
        }

        if (Objects.nonNull(sellContract)) {
            CtrProduct sellProduct = ctrProductDao.findOneByCtrContractId(sellContract.getId());
            profitVo.setSellProduct(sellProduct);
            CtrContractApply sellContractApply = ctrContractApplyDao.findByCtrContractId(sellContract.getId());
            profitVo.setSellContractApply(Objects.nonNull(sellContractApply) ? sellContractApply : new CtrContractApply());
        }

        if (Objects.nonNull(specialBuyContract)) {
            CtrProduct specialBuyProduct = ctrProductDao.findOneByCtrContractId(specialBuyContract.getId());
            profitVo.setSpecialBuyProduct(specialBuyProduct);
            CtrContractApply specialBuyContractApply = ctrContractApplyDao.findByCtrContractId(specialBuyContract.getId());
            profitVo.setSpecialBuyContractApply(Objects.nonNull(specialBuyContractApply) ? specialBuyContractApply : new CtrContractApply());
        }

        profitVo.setChargeFlg(chargeFlg);
        profitVo.setBuyContract(buyContract);
        profitVo.setSpecialBuyContract(specialBuyContract);
        profitVo.setSellContract(sellContract);
        return profitVo;
    }

    /**
     * 更新 buyContract 利润统计数据
     *
     * @param buyContractList
     */
    private void refreshBuyContract(List<CtrContract> buyContractList) {
        if (CollectionUtils.isEmpty(buyContractList)) {
            return;
        }
        Map<String, CtrContract> contractMap = buyContractList.stream().collect(Collectors.toMap(CtrContract::getContractNo, c -> c, (a, b) -> b));
        List<String> contractNoList = buyContractList.stream().map(CtrContract::getContractNo).collect(Collectors.toList());
        List<Long> contractIdList = buyContractList.stream().map(CtrContract::getId).collect(Collectors.toList());
        List<CtrContractProfit> profitList = ctrContractProfitDao.findByBuyContractNoIn(contractNoList);
        List<CtrContractApply> contractApplyList = ctrContractApplyDao.findByCtrContractIdIn(contractIdList);
        Map<Long, CtrContractApply> applyMap = contractApplyList.stream().collect(Collectors.toMap(CtrContractApply::getCtrContractId, c -> c, (a, b) -> b));
        if (CollectionUtils.isEmpty(profitList)) {
            return;
        }
        profitList.forEach(entity -> {
            CtrContract contract = contractMap.get(entity.getBuyContractNo());
            if (Objects.nonNull(contract)) {
                entity.setBuyContractStatus(contract.getContractStatus());
                entity.setBuyTotalAmount(contract.getTotalAmount());
                entity.setBuyCompanyName(contract.getCompanyName());
                entity.setPayAmount(contract.getDealedAmount());
                entity.setBalancePayable(contract.getTotalAmount().subtract(contract.getDealedAmount()).add(contract.getLossAmount()));
                entity.setBuyDeliveryType(contract.getDeliveryType());
                CtrContractApply contractApply = applyMap.get(contract.getId());
                Date receiptBillDate = Objects.nonNull(contractApply) ? contractApply.getRealBillDate() : null;
                entity.setReceiptBillDate(contract.getBilledAmount().compareTo(BigDecimal.ZERO) > 0 ? receiptBillDate : null);
                entity.setReceiptBillAmount(contract.getBilledAmount());

                Date deliveryInDate = Objects.nonNull(contractApply) ? contractApply.getRealWarehoseDate() : null;
                entity.setDeliveryInDate(contract.getWarehouseNumber().compareTo(BigDecimal.ZERO) > 0 ? deliveryInDate : null);
                entity.setDeliveryInNumber(contract.getWarehouseNumber());
            }
            List<CtrProduct> buyProductList = ctrProductDao.findByCtrContractId(contract.getId());
            CtrProduct ctrProduct = CollectionUtils.isNotEmpty(buyProductList) ? buyProductList.get(0) : null;
            if (Objects.nonNull(ctrProduct)) {
                entity.setBuyPrice(ctrProduct.getDealPrice());
                entity.setTotalNumber(ctrProduct.getDealNumber());
            }
        });
        ctrContractProfitDao.saveAll(profitList);
    }

    /**
     * 更新 sellContract 利润统计数据
     *
     * @param sellContractList
     */
    private void refreshSellContract(List<CtrContract> sellContractList) {
        if (CollectionUtils.isEmpty(sellContractList)) {
            return;
        }
        Map<String, CtrContract> contractMap = sellContractList.stream().collect(Collectors.toMap(CtrContract::getContractNo, c -> c, (a, b) -> b));
        List<String> contractNoList = sellContractList.stream().map(CtrContract::getContractNo).collect(Collectors.toList());
        List<Long> contractIdList = sellContractList.stream().map(CtrContract::getId).collect(Collectors.toList());
        List<CtrContractProfit> profitList = ctrContractProfitDao.findBySellContractNoIn(contractNoList);
        List<CtrContractApply> contractApplyList = ctrContractApplyDao.findByCtrContractIdIn(contractIdList);
        Map<Long, CtrContractApply> applyMap = contractApplyList.stream().collect(Collectors.toMap(CtrContractApply::getCtrContractId, c -> c, (a, b) -> b));
        if (CollectionUtils.isEmpty(profitList)) {
            return;
        }
        profitList.forEach(entity -> {
            CtrContract contract = contractMap.get(entity.getSellContractNo());
            if (Objects.nonNull(contract)) {
                entity.setProductName(contract.getProductsName());
                entity.setTotalNumber(contract.getTotalNumber());
                entity.setSellContractStatus(contract.getContractStatus());
                entity.setSellTotalAmount(contract.getTotalAmount());
                entity.setSellCompanyName(contract.getCompanyName());
                entity.setReceiveAmount(contract.getDealedAmount());
                entity.setBalanceReceivable(contract.getTotalAmount().subtract(contract.getDealedAmount()).add(contract.getLossAmount()));
                entity.setSellPayFullDate(contract.getPayFullTime());
                entity.setAppointPayFullTime(contract.getAppointPayFullTime());
                entity.setSellDeliveryType(contract.getDeliveryType());
                CtrContractApply contractApply = applyMap.get(contract.getId());
                Date invoiceBillDate = Objects.nonNull(contractApply) ? contractApply.getRealBillDate() : null;
                entity.setInvoiceBillDate(contract.getBilledAmount().compareTo(BigDecimal.ZERO) > 0 ? invoiceBillDate : null);
                entity.setInvoiceBillAmount(contract.getBilledAmount());

                Date deliveryOutDate = Objects.nonNull(contractApply) ? contractApply.getRealWarehoseDate() : null;
                entity.setDeliveryOutDate(contract.getWarehouseNumber().compareTo(BigDecimal.ZERO) > 0 ? deliveryOutDate : null);
                entity.setDeliveryOutNumber(contract.getWarehouseNumber());
                entity.setBreachDays(contract.getBreachDays());
                entity.setBreachAmount(contract.getBreachAmount());
                entity.setReceiveBreachAmount(contract.getReceiveBreachAmount());
                entity.setConfirmReceiveNumber(contract.getConfirmReceiveNumber());
                entity.setConfirmDate(contract.getConfirmDate());
                entity.setSealFlg(contract.getSealFlg());
                entity.setBillFlg(contract.getBillFlg());
            }
            List<CtrProduct> sellProductList = ctrProductDao.findByCtrContractId(contract.getId());
            CtrProduct ctrProduct = CollectionUtils.isNotEmpty(sellProductList) ? sellProductList.get(0) : null;
            if (Objects.nonNull(ctrProduct)) {
                entity.setSellPrice(ctrProduct.getDealPrice());
                entity.setTotalNumber(ctrProduct.getDealNumber());
            }
        });
        ctrContractProfitDao.saveAll(profitList);
    }

    private void refreshBuyDcsx(List<ApplyCtrDCSX> applyCtrDCSXList) {
        if (CollectionUtils.isEmpty(applyCtrDCSXList)) {
            return;
        }
        Map<String, ApplyCtrDCSX> contractMap = applyCtrDCSXList.stream().collect(Collectors.toMap(ApplyCtrDCSX::getContractNo, c -> c, (a, b) -> b));
        List<String> contractNoList = applyCtrDCSXList.stream().map(ApplyCtrDCSX::getContractNo).collect(Collectors.toList());
        List<CtrContractProfit> profitList = ctrContractProfitDao.findByBuyContractNoIn(contractNoList);
        if (CollectionUtils.isEmpty(profitList)) {
            return;
        }

        profitList.forEach(entity -> {
            ApplyCtrDCSX contract = contractMap.get(entity.getBuyContractNo());
            if (Objects.nonNull(contract)) {
                entity.setBuyContractStatus(contract.getStatus());
                entity.setBuyTotalAmount(contract.getTotalAmount());
                entity.setBuyPrice(contract.getDealPrice());
                entity.setPayAmount(contract.getDealedAmount());
                entity.setBalancePayable(contract.getTotalAmount().subtract(contract.getDealedAmount()));
                entity.setReceiptBillAmount(contract.getBilledAmount());
                entity.setPayFullDate(contract.getPayFullTime());
            }
            CtrContract sellContract = ctrContractDao.findByApproveIdAndContractType(contract.getApproveId(), BasConstants.CONTRACT_TYPE_S);
            if (Objects.nonNull(sellContract)) {
                CtrContractApply contractApply = ctrContractApplyDao.findByCtrContractId(sellContract.getId());
                entity.setDeliveryInNumber(sellContract.getWarehouseNumber());
                entity.setDeliveryInDate(contractApply.getRealWarehoseDate());
            }
        });
        ctrContractProfitDao.saveAll(profitList);
    }

    private void refreshSellDcsx(List<ApplyCtrDCSX> applyCtrDCSXList) {
        if (CollectionUtils.isEmpty(applyCtrDCSXList)) {
            return;
        }
        Map<String, ApplyCtrDCSX> contractMap = applyCtrDCSXList.stream().collect(Collectors.toMap(ApplyCtrDCSX::getContractNo, c -> c, (a, b) -> b));
        List<String> contractNoList = applyCtrDCSXList.stream().map(ApplyCtrDCSX::getContractNo).collect(Collectors.toList());
        List<CtrContractProfit> profitList = ctrContractProfitDao.findBySellContractNoIn(contractNoList);
        if (CollectionUtils.isEmpty(profitList)) {
            return;
        }

        profitList.forEach(entity -> {
            ApplyCtrDCSX contract = contractMap.get(entity.getSellContractNo());
            if (Objects.nonNull(contract)) {
                entity.setSellContractStatus(contract.getStatus());
                entity.setSellTotalAmount(contract.getTotalAmount());
                entity.setSellPrice(contract.getDealPrice());
                entity.setReceiveAmount(contract.getDealedAmount());
                entity.setBalanceReceivable(contract.getTotalAmount().subtract(contract.getDealedAmount()));
                entity.setInvoiceBillAmount(contract.getInvoiceBillAmount());
                entity.setDeliveryOutNumber(contract.getWarehouseNumber());
                entity.setSellPayFullDate(contract.getPayFullTime());
                entity.setAppointPayFullTime(contract.getPayFullTime());
                entity.setSealFlg(contract.getSealFlg());
                entity.setBillFlg(contract.getBillFlg());
            }
            CtrContract sellContract = ctrContractDao.findByApproveIdAndContractType(contract.getApproveId(), BasConstants.CONTRACT_TYPE_S);
            if (Objects.nonNull(sellContract)) {
                CtrContractApply contractApply = ctrContractApplyDao.findByCtrContractId(sellContract.getId());
                entity.setDeliveryOutNumber(sellContract.getWarehouseNumber());
                entity.setDeliveryOutDate(contractApply.getRealWarehoseDate());
            }
        });
        ctrContractProfitDao.saveAll(profitList);
    }
}
