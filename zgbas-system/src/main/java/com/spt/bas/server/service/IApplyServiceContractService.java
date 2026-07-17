package com.spt.bas.server.service;

import com.spt.bas.client.entity.ApplyServiceContract;
import com.spt.tools.jpa.service.IBaseService;

public interface IApplyServiceContractService extends IBaseService<ApplyServiceContract> {

	void updateFileId(Long id, String fileId);
	
	ApplyServiceContract findByContractId(Long contractId);
}

