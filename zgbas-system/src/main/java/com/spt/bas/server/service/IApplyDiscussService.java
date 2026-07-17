package com.spt.bas.server.service;

import com.spt.bas.client.entity.ApplyDiscuss;
import com.spt.tools.jpa.service.IBaseService;

public interface IApplyDiscussService extends IBaseService<ApplyDiscuss> {
    void updateFileId(Long id, String fileId);
}

