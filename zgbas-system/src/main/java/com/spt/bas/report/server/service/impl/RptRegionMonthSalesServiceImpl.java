package com.spt.bas.report.server.service.impl;

import com.spt.bas.report.client.entity.RptRegionMonthSales;
import com.spt.bas.report.client.vo.RptRegionMonthSalesVo;
import com.spt.bas.report.server.dao.RptRegionMonthSalesMapper;
import com.spt.bas.report.server.service.IRptRegionMonthSalesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RptRegionMonthSalesServiceImpl implements IRptRegionMonthSalesService {
    @Autowired
    private RptRegionMonthSalesMapper regionMonthSalesMapper;
    @Override
    public List<RptRegionMonthSales> getRegionMonthSalesList(RptRegionMonthSalesVo searchVo) {
        return regionMonthSalesMapper.getRegionMonthSalesList(searchVo);
    }
}
