package com.spt.bas.server.api;


import com.spt.bas.client.entity.CtrContractDelivery;
import com.spt.bas.client.vo.ContractSearchVo;
import com.spt.bas.client.vo.ContractShowVo;
import com.spt.bas.client.vo.CtrContractDeliveryVo;
import com.spt.bas.server.service.ICtrContractDeliveryService;
import com.spt.tools.data.service.BaseApi;
import com.spt.tools.data.service.IDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping(value = "/ctr/contractDelivery")
public class CtrContractDeliveryApi  extends BaseApi<CtrContractDelivery> {


    @Resource
    private ICtrContractDeliveryService ctrContractDeliveryService;

    @Override
    public IDataService<CtrContractDelivery> getService() {
        return ctrContractDeliveryService;
    }

    @PostMapping("findByDeliveryId")
    public CtrContractDelivery findByDeliveryId(@RequestBody String waybillCode){
        return ctrContractDeliveryService.findByDeliveryId(waybillCode);
    };
    @PostMapping("findByContractId")
    public CtrContractDelivery findByContractId(@RequestBody Long contractId){
        return ctrContractDeliveryService.findByContractId(contractId);
    };

    @PostMapping("findPageContract")
    public Page<CtrContractDeliveryVo> findPageContract(@RequestBody ContractSearchVo queryVo){
        return ctrContractDeliveryService.findPageContract(queryVo);
    }


    @PostMapping("deliveryNoteUpdate")
    public void deliveryNoteUpdate(@RequestParam("waybillCode") String  waybillCode, @RequestParam("driverName")  String driverName,@RequestParam("driverPhone") String driverPhone, @RequestParam("plateNumber")  String plateNumber){
        ctrContractDeliveryService.deliveryNoteUpdate(waybillCode,driverName,driverPhone,plateNumber);
    }



}
