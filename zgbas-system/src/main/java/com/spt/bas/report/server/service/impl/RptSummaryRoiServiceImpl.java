package com.spt.bas.report.server.service.impl;

import com.spt.bas.report.client.constant.ReportConstant;
import com.spt.bas.report.client.vo.*;
import com.spt.bas.report.server.dao.RptBaseCostMapper;
import com.spt.bas.report.server.service.IRptSummaryRoiService;
import com.spt.tools.data.vo.PageDown;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author 杨英承
 * @version 1.0.0
 * @date 2023/4/11 16:41
 */
@Service
public class RptSummaryRoiServiceImpl implements IRptSummaryRoiService {

    @Autowired
    private RptBaseCostMapper rptBaseCostMapper;

    protected Logger log = LoggerFactory.getLogger(this.getClass());

    /**
     * 查询每页的数据
     *
     * @param vo 查询参数
     * @return 结果
     */
    @Override
    public RptSummaryResultVo findPage(RptUserRoiVo vo) {
        List<RptSummaryRoiResultVo> summaryRoiResult = getSummaryRoiResult(vo);
        summaryRoiResult.sort(Comparator.comparing(RptSummaryRoiResultVo::getBusinessTypeName).thenComparing(RptSummaryRoiResultVo::getBranchName));
        RptSummaryResultVo result = new RptSummaryResultVo();
        if (CollectionUtils.isEmpty(summaryRoiResult)) {
            return result;
        }
        Map<String, Object> footer = getTotal(summaryRoiResult);
        PageDown<RptSummaryRoiResultVo> page = new PageDown<>();
        page.setContent(summaryRoiResult);
        page.setTotalElements(summaryRoiResult.size());
        result.setPage(page);
        result.setFooter(footer);
        return result;
    }

    @Override
    public List<RptSummaryRoiResultVo> getSummaryRoiResult(RptUserRoiVo vo) {
        List<RptBaseCostAndContractVo> baseCostAndContract = getAllData(vo);
        Map<String, BigDecimal> totalUserCountMap = getTotalUserCount(vo.getBaseDate());

        Map<String, Map<String, List<RptBaseCostAndContractVo>>> summaryMap = baseCostAndContract.stream()
                .collect(Collectors.groupingBy(RptBaseCostAndContractVo::getBusinessName,
                        LinkedHashMap::new,
                        Collectors.groupingBy(RptBaseCostAndContractVo::getBranchName, LinkedHashMap::new, Collectors.toList())));

        List<RptSummaryRoiResultVo> allResult = new ArrayList<>();
        for (Map.Entry<String, Map<String, List<RptBaseCostAndContractVo>>> mapEntry : summaryMap.entrySet()) {
            Map<String, List<RptBaseCostAndContractVo>> item = mapEntry.getValue();
            if (MapUtils.isNotEmpty(item)) {
                for (Map.Entry<String, List<RptBaseCostAndContractVo>> data : item.entrySet()) {
                    RptSummaryRoiResultVo summaryRoiResultVo = new RptSummaryRoiResultVo();
                    summaryRoiResultVo.setBusinessTypeName(mapEntry.getKey());// 业务类型名称
                    summaryRoiResultVo.setBranchName(data.getKey());// 区域名称
                    List<RptBaseCostAndContractVo> list = data.getValue();
                    String branchCd = "";
                    if(CollectionUtils.isNotEmpty(list)) {
                        branchCd = list.get(0).getBranchCd();
                    }
                    summaryRoiResultVo.setBranchCd(branchCd);// 区域Cd
                    int orderCount = getOrderCount(list);
                    summaryRoiResultVo.setOrderCount(orderCount);// 订单数量
                    summaryRoiResultVo.setTonnes(getTonnes(list).setScale(2, RoundingMode.HALF_UP));
                    summaryRoiResultVo.setBusinessUserCount(getBusinessUserCount(list));// 业务人数
                    summaryRoiResultVo.setBaseDate(vo.getBaseDate());// 年、月
                    BigDecimal buyMoney = getBuyMoney(list);// 总采购额
                    BigDecimal sellMoney = getSellMoney(list);// 总销售额
                    BigDecimal steveDorageAmount = getSumMoney(list, RptBaseCostAndContractVo::getSteveDorageAmount);// 总销售额
                    BigDecimal transportAmount = getSumMoney(list, RptBaseCostAndContractVo::getTransportAmount);// 总运输费
                    BigDecimal warehouseAmount = getSumMoney(list, RptBaseCostAndContractVo::getWarehouseAmount);// 总仓储费
                    summaryRoiResultVo.setSellMoney(toTenThousand(sellMoney));// 销售额
                    String businessUserCountStr = Objects.isNull(summaryRoiResultVo.getBusinessUserCount()) ? "0" : summaryRoiResultVo.getBusinessUserCount().toString();
                    BigDecimal businessUserCountBigDecimal = new BigDecimal(businessUserCountStr);
                    BigDecimal sellLabor = getSellLabor(summaryRoiResultVo.getSellMoney(), businessUserCountBigDecimal);
                    summaryRoiResultVo.setSellLabor(sellLabor);// 销售额人效
                    BigDecimal gross = getGross(sellMoney, buyMoney, steveDorageAmount, transportAmount, warehouseAmount);
                    summaryRoiResultVo.setGross(toTenThousand(gross));// 毛利
                    summaryRoiResultVo.setGrossLabor(getGrossLabor(gross, businessUserCountBigDecimal));// 毛利人效
                    summaryRoiResultVo.setGrossAvg(getGrossAvg(list, totalUserCountMap.getOrDefault(data.getKey(), BigDecimal.ZERO)));// 毛利率均值
                    allResult.add(summaryRoiResultVo);
                }
            }
        }

        return allResult;
    }

    private BigDecimal getSumMoney(List<RptBaseCostAndContractVo> list, Function<RptBaseCostAndContractVo, BigDecimal> function) {
        if (CollectionUtils.isEmpty(list)) {
            return BigDecimal.ZERO;
        }
        return list.stream().map(function)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private Integer getOrderCount(List<RptBaseCostAndContractVo> list) {
        if (CollectionUtils.isEmpty(list)) {
            return 0;
        }
        return list.stream().map(RptBaseCostAndContractVo::getSellContractNo).collect(Collectors.toSet()).size();

    }

    /**
     * 获取吨数
     *
     * @param list 分组数据
     * @return 吨数
     */
    private BigDecimal getTonnes(List<RptBaseCostAndContractVo> list) {
        if (CollectionUtils.isEmpty(list)) {
            return BigDecimal.ZERO;
        }
        return list.stream().map(RptBaseCostAndContractVo::getDealNumber).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * 转为万元
     *
     * @param bigDecimal 值
     * @return 结果
     */
    private BigDecimal toTenThousand(BigDecimal bigDecimal) {
        return toBigDecimal(bigDecimal).divide(BigDecimal.valueOf(10000), RoundingMode.HALF_UP);
    }

    /**
     * 转为bigDecimal
     *
     * @param bigDecimal 值
     * @return 结果
     */
    private BigDecimal toBigDecimal(BigDecimal bigDecimal) {
        return Objects.isNull(bigDecimal) ? BigDecimal.ZERO : bigDecimal;
    }

    /**
     * 毛利人效 = 毛利 / 区域业务总人数
     *
     * @param gross             毛利
     * @param businessUserCount 区域业务人数
     * @return 毛利人效
     */
    private BigDecimal getGrossLabor(BigDecimal gross, BigDecimal businessUserCount) {
        if (BigDecimal.ZERO.compareTo(businessUserCount) == 0) {
            return BigDecimal.ZERO;
        }
        return gross.divide(businessUserCount, 2, RoundingMode.HALF_UP);
    }

    /**
     * 获取业务成本总人数
     *
     * @param baseDate 年月
     * @return 总人数
     */
    private Map<String,BigDecimal> getTotalUserCount(String baseDate) {
        List<RptBaseCostReportVo> result = rptBaseCostMapper.selectUser(baseDate);
        if (CollectionUtils.isEmpty(result)) {
            return new HashMap<>();
        }
        return result.stream().collect(Collectors.groupingBy(RptBaseCostReportVo::getBranchName, Collectors.collectingAndThen(Collectors.toList(), list -> {
            if (CollectionUtils.isEmpty(list)) {
                return BigDecimal.ZERO;
            }
            int size = list.stream().map(RptBaseCostReportVo::getMatchUserId).collect(Collectors.toSet()).size();
            return new BigDecimal(size);
        })));
    }

    /**
     * 毛利率均值 = 毛利 / 总人数
     *
     * @return 毛利率
     */
    private BigDecimal getGrossAvg(List<RptBaseCostAndContractVo> list, BigDecimal totalUser) {
        if (BigDecimal.ZERO.compareTo(totalUser) == 0) {
            return BigDecimal.ZERO;
        }
        BigDecimal grossAvgSum = BigDecimal.ZERO;
        if (CollectionUtils.isNotEmpty(list)) {
            // 每个人毛利率总和 = 总毛利（该人做的所有单子毛利累加=总毛利） / 总采购（该人做的所有单子采购累加=总采购）
            Map<Long, BigDecimal> everyGrossAvg = list.stream().collect(Collectors.groupingBy(RptBaseCostAndContractVo::getMatchUserId, Collectors.collectingAndThen(Collectors.toList(), userList -> {
                BigDecimal buyMoneyOne = getBuyMoney(list);
                BigDecimal sellMoneyOne = getSellMoney(list);
                BigDecimal warehouseAmount = getSumMoney(list, RptBaseCostAndContractVo::getWarehouseAmount);
                BigDecimal transportAmount = getSumMoney(list, RptBaseCostAndContractVo::getTransportAmount);
                BigDecimal steveDorageAmount = getSumMoney(list, RptBaseCostAndContractVo::getSteveDorageAmount);
                BigDecimal grossOne = sellMoneyOne.subtract(buyMoneyOne).subtract(warehouseAmount).subtract(transportAmount).subtract(steveDorageAmount);// 总毛利
                log.info("buyMoneyOne={},sellMoneyOne={},grossOne={}", buyMoneyOne, sellMoneyOne, grossOne);
                if (BigDecimal.ZERO.compareTo(buyMoneyOne) == 0) {
                    return BigDecimal.ZERO;
                }
                return grossOne.divide(buyMoneyOne, 2, RoundingMode.HALF_UP);
            })));
            grossAvgSum = everyGrossAvg.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add);
            log.info("grossAvgSum={},totalUser={}", grossAvgSum, totalUser);
        }
        return grossAvgSum.divide(totalUser, 2, RoundingMode.HALF_UP);
    }

    /**
     * 毛利 = 销售总价 - 采购总价
     *
     * @param sellMoney 销售总额
     * @param buyMoney  采购总额
     * @return 毛利
     */
    private BigDecimal getGross(BigDecimal sellMoney, BigDecimal buyMoney, BigDecimal steveDorageAmount, BigDecimal transportAmount, BigDecimal warehouseAmount) {
        return sellMoney.subtract(buyMoney)
                .subtract(steveDorageAmount)
                .subtract(transportAmount)
                .subtract(warehouseAmount);
    }

    /**
     * 获取采购总价
     *
     * @param list 分组数据
     * @return 采购总价
     */
    private BigDecimal getBuyMoney(List<RptBaseCostAndContractVo> list) {
        if (CollectionUtils.isEmpty(list)) {
            return BigDecimal.ZERO;
        }
        return list.stream().map(RptBaseCostAndContractVo::getBuyTotalAmount).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * 销售额人效
     *
     * @param sellMoney         销售额
     * @param businessUserCount 业务人数
     * @return 销售额人效
     */
    private BigDecimal getSellLabor(BigDecimal sellMoney, BigDecimal businessUserCount) {
        if (BigDecimal.ZERO.compareTo(businessUserCount) == 0) {
            return BigDecimal.ZERO;
        }
        return sellMoney.divide(businessUserCount, 2, RoundingMode.HALF_UP);
    }

    /**
     * 获取销售额
     *
     * @param list 分组数据
     * @return 销售额
     */
    private BigDecimal getSellMoney(List<RptBaseCostAndContractVo> list) {
        if (CollectionUtils.isEmpty(list)) {
            return BigDecimal.ZERO;
        }
        return list.stream().map(RptBaseCostAndContractVo::getSellTotalAmount).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * 获取业务人数
     *
     * @param list 分组数据
     * @return 业务人数
     */
    private Integer getBusinessUserCount(List<RptBaseCostAndContractVo> list) {
        if (CollectionUtils.isEmpty(list)) {
            return 0;
        }
        return list.stream().map(RptBaseCostAndContractVo::getMatchUserId).collect(Collectors.toSet()).size();
    }

    private List<RptBaseCostAndContractVo> getAllData(RptUserRoiVo vo) {
        List<RptBaseCostAndContractVo> result = rptBaseCostMapper.findBaseCostAndContract(vo.getBaseDate(), vo.getBranchCd(),vo.getBranchCdList());
        if (CollectionUtils.isEmpty(result)) {
            return new ArrayList<>(0);
        }
        for (RptBaseCostAndContractVo item : result) {
            item.setBusinessName(conversionBusinessName(item.getBusinessType(), item.getMatchCreditFlg(), item.getBusinessTypeDcsx()));
        }
        return result;
    }

    /**
     * 转化
     *
     * @return 结果
     */
    private String conversionBusinessName(String businessType, Boolean flag, String businessTypeDcsx) {
        if (StringUtils.equals(ReportConstant.BUSINESS_TYPE_ZY_BB, businessType) && flag) {
            return "赊销";
        } else if (StringUtils.equals(ReportConstant.BUSINESS_TYPE_ZY_BB, businessType) && !flag) {
            return "代采";
        } else{
            return "";
        }
    }


    /**
     * 计算合计
     *
     * @param summaryRoiResult
     * @return
     */
    private Map<String, Object> getTotal(List<RptSummaryRoiResultVo> summaryRoiResult) {

        HashMap<String, Object> resultMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(summaryRoiResult)) {
            Integer orderCount = 0;
            BigDecimal tonnes = BigDecimal.ZERO;
            BigDecimal sellMoney = BigDecimal.ZERO;
            BigDecimal sellLabor = BigDecimal.ZERO;
            BigDecimal gross = BigDecimal.ZERO;
            BigDecimal grossLabor = BigDecimal.ZERO;
            BigDecimal grossAvg = BigDecimal.ZERO;
            for (RptSummaryRoiResultVo item : summaryRoiResult) {
                orderCount += item.getOrderCount();
                sellMoney = sellMoney.add(item.getSellMoney());
                tonnes = tonnes.add(item.getTonnes());
                sellLabor = sellLabor.add(item.getSellLabor());
                gross = gross.add(item.getGross());
                grossLabor = grossLabor.add(item.getGrossLabor());
                grossAvg = grossAvg.add(item.getGrossAvg());

            }
            resultMap.put("businessTypeName", "合计");
            resultMap.put("orderCount", orderCount);
            resultMap.put("tonnes", tonnes);
            resultMap.put("sellMoney", sellMoney);
            resultMap.put("sellLabor", sellLabor);
            resultMap.put("gross", gross);
            resultMap.put("grossLabor", grossLabor);
            resultMap.put("grossAvg", grossAvg);
        }
        return resultMap;
    }
}
