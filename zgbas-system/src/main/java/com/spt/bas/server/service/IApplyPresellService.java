package com.spt.bas.server.service;

import com.spt.bas.client.entity.ApplyPresell;
import com.spt.tools.jpa.service.IBaseService;

public interface IApplyPresellService extends IBaseService<ApplyPresell> {
	
	public void updateApplyStatus(Long contractId);
	
	public ApplyPresell findByContractId(Long contractId);
	
	void updateFileId(Long id, String fileId);
}

