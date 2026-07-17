package com.spt.bas.server.api;

import com.spt.bas.client.entity.ClaimBuyer;
import com.spt.bas.client.entity.PiccShipmentApply;
import com.spt.bas.server.service.IClaimBuyerService;
import com.spt.bas.server.service.IPiccShipmentApplyService;
import com.spt.tools.data.service.BaseApi;
import com.spt.tools.data.service.IDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 人保赊销申请报文
 */
@RestController
@RequestMapping(value = "picc/shipmentApply")
public class PiccShipmentApplyApi extends BaseApi<PiccShipmentApply> {

    @Autowired
    private IPiccShipmentApplyService piccShipmentApplyService;

    @Override
    public IDataService<PiccShipmentApply> getService() {
        return piccShipmentApplyService;
    }

    @PostMapping("findByContractNo")
    public PiccShipmentApply findByContractNo(@RequestBody String contractNo){
        return piccShipmentApplyService.findByContractNo(contractNo);
    }
}
