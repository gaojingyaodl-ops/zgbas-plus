package com.spt.bas.server.service;

import com.spt.bas.client.entity.ApplyMatters;
import com.spt.tools.jpa.service.IBaseService;

public interface IApplyMattersService extends IBaseService<ApplyMatters> {
    void updateFileId(Long id, String fileId);
}
