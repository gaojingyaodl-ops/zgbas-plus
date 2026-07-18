package com.spt.bas.report.server.api;

import com.spt.bas.report.client.vo.RptCtrContractUnDeliverySearchVo;
import com.spt.bas.report.client.vo.RptCtrContractUnDeliveryVo;
import com.spt.bas.report.server.service.IRptCtrContractUnDeliveryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/rpt/unDelivery")
public class RptCtrContractUnDeliveryApi {
    @Autowired
    private IRptCtrContractUnDeliveryService contractUnDeliveryService;

    @PostMapping("findUnDeliveryPage")
    public Page<RptCtrContractUnDeliveryVo> findUnDeliveryPage(@RequestBody RptCtrContractUnDeliverySearchVo searchVo){
        return contractUnDeliveryService.findUnDeliveryPage(searchVo);
    }
    
    
}
