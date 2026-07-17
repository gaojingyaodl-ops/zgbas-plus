package com.spt.bas.report.server.service.impl;

import com.spt.bas.client.entity.CtrContractSettlement;
import com.spt.bas.report.client.vo.RptBaseCostReportVo;
import com.spt.bas.report.client.vo.RptUserRoiResultVo;
import com.spt.bas.report.client.vo.RptUserRoiVo;
import com.spt.bas.report.server.dao.RptBaseCostMapper;
import com.spt.bas.report.server.service.IRptUserRoiService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author 杨英承
 * @version 1.0.0
 * @date 2023/3/21 15:58
 */
@Service
public class RptUserRoiServiceImpl implements IRptUserRoiService {

    @Autowired
    private RptBaseCostMapper rptBaseCostMapper;

    /**
     * 毛利 = 销售总价 - 采购总价;
     * 毛利率均值 = 每单毛利之和 / 订单数;
     * 总投入 = 当月实发工资 + 出差 + 社保;
     * ROI = （毛利-总投入）/总投入;
     *
     * @param vo 查询参数
     * @return 结果
     */
    @Override
    public List<RptUserRoiResultVo> findPage(RptUserRoiVo vo) {
        return getUserRoiResultVoList(vo);
    }

    @Override
    public List<RptUserRoiResultVo> getUserRoiResultVoList(RptUserRoiVo vo) {
        if (CollectionUtils.isEmpty(vo.getUserList())) {
            return new ArrayList<>();
        }
        String baseStartDate = vo.getBaseStartDate();
        String baseEndDate = vo.getBaseEndDate();
        List<RptBaseCostReportVo> content = rptBaseCostMapper.selectUserRoi(baseStartDate, baseEndDate, vo.getUserList());

        // 查询所有的 CtrContractSettlement 数据
        List<CtrContractSettlement> contractSettlements = rptBaseCostMapper.selectContractSettlements(baseStartDate, baseEndDate);
        // 将 CtrContractSettlement 数据按照 matchUserId 分组
        Map<Long, List<CtrContractSettlement>> settlementMap = contractSettlements.stream()
                .collect(Collectors.groupingBy(CtrContractSettlement::getSellMatchUserId));

        // 将 CtrContractSettlement 列表关联到对应的 RptBaseCostReportVo
        for (RptBaseCostReportVo report : content) {
            StringBuilder baseDate = new StringBuilder();
            if (StringUtils.isNotBlank(baseStartDate) && StringUtils.isNotBlank(baseEndDate) && StringUtils.equals(baseStartDate, baseEndDate)) {
                baseDate.append(baseStartDate);
            } else {
                if (StringUtils.isNotBlank(baseStartDate)) {
                    baseDate.append(baseStartDate);
                } else {
                    baseDate.append("-∞");
                }
                baseDate.append(" ~ ");
                if (StringUtils.isNotBlank(baseEndDate)) {
                    baseDate.append(baseEndDate);
                } else {
                    baseDate.append("∞");
                }
            }
            report.setBaseDate(baseDate.toString());

            Long key = report.getMatchUserId();
            report.setContractSettlementList(settlementMap.getOrDefault(key, new ArrayList<>()));
        }


        List<RptUserRoiResultVo> result = new ArrayList<>();

        for (RptBaseCostReportVo baseCost : content) {
            RptUserRoiResultVo resultVo = new RptUserRoiResultVo();
            BeanUtils.copyProperties(baseCost, resultVo);
            List<CtrContractSettlement> contractList = baseCost.getContractSettlementList();
            BigDecimal totalTunnageSum = BigDecimal.ZERO;
            BigDecimal sellAmountSum = BigDecimal.ZERO;
            BigDecimal buyAmountSum = BigDecimal.ZERO;
            BigDecimal transportAmountSum = BigDecimal.ZERO;
            BigDecimal warehouseAmountSum = BigDecimal.ZERO;
            BigDecimal steveDorageAmountSum = BigDecimal.ZERO;
            BigDecimal grossAvgSum = BigDecimal.ZERO;
            // 销售提成
            BigDecimal sellMatchAmountSum = BigDecimal.ZERO;
            // 保险费 （销售总额*保险费率/1.06）
            BigDecimal insuranceAmountSum = BigDecimal.ZERO;
            // 出库费
            BigDecimal deliveryFeeSum = BigDecimal.ZERO;
            // 增值税
            BigDecimal vatAmountSum = BigDecimal.ZERO;
            // 印花税
            BigDecimal printAmountSum = BigDecimal.ZERO;
            
            if (CollectionUtils.isNotEmpty(contractList)) {
                for (CtrContractSettlement ctrContractSettlement : contractList) {
                    totalTunnageSum = toBigDecimal(ctrContractSettlement.getDealNumber()).add(totalTunnageSum);
                    BigDecimal sell = toBigDecimal(ctrContractSettlement.getSellTotalAmount());
                    BigDecimal buy = toBigDecimal(ctrContractSettlement.getBuyTotalAmount());
                    BigDecimal transportAmount = toBigDecimal(ctrContractSettlement.getTransportAmount());
                    BigDecimal warehouseAmount = toBigDecimal(ctrContractSettlement.getWarehouseAmount());
                    BigDecimal steveDorageAmount = toBigDecimal(ctrContractSettlement.getSteveDorageAmount());
                    BigDecimal sellMatchAmount = toBigDecimal(ctrContractSettlement.getSellMatchAmount());
                    BigDecimal insuranceRate = toBigDecimal(ctrContractSettlement.getInsuranceRate());
                    BigDecimal deliveryFee = toBigDecimal(ctrContractSettlement.getDeliveryFee());
                    BigDecimal vatAmount = toBigDecimal(ctrContractSettlement.getVatAmount());
                    BigDecimal printAmount = toBigDecimal(ctrContractSettlement.getPrintAmount());
                    
                    sellAmountSum = sell.add(sellAmountSum);
                    buyAmountSum = buy.add(buyAmountSum);
                    transportAmountSum = transportAmount.add(transportAmountSum);
                    warehouseAmountSum = warehouseAmount.add(warehouseAmountSum);
                    steveDorageAmountSum = steveDorageAmount.add(steveDorageAmountSum);
                    sellMatchAmountSum = sellMatchAmount.add(sellMatchAmountSum);
                    insuranceAmountSum = (sell.multiply(insuranceRate)).divide(new BigDecimal(1.06),2, RoundingMode.HALF_UP).add(insuranceAmountSum);
                    deliveryFeeSum = deliveryFee.add(deliveryFeeSum);
                    vatAmountSum = vatAmount.add(vatAmountSum);
                    printAmountSum = printAmount.add(printAmountSum);
                    if (BigDecimal.ZERO.compareTo(buyAmountSum) != 0) {
                        // 本单毛利
                        BigDecimal grossOne = sell.subtract(buy).subtract(transportAmount).subtract(warehouseAmount).subtract(steveDorageAmount);
                        BigDecimal grossOneAvg = grossOne.divide(buy, 2, RoundingMode.HALF_UP);
                        grossAvgSum = grossAvgSum.add(grossOneAvg);
                    }
                }
            }
            // 毛利
            BigDecimal gross = sellAmountSum.subtract(buyAmountSum).subtract(transportAmountSum).subtract(warehouseAmountSum).subtract(steveDorageAmountSum);

            // 净毛利：(销售总额-采购总额)/1.13-运费/1.09 - (仓储费+出库费+装卸费)/1.06 - 保险费 - 附加税 - 印花税
            BigDecimal factor1 = new BigDecimal(1.13);
            BigDecimal factor2 = new BigDecimal(1.09);
            BigDecimal factor3 = new BigDecimal(1.06);
            BigDecimal factor4 = new BigDecimal(0.12);
            
            BigDecimal netGross = (sellAmountSum.subtract(buyAmountSum)).divide(factor1, RoundingMode.HALF_UP)  // 需要考虑除法时的精度
                    .subtract( (transportAmountSum.divide(factor2, RoundingMode.HALF_UP)) )
                    .subtract( ((warehouseAmountSum.add(deliveryFeeSum).add(steveDorageAmountSum)).divide(factor3, RoundingMode.HALF_UP)) )
                    .subtract(insuranceAmountSum)
                    .subtract( (vatAmountSum.multiply(factor4) ))
                    .subtract(printAmountSum);
            // 总投入
            BigDecimal totalFinancing = toBigDecimal(baseCost.getTotalCost());
            int orderCount = contractList.size();
            // 订单数
            resultVo.setOrderCount(orderCount);
            // 吨数
            resultVo.setTunnage(totalTunnageSum);
            // 销售额
            resultVo.setSellMoney(toTenThousand(sellAmountSum));
            // 毛利
            resultVo.setGross(toTenThousand(netGross));
            // 总投入
            resultVo.setTotalFinancing(toTenThousand(totalFinancing));
            // 毛利均值
            resultVo.setGrossAvg(getGrossAvg(grossAvgSum, new BigDecimal(orderCount)));
            // 提成
            resultVo.setCommission(toTenThousand(sellMatchAmountSum));
            resultVo.setEvectionCost(toTenThousand(baseCost.getEvectionCost()));
            BigDecimal roi = BigDecimal.ZERO;
            if (resultVo.getTotalFinancing().compareTo(BigDecimal.ZERO) != 0 ) {
                roi = totalFinancing.compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ZERO : resultVo.getGross().subtract(resultVo.getTotalFinancing()).divide(resultVo.getTotalFinancing(), 2, RoundingMode.HALF_UP);
            }
            resultVo.setRoi(roi);
            result.add(resultVo);
        }
//        result.sort(new Comparator<UserRoiResultVo>() {
//            @Override
//            public int compare(UserRoiResultVo a, UserRoiResultVo b) {
//                int sort = a.getBranchCd().compareTo(b.getBranchCd());
//                if (sort == 0) {
//                    BigDecimal subtract = b.getRoi().subtract(a.getRoi());
//                    return subtract.intValue();
//                } else {
//                    return sort;
//                }
//            }
//        });
        result.sort(Comparator.comparing(RptUserRoiResultVo::getBaseDate,Comparator.reverseOrder())
                .thenComparing(RptUserRoiResultVo::getBranchName)
                .thenComparing(RptUserRoiResultVo::getRoi,Comparator.reverseOrder()));
        return result;
    }

    /**
     * 毛利率均值 = sum(每单毛利率) / 订单数;
     *
     * @param grossAvgSum sum(每单毛利率)
     * @param orderCount  订单数
     * @return 毛利率均值
     */
    private BigDecimal getGrossAvg(BigDecimal grossAvgSum, BigDecimal orderCount) {
        if (BigDecimal.ZERO.compareTo(orderCount) == 0) {
            return BigDecimal.ZERO;
        }
        return grossAvgSum.divide(orderCount, 2, RoundingMode.HALF_UP);
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
     * 转为万元
     *
     * @param bigDecimal 值
     * @return 结果
     */
    private BigDecimal toTenThousand(BigDecimal bigDecimal) {
        return toBigDecimal(bigDecimal).divide(BigDecimal.valueOf(10000), 2, RoundingMode.HALF_UP);
    }

    /**
     * 合计
     *
     * @return 合计
     */
    @Override
    public Map<String, Object> getTotal(RptUserRoiVo userRoiVo) {
        List<RptUserRoiResultVo> userRoiResultVoList = getUserRoiResultVoList(userRoiVo);
        Map<String, Object> result = new HashMap<>();
        Integer sumOrderCount = 0;// 订单合计
        BigDecimal sumTunnage = BigDecimal.ZERO;// 吨数合计
        BigDecimal sumSellMoney = BigDecimal.ZERO;// 销售额合计
        BigDecimal sumGross = BigDecimal.ZERO;// 毛利合计
        BigDecimal sumGrossAvg = BigDecimal.ZERO;// 毛利均值合计
        BigDecimal sumTotalFinancing = BigDecimal.ZERO;// 总投入合计
        BigDecimal sumCommission = BigDecimal.ZERO;// 提成合计
        BigDecimal sumEvectionCost = BigDecimal.ZERO;// 出差报销费用合计
        BigDecimal sumRoi = BigDecimal.ZERO;// roi合计
        for (RptUserRoiResultVo item : userRoiResultVoList) {
            sumOrderCount += item.getOrderCount();
            sumTunnage = sumTunnage.add(item.getTunnage());
            sumSellMoney = sumSellMoney.add(item.getSellMoney());
            sumGross = sumGross.add(item.getGross());
            sumGrossAvg = sumGrossAvg.add(item.getGrossAvg());
            sumTotalFinancing = sumTotalFinancing.add(item.getTotalFinancing());
            sumCommission = sumCommission.add(item.getCommission());
            sumEvectionCost = sumEvectionCost.add(item.getEvectionCost());
//            sumRoi = sumRoi.add(item.getRoi());
        }
        if (sumTotalFinancing.compareTo(BigDecimal.ZERO) > 0) {
            sumRoi = sumGross.subtract(sumTotalFinancing).divide(sumTotalFinancing,2, RoundingMode.HALF_UP);
        }
        result.put("matchUserName", "合计");
        result.put("orderCount", sumOrderCount);
        result.put("tunnage", sumTunnage);
        result.put("sellMoney", sumSellMoney);
        result.put("gross", sumGross);
        result.put("grossAvg", sumGrossAvg);
        result.put("totalFinancing", sumTotalFinancing);
        result.put("commission", sumCommission);
        result.put("evectionCost", sumEvectionCost);
        result.put("roi", sumRoi);
        return result;
    }
}
