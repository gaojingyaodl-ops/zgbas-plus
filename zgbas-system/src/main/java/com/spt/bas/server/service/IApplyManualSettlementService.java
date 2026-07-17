package com.spt.bas.server.service;

import com.spt.bas.client.entity.ApplyManualSettlement;
import com.spt.tools.jpa.service.IBaseService;

public interface IApplyManualSettlementService extends IBaseService<ApplyManualSettlement> {

    void updateFileId(Long id, String fileId);

}
