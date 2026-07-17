package com.spt.bas.server.service;

import com.spt.bas.client.entity.CtrContractApply;
import com.spt.bas.client.entity.CtrContractDelivery;
import com.spt.bas.client.vo.ContractSearchVo;
import com.spt.bas.client.vo.ContractShowVo;
import com.spt.bas.client.vo.CtrContractDeliveryVo;
import com.spt.tools.jpa.service.IBaseService;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.Query;

public interface ICtrContractDeliveryService  extends IBaseService<CtrContractDelivery> {

    CtrContractDelivery findByDeliveryId(String   waybillCode);
    CtrContractDelivery findByContractId(Long   contractId);

    Page<CtrContractDeliveryVo> findPageContract(ContractSearchVo queryVo);

     void deliveryNoteUpdate(String waybillCode, String driverName,String  driverPhone,  String plateNumber);

}
