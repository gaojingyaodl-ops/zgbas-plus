package com.spt.bas.report.server.api;

import com.spt.bas.report.client.entity.RptRegionMonthSales;
import com.spt.bas.report.client.vo.RptRegionMonthSalesVo;
import com.spt.bas.report.server.service.IRptRegionMonthSalesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/region/month/sales")
public class RptRegionMonthSalesApi {
    @Autowired
    private IRptRegionMonthSalesService regionMonthSalesService;
    @PostMapping("getRegionMonthSalesList")
    public List<RptRegionMonthSales> getRegionMonthSalesList(@RequestBody RptRegionMonthSalesVo searchVo){
        return regionMonthSalesService.getRegionMonthSalesList(searchVo);
    }
}
