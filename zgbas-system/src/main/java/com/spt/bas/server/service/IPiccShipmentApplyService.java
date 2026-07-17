package com.spt.bas.server.service;

import com.spt.bas.client.entity.PiccShipmentApply;
import com.spt.tools.jpa.service.IBaseService;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface IPiccShipmentApplyService extends IBaseService<PiccShipmentApply> {

    PiccShipmentApply findByContractNo(String contractNo);

    List<PiccShipmentApply> findByStateAndApproveFlag(String state, String approveFlag);

    List<PiccShipmentApply> findByStateAndApproveFlagAndComputeStatus(String state, String approveFlag, String insuranceComputeStatus);

}
