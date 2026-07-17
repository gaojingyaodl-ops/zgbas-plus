package com.spt.bas.server.service;

import com.spt.bas.client.entity.ApplyServiceReceive;
import com.spt.tools.jpa.service.IBaseService;

import java.util.List;

public interface IApplyServiceReceiveService extends IBaseService<ApplyServiceReceive> {
    void updateFileId(Long id, String fileId);

    List<ApplyServiceReceive> findByContractId(Long contractId);

    List<ApplyServiceReceive> findByServiceContractId(Long serviceContractId);

    void updateApplyStatus(Long serviceContractId);
}

