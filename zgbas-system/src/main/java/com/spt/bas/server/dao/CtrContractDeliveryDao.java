package com.spt.bas.server.dao;

import com.spt.bas.client.entity.CtrContract;
import com.spt.bas.client.entity.CtrContractDelivery;
import com.spt.bas.client.vo.CtrContractDeliveryVo;
import com.spt.tools.jpa.dao.BaseDao;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.List;

public interface CtrContractDeliveryDao  extends BaseDao<CtrContractDelivery> {


    @Query("from CtrContractDelivery c where  c.waybillCode=?1 ")
    CtrContractDelivery findByDeliveryId(String waybillCode);
    
    @Query("from CtrContractDelivery c where  c.contractId=?1 ")
    CtrContractDelivery findByContractId(Long contractId);

    @Transactional
    @Modifying
    @Query("update CtrContractDelivery a set a.driverName = ?2,a.driverPhone= ?3,a.plateNumber = ?4 where a.waybillCode = ?1")
    void deliveryNoteUpdate(String waybillCode, String driverName,String  driverPhone,  String plateNumber);


}
