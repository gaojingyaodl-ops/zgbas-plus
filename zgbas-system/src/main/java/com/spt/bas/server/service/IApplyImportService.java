package com.spt.bas.server.service;

import com.spt.bas.client.entity.ApplyImport;
import com.spt.tools.jpa.service.IBaseService;

public interface IApplyImportService extends IBaseService<ApplyImport> {
	void updateFileId(Long id, String fileId);	
}

