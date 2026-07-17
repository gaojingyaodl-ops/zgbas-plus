package com.spt.bas.server.service;

import com.spt.bas.client.entity.ApplyLoss;
import com.spt.tools.jpa.service.IBaseService;

public interface IApplyLossService extends IBaseService<ApplyLoss> {
    void updateFileId(Long id, String fileId);
}

