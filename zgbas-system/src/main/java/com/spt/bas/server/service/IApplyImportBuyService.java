package com.spt.bas.server.service;


import com.spt.bas.client.entity.ApplyImportBuy;
import com.spt.tools.jpa.service.IBaseService;

public interface IApplyImportBuyService extends IBaseService<ApplyImportBuy> {

	void updateFileId(Long id, String fileId);
	
	ApplyImportBuy findByContractId(Long contractId);
	
	void updateApplyStatusC(Long id);
}

