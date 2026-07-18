package com.spt.bas.report.server.api;

import com.spt.bas.report.client.entity.RptProvinceCustomerSales;
import com.spt.bas.report.client.vo.RptProvinceCustomerSalesVo;
import com.spt.bas.report.server.service.IRptProvinceCustomerSalesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/province/customer/sales")
public class RptProvinceCustomerSalesApi {
    @Autowired
    private IRptProvinceCustomerSalesService provinceCustomerSalesService;

    @PostMapping("getProvinceCustomerSales")
    public List<RptProvinceCustomerSales> getProvinceCustomerSales(@RequestBody RptProvinceCustomerSalesVo searchVo) {
        return provinceCustomerSalesService.getProvinceCustomerSales(searchVo);
    }
}
