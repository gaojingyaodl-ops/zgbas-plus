package com.spt.bas.report.server.service.impl;

import com.spt.bas.report.client.entity.*;
import com.spt.bas.report.client.vo.RptProfitStatisticsSearchVo;
import com.spt.bas.report.server.dao.RptProfitStatisticsMapper;
import com.spt.bas.report.server.service.IRptProfitStatisticsService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


/**
 * 利润表
 */
@Component
public class RptProfitStatisticsServiceImpl implements IRptProfitStatisticsService {

    @Autowired
    private RptProfitStatisticsMapper rptProfitStatisticsMapper;


    /**
     * 获取利润表数据
     * @param searchVo
     * @return
     */
    @Override
    public RptProfitStatistics getRptProfitStatistics(RptProfitStatisticsSearchVo searchVo) {
        String searchMonth = searchVo.getSearchMonth();
        if(StringUtils.isNotBlank(searchMonth)) {
//            searchVo.setSearchMonth(searchMonth+"-01");
            searchVo.setSearchYear(searchMonth.substring(0,4));
        }
        
        
        RptProfitStatistics entity;
        if (StringUtils.equals("P",searchVo.getType())) {
            entity = rptProfitStatisticsMapper.getRptProfitStatisticsByRealPayFullTime(searchVo);
        } else {
            entity = rptProfitStatisticsMapper.getRptProfitStatistics(searchVo);
        }

        // 主营业务收入
        entity.setMonthBusinessIncomeSum(entity.getMonthDcBusinessIncome().add(entity.getMonthDcsxBusinessIncome()));
        entity.setYearBusinessIncomeSum(entity.getYearDcBusinessIncome().add(entity.getYearDcsxBusinessIncome()));
        // 主营业务成本
        entity.setMonthBusinessCostsSum(entity.getMonthDcBusinessCosts().add(entity.getMonthDcsxBusinessCosts()));
        entity.setYearBusinessCostsSum(entity.getYearDcBusinessCosts().add(entity.getYearDcsxBusinessCosts()));
        // 营业利润
        entity.setMonthBusinessProfitSum(entity.getMonthBusinessIncomeSum().subtract(entity.getMonthBusinessCostsSum()));
        entity.setYearBusinessProfitSum(entity.getYearBusinessIncomeSum().subtract(entity.getYearBusinessCostsSum()));
        // 营业费用
        entity.setMonthBusinessFeeSum(entity.getMonthTransportFee().add(entity.getMonthWarehouseAmount()).add(entity.getMonthCapitalCost())
                .add(entity.getMonthDiscountCost()).add(entity.getMonthTaxes())
                .add(entity.getMonthPersonCost()).add(entity.getMonthTravelExpensesFee()).add(entity.getMonthOtherFee()));
        entity.setYearBusinessFeeSum(entity.getYearTransportFee().add(entity.getYearWarehouseAmount()).add(entity.getYearCapitalCost())
                .add(entity.getYearDiscountCost()).add(entity.getYearTaxes())
                .add(entity.getYearPersonCost()).add(entity.getYearTravelExpensesFee()).add(entity.getYearOtherFee()));
        // 净利润
        entity.setMonthNetProfitSum(entity.getMonthBusinessProfitSum().subtract(entity.getMonthBusinessFeeSum()));
        entity.setYearNetProfitSum(entity.getYearBusinessProfitSum().subtract(entity.getYearBusinessFeeSum()));
        return entity;
    }
}
