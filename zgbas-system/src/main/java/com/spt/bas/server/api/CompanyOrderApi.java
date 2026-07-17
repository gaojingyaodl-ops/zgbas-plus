package com.spt.bas.server.api;

import com.spt.bas.client.vo.CompanyOrderResVo;
import com.spt.bas.server.service.ICtrContractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "company/order")
public class CompanyOrderApi {
    @Autowired
    private ICtrContractService contractService;

    /**
     * 查询交易记录
     * @param minute
     * @return
     */
    @PostMapping("findCompanyOrder")
    List<CompanyOrderResVo> findCompanyOrder(@RequestBody String minute){
        return contractService.findCompanyOrder(minute);
    }
    
}
