package com.spt.bas.report.server.service.impl;

import com.spt.bas.report.client.entity.RptProvinceCustomerSales;
import com.spt.bas.report.client.vo.RptProvinceCustomerSalesVo;
import com.spt.bas.report.server.dao.RptProvinceCustomerSalesMapper;
import com.spt.bas.report.server.service.IRptProvinceCustomerSalesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RptProvinceCustomerSalesServiceIml implements IRptProvinceCustomerSalesService {
    @Autowired
    private RptProvinceCustomerSalesMapper provinceCustomerSalesMapper;

    @Override
    public List<RptProvinceCustomerSales> getProvinceCustomerSales(RptProvinceCustomerSalesVo searchVo) {
        return provinceCustomerSalesMapper.getProvinceCustomerSales(searchVo);
    }
}
