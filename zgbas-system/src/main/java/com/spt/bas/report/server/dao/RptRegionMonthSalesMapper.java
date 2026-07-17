package com.spt.bas.report.server.dao;

import com.spt.bas.report.client.entity.RptRegionMonthSales;
import com.spt.bas.report.client.vo.RptRegionMonthSalesVo;
import com.spt.tools.mybatis.annotation.MyBatisDao;

import java.util.List;

@MyBatisDao
public interface RptRegionMonthSalesMapper {
    List<RptRegionMonthSales> getRegionMonthSalesList(RptRegionMonthSalesVo searchVo);
}
