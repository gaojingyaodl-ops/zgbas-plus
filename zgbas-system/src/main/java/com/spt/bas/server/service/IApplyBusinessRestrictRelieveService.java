package com.spt.bas.server.service;

import com.spt.bas.client.entity.ApplyBusinessRestrictRelieve;
import com.spt.tools.jpa.service.IBaseService;

public interface IApplyBusinessRestrictRelieveService extends IBaseService<ApplyBusinessRestrictRelieve> {
    void updateFileId(Long id, String fileId);
}
