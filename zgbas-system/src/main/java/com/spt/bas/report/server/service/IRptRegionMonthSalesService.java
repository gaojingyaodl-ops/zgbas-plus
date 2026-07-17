package com.spt.bas.report.server.service;

import com.spt.bas.report.client.entity.RptRegionMonthSales;
import com.spt.bas.report.client.vo.RptRegionMonthSalesVo;

import java.util.List;

public interface IRptRegionMonthSalesService {
    List<RptRegionMonthSales> getRegionMonthSalesList(RptRegionMonthSalesVo searchVo);
}
