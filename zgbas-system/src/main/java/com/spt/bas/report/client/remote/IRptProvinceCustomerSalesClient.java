package com.spt.bas.report.client.remote;

import com.spt.bas.report.client.constant.ReportConstant;
import com.spt.bas.report.client.entity.RptProvinceCustomerSales;
import com.spt.bas.report.client.vo.RptProvinceCustomerSalesVo;
import com.spt.tools.http.feign.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = ReportConstant.SERVER_NAME, path = ReportConstant.SERVER_NAME + "/province/customer/sales", url = ReportConstant.SERVER_URL, configuration = FeignConfig.class)

public interface IRptProvinceCustomerSalesClient {
    @PostMapping("getProvinceCustomerSales")
    public List<RptProvinceCustomerSales> getProvinceCustomerSales(@RequestBody RptProvinceCustomerSalesVo searchVo);
}
