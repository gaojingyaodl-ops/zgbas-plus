package com.spt.bas.client.remote;


import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.CtrContractDelivery;
import com.spt.bas.client.vo.ContractSearchVo;
import com.spt.bas.client.vo.ContractShowVo;
import com.spt.bas.client.vo.CtrContractDeliveryVo;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.data.vo.PageDown;
import com.spt.tools.http.feign.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = BasConstants.SERVER_NAME, path = BasConstants.SERVER_NAME + "/ctr/contractDelivery", url = BasConstants.SERVER_URL, configuration = FeignConfig.class)
public interface ICtrContractDeliveryClient extends BaseClient<CtrContractDelivery> {



    @PostMapping("findPageContract")
    PageDown<CtrContractDeliveryVo> findPageContract(@RequestBody ContractSearchVo queryVo);

    @PostMapping("findByDeliveryId")
      CtrContractDelivery  findByDeliveryId(@RequestBody String  waybillCode);
    
    @PostMapping("findByContractId")
    CtrContractDelivery  findByContractId(@RequestBody Long  contractId);

    @PostMapping("deliveryNoteUpdate")
     void deliveryNoteUpdate(@RequestParam("waybillCode") String  waybillCode, @RequestParam("driverName")  String driverName, @RequestParam("driverPhone") String driverPhone, @RequestParam("plateNumber")  String plateNumber);




}
