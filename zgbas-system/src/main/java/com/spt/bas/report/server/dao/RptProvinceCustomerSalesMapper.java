package com.spt.bas.report.server.dao;

import com.spt.bas.report.client.entity.RptProvinceCustomerSales;
import com.spt.bas.report.client.vo.RptProvinceCustomerSalesVo;
import com.spt.tools.mybatis.annotation.MyBatisDao;

import java.util.List;

@MyBatisDao
public interface RptProvinceCustomerSalesMapper {
    List<RptProvinceCustomerSales> getProvinceCustomerSales(RptProvinceCustomerSalesVo searchVo);
}
