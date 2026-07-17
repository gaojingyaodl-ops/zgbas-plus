package com.spt.bas.report.server.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.NumberUtil;
import com.spt.bas.report.client.vo.*;
import com.spt.bas.report.server.dao.RptCtrContractMapper;
import com.spt.bas.report.server.dao.RptCtrContractSettlementMapper;
import com.spt.bas.report.server.dao.RptPmApproveMapper;
import com.spt.bas.report.server.service.IRptIndexReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author 杨英承
 * @version 1.0.0
 * @date 2022/6/16 16:11
 */

@Service
public class RptIndexReportServiceImpl implements IRptIndexReportService {

    @Autowired
    private RptCtrContractMapper ctrContractMapper;
    @Autowired
    private RptPmApproveMapper pmApproveMapper;
    @Autowired
    private RptCtrContractSettlementMapper ctrContractSettlementMapper;


    /**
     * 待办统计
     *
     * @param query 查询参数
     */
    @Override
    public RptToDoStatisticsVo backlogStatistics(RptIndexReportQuery query) {
        RptToDoStatisticsVo result = new RptToDoStatisticsVo();
        // 查询采购
        query.setContractType("B");
        RptNobillVo buyData = ctrContractMapper.getNobill(query);
        if(buyData != null){
            result.setBuyNoBill(new RptIndexCommonVo("进项未收发票",buyData.getSumTotalMount(),buyData.getSumCount()));
        }else{
            result.setBuyNoBill(new RptIndexCommonVo("进项未收发票"));
        }

        // 查询销售
        query.setContractType("S");
        RptNobillVo sellData = ctrContractMapper.getNobill(query);
        if(sellData != null){
            result.setSellNoBill(new RptIndexCommonVo("销项未收发票",sellData.getSumTotalMount(),sellData.getSumCount()));
        }else{
            result.setSellNoBill(new RptIndexCommonVo("销项未收发票"));
        }
        return result;
    }


    /**
     * 业务统计
     *
     * @param query 查询参数
     * @return 结果
     */
    @Override
    public List<RptIndexCommonVo> businessStatistics(RptIndexReportQuery query) {
        // 今日
        query.setDayOrMonth("day");
        List<RptIndexCommonVo> dayList = ctrContractMapper.getBuinessStatistic(query);
        // 本月
        query.setDayOrMonth("month");
        List<RptIndexCommonVo> monthList = ctrContractMapper.getBuinessStatistic(query);
        dayList.addAll(monthList);
        return dayList;
    }

    /**
     * 业务统计
     *
     * @param query 查询参数
     * @return 结果
     */
    @Override
    public List<RptIndexCommonVo> businessStatisticsByMonth(RptIndexReportQuery query) {
        query.setDayOrMonth("month");
        List<RptIndexCommonVo> monthList = ctrContractMapper.getBuinessStatisticByMonth(query);
        if(CollectionUtil.isNotEmpty(monthList)){
            for (RptIndexCommonVo indexCommonVo : monthList) {
                indexCommonVo.setTypeName(indexCommonVo.getTypeName().replaceAll("本月",getMonthByNum(query.getLastMonthNum())));
            }
        }
        return monthList;
    }

    protected String getMonthByNum(Integer num) {
        LocalDate today = LocalDate.now();
        today = today.minusMonths(num);
        DateTimeFormatter formatters = DateTimeFormatter.ofPattern("MM");
        return formatters.format(today)+"月";
    }
    /**
     * 业绩提成
     *
     * @param query 查询参数
     * @return 业绩提成统计
     */
    @Override
    public RptPerformanceVo getPerformanceCommission(RptIndexReportQuery query) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
        LocalDate now = LocalDate.now();
        String nowMonth = now.format(formatter);
        // 累计未结算提成
        query.setNameFlag(1);
        query.setDayOrMonth(nowMonth);
        query.setSettleStatus(2);// 已审核
        RptIndexCommonVo unSettlement  = ctrContractSettlementMapper.getPerformanceCommission(query);

        // 预计提成
        query.setNameFlag(2);
        query.setSettleStatus(null);// 所有状态
        RptIndexCommonVo planSettlement  = ctrContractSettlementMapper.getPerformanceCommission(query);

        // 上个月结算提成（上个月）
        String lastMonth = now.plusMonths(-1).format(formatter);
        query.setDayOrMonth(lastMonth);
        query.setNameFlag(3);
        query.setSettleStatus(3);// 已结算
        RptIndexCommonVo laseMonthSettlement  = ctrContractSettlementMapper.getPerformanceCommission(query);
        return new RptPerformanceVo(unSettlement,planSettlement,laseMonthSettlement);
    }

    /**
     * 查询业绩排行
     *
     * @param query 查询参数
     * @return 业绩排行
     */
    @Override
    public List<RptIndexCommonVo> performanceRanking(RptIndexReportQuery query) {
        // 默认查询当前月
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
        LocalDate now = LocalDate.now();
        String nowMonth = now.format(formatter);
        query.setDayOrMonth(nowMonth);
        return ctrContractMapper.getPerformanceRanking(query);
    }

    /**
     * 过去10个月的毛利率
     *
     * @return 毛利率
     */
    @Override
    public List<RptGrossProfitMarginVo> grossProfitMargin(RptGrossProfitMarginSearchVo searchVo) {

        // 获取过去 18 个月
        List<String> eighteenMonths = getPreviousEighteenMonths();
        searchVo.setEighteenMonthList(eighteenMonths);


        List<RptGrossProfitMarginVo> dcsxCapitalCosts = ctrContractMapper.getDcsxCapitalCost(searchVo);
        if (CollectionUtil.isNotEmpty(dcsxCapitalCosts) && dcsxCapitalCosts.size() < 18) {
            dcsxCapitalCosts = getTenGrossDcsxCapitalCost(dcsxCapitalCosts, eighteenMonths);
        }
        Map<String, RptGrossProfitMarginVo> dcsxCapitalCostMap = dcsxCapitalCosts.stream()
                .collect(Collectors.toMap(
                        RptGrossProfitMarginVo::getMonth, // 提取Key的方法
                        Function.identity(),           // Value保持不变，即GrossProfitMarginVo对象本身
                        (vo1, vo2) -> vo1,             // 合并策略，如果有相同的Key，保留第一个值
                        LinkedHashMap::new));
        List<RptGrossProfitMarginVo> capitalCosts = ctrContractMapper.getCapitalCost(searchVo);
        if (CollectionUtil.isNotEmpty(capitalCosts) && capitalCosts.size() < 18) {
            capitalCosts = getTenGrossCapitalCost(capitalCosts, eighteenMonths);
        }
        Map<String, RptGrossProfitMarginVo> capitalCostMap = capitalCosts.stream()
                .collect(Collectors.toMap(
                        RptGrossProfitMarginVo::getMonth, // 提取Key的方法
                        Function.identity(),           // Value保持不变，即GrossProfitMarginVo对象本身
                        (vo1, vo2) -> vo1,             // 合并策略，如果有相同的Key，保留第一个值
                        LinkedHashMap::new));

        // 毛利率
        List<RptGrossProfitMarginVo> grossProfitMarginVos = ctrContractMapper.grossProfitMargin(searchVo);
        // 如果长度不够18，说明是其中一个月没有数据，需要补齐
        if (grossProfitMarginVos.size() < 18) {
            grossProfitMarginVos = getTenGross(grossProfitMarginVos, eighteenMonths);
        }
        grossProfitMarginVos.forEach(grossProfitMarginVo -> {

            BigDecimal dcsxCapitalCost = dcsxCapitalCostMap.get(grossProfitMarginVo.getMonth()) == null
                    ? BigDecimal.ZERO : dcsxCapitalCostMap.get(grossProfitMarginVo.getMonth()).getDcsxCapitalCost();
            BigDecimal capitalCost = capitalCostMap.get(grossProfitMarginVo.getMonth()) == null
                    ? BigDecimal.ZERO : capitalCostMap.get(grossProfitMarginVo.getMonth()).getCapitalCost();

            grossProfitMarginVo.setDcsxCapitalCost(dcsxCapitalCost);
            grossProfitMarginVo.setCapitalCost(capitalCost);
            grossProfitMarginVo.setTotalCapitalCost(NumberUtil.add(dcsxCapitalCost, capitalCost));

            BigDecimal netGrossProfit = grossProfitMarginVo.getNetGrossProfit().subtract(dcsxCapitalCost).subtract(capitalCost);
            grossProfitMarginVo.setNetGrossProfit(netGrossProfit.setScale(2, RoundingMode.HALF_UP));
            if(BigDecimal.ZERO.compareTo(grossProfitMarginVo.getBuyTotalAmount()) == 0) {
                grossProfitMarginVo.setNetRate(BigDecimal.ZERO);
            } else {
                grossProfitMarginVo.setNetRate(netGrossProfit.multiply(BigDecimal.valueOf(100)).divide(grossProfitMarginVo.getBuyTotalAmount(), 2, RoundingMode.HALF_UP));
            }
        });

        return grossProfitMarginVos;
    }

    /**
     * 拼凑18个月的数据，没有的直接置为0
     * @param grossProfitMarginVos 数据
     * @param tenMonths 18月
     * @return
     */
    private List<RptGrossProfitMarginVo> getTenGross(List<RptGrossProfitMarginVo> grossProfitMarginVos, List<String> tenMonths) {
        Map<String, BigDecimal> tenGrossMap = grossProfitMarginVos.stream().collect(Collectors.toMap(RptGrossProfitMarginVo::getMonth, RptGrossProfitMarginVo::getRate, (a, b) -> b));
        Map<String, BigDecimal> tenGrossProfitMap = grossProfitMarginVos.stream().collect(Collectors.toMap(RptGrossProfitMarginVo::getMonth, RptGrossProfitMarginVo::getGrossProfit, (a, b) -> b));
        Map<String, BigDecimal> noReceiveBreachAmountMap = grossProfitMarginVos.stream().collect(Collectors.toMap(RptGrossProfitMarginVo::getMonth, RptGrossProfitMarginVo::getNoReceiveBreachAmount, (a, b) -> b));
        Map<String, BigDecimal> buyTotalAmountMap = grossProfitMarginVos.stream().collect(Collectors.toMap(RptGrossProfitMarginVo::getMonth, RptGrossProfitMarginVo::getBuyTotalAmount, (a, b) -> b));
        return tenMonths.stream().map(covertGrossEntity(tenGrossMap, tenGrossProfitMap, noReceiveBreachAmountMap, buyTotalAmountMap)).collect(Collectors.toList());
    }

    /**
     * 拼凑18个月的代采赊销资金成本数据，没有的直接置为0
     * @param grossProfitMarginVos 数据
     * @param tenMonths 18月
     * @return
     */
    private List<RptGrossProfitMarginVo> getTenGrossDcsxCapitalCost(List<RptGrossProfitMarginVo> grossProfitMarginVos, List<String> tenMonths) {
        Map<String, BigDecimal> tenGrossDcsxCapitalCostMap = grossProfitMarginVos.stream().collect(Collectors.toMap(RptGrossProfitMarginVo::getMonth, RptGrossProfitMarginVo::getDcsxCapitalCost, (a, b) -> b));
        return tenMonths.stream().map(covertDcsxCapitalCostEntity(tenGrossDcsxCapitalCostMap)).collect(Collectors.toList());
    }

    /**
     * 拼凑18个月的普通赊销资金成本数据，没有的直接置为0
     * @param grossProfitMarginVos 数据
     * @param tenMonths 18月
     * @return
     */
    private List<RptGrossProfitMarginVo> getTenGrossCapitalCost(List<RptGrossProfitMarginVo> grossProfitMarginVos, List<String> tenMonths) {
        Map<String, BigDecimal> tenGrossCapitalCostMap = grossProfitMarginVos.stream().collect(Collectors.toMap(RptGrossProfitMarginVo::getMonth, RptGrossProfitMarginVo::getCapitalCost, (a, b) -> b));
        return tenMonths.stream().map(covertCapitalCostEntity(tenGrossCapitalCostMap)).collect(Collectors.toList());
    }

    /**
     * 转化为GrossProfitMarginVo
     * @param tenGrossMap 月份数据集
     * @return 18月数据集
     */
    private Function<String, RptGrossProfitMarginVo> covertGrossEntity(Map<String, BigDecimal> tenGrossMap,
                                                                       Map<String, BigDecimal> tenGrossProfitMap,
                                                                       Map<String, BigDecimal> noReceiveBreachAmountMap,
                                                                       Map<String, BigDecimal> buyTotalAmountMap) {
        return e -> {
            RptGrossProfitMarginVo grossProfitMarginVo = new RptGrossProfitMarginVo();
            grossProfitMarginVo.setMonth(e);
            grossProfitMarginVo.setRate(tenGrossMap.getOrDefault(e, BigDecimal.ZERO));
            grossProfitMarginVo.setGrossProfit(tenGrossProfitMap.getOrDefault(e, BigDecimal.ZERO));
            grossProfitMarginVo.setNoReceiveBreachAmount(noReceiveBreachAmountMap.getOrDefault(e, BigDecimal.ZERO));
            grossProfitMarginVo.setBuyTotalAmount(buyTotalAmountMap.getOrDefault(e, BigDecimal.ZERO));
            return grossProfitMarginVo;
        };
    }
    /**
     * 转化为GrossProfitMarginVo
     * @return 18月数据集
     */
    private Function<String, RptGrossProfitMarginVo> covertDcsxCapitalCostEntity(Map<String, BigDecimal> tenGrossDcsxCapitalCostMap) {
        return e -> {
            RptGrossProfitMarginVo grossProfitMarginVo = new RptGrossProfitMarginVo();
            grossProfitMarginVo.setMonth(e);
            grossProfitMarginVo.setDcsxCapitalCost(tenGrossDcsxCapitalCostMap.getOrDefault(e, BigDecimal.ZERO));
            return grossProfitMarginVo;
        };
    }
    /**
     * 转化为GrossProfitMarginVo
     * @return 18月数据集
     */
    private Function<String, RptGrossProfitMarginVo> covertCapitalCostEntity(Map<String, BigDecimal> tenGrossCapitalCostMap) {
        return e -> {
            RptGrossProfitMarginVo grossProfitMarginVo = new RptGrossProfitMarginVo();
            grossProfitMarginVo.setMonth(e);
            grossProfitMarginVo.setCapitalCost(tenGrossCapitalCostMap.getOrDefault(e, BigDecimal.ZERO));
            return grossProfitMarginVo;
        };
    }

    /**
     * 获取过去18个月的月份数据
     * @return 过去18个月的月份数据
     */
    private List<String> getPreviousEighteenMonths(){
        LocalDate now = LocalDate.now();
        DateTimeFormatter patten = DateTimeFormatter.ofPattern("yyyy-MM");
        List<String> result = new ArrayList<>();
        for(int i = 17 ; i >= 0 ; i--){
            result.add(now.plusMonths(i*(-1)).format(patten));
        }
        return result;
    }

    /**
     * 查询待办统计数量
     *
     * @param query 查询参数
     * @return 待办统计数量
     */
    @Override
    public Integer getApprovalCount(RptIndexReportQuery query) {
        // 待审批数
        Integer approveCount = pmApproveMapper.getApproveCount(query);
        return Objects.nonNull(approveCount) ? approveCount : 0;
    }
}
