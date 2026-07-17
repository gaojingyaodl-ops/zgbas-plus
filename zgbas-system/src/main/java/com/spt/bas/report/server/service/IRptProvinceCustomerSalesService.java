package com.spt.bas.report.server.service;

import com.spt.bas.report.client.entity.RptProvinceCustomerSales;
import com.spt.bas.report.client.vo.RptProvinceCustomerSalesVo;

import java.util.List;

public interface IRptProvinceCustomerSalesService {
    List<RptProvinceCustomerSales> getProvinceCustomerSales(RptProvinceCustomerSalesVo searchVo);
}
