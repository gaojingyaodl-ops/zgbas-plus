package com.spt.bas.server.dao;

import com.spt.bas.client.entity.PiccShipmentApply;
import com.spt.tools.jpa.dao.BaseDao;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 *人保赊销申请回调报文
 */
public interface PiccShipmentApplyDao extends BaseDao<PiccShipmentApply> {

    PiccShipmentApply findByContractNo(String contractNo);

    @Query("from PiccShipmentApply p where p.state = ?1 and p.approveFlag = ?2")
    List<PiccShipmentApply> findByStateAndApproveFlag(String state, String approveFlag);

    @Query("from PiccShipmentApply p where p.state = ?1 and p.approveFlag = ?2 and p.insuranceComputeStatus = ?3")
    List<PiccShipmentApply> findByStateAndApproveFlagAndComputeStatus(String state, String approveFlag, String insuranceComputeStatus);

}
