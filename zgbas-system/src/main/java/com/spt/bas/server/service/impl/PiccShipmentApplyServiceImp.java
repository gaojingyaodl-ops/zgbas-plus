package com.spt.bas.server.service.impl;

import com.spt.bas.client.entity.PiccPushData;
import com.spt.bas.client.entity.PiccShipmentApply;
import com.spt.bas.server.dao.PiccShipmentApplyDao;
import com.spt.bas.server.service.IPiccShipmentApplyService;
import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.service.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class PiccShipmentApplyServiceImp extends BaseService<PiccShipmentApply> implements IPiccShipmentApplyService {
    @Autowired
    private PiccShipmentApplyDao piccShipmentApplyDao;

    @Override
    public PiccShipmentApply findByContractNo(String contractNo) {
        return piccShipmentApplyDao.findByContractNo(contractNo);
    }

    @Override
    public List<PiccShipmentApply> findByStateAndApproveFlag(String state, String approveFlag) {
        return piccShipmentApplyDao.findByStateAndApproveFlag(state, approveFlag);
    }

    @Override
    public List<PiccShipmentApply> findByStateAndApproveFlagAndComputeStatus(String state, String approveFlag, String insuranceComputeStatus) {
        return piccShipmentApplyDao.findByStateAndApproveFlagAndComputeStatus(state, approveFlag, insuranceComputeStatus);
    }

    @Override
    public BaseDao<PiccShipmentApply> getBaseDao() {
        return piccShipmentApplyDao;
    }
}
