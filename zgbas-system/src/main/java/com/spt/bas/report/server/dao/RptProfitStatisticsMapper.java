package com.spt.bas.report.server.dao;

import com.spt.bas.report.client.entity.RptProfitStatistics;
import com.spt.bas.report.client.vo.RptProfitStatisticsSearchVo;
import com.spt.tools.mybatis.annotation.MyBatisDao;


/**
 * 利润表
 * @author shengong
 */
@MyBatisDao
public interface RptProfitStatisticsMapper {

    /**
     * 获取利润表数据
     */
    RptProfitStatistics getRptProfitStatistics(RptProfitStatisticsSearchVo searchVo);

    /**
     * 根据销售付款日期获取利润表数据
     * @param searchVo
     * @return
     */
    RptProfitStatistics getRptProfitStatisticsByRealPayFullTime(RptProfitStatisticsSearchVo searchVo);

}
