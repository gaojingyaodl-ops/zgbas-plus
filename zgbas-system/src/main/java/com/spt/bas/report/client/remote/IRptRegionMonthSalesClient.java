package com.spt.bas.report.client.remote;

import com.spt.bas.report.client.constant.ReportConstant;
import com.spt.bas.report.client.entity.RptRegionMonthSales;
import com.spt.bas.report.client.vo.RptRegionMonthSalesVo;
import com.spt.tools.http.feign.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = ReportConstant.SERVER_NAME,path= ReportConstant.SERVER_NAME+"/region/month/sales",url=ReportConstant.SERVER_URL,configuration= FeignConfig.class)

public interface IRptRegionMonthSalesClient {
    @PostMapping("getRegionMonthSalesList")
    public List<RptRegionMonthSales> getRegionMonthSalesList(@RequestBody RptRegionMonthSalesVo searchVo);
}
