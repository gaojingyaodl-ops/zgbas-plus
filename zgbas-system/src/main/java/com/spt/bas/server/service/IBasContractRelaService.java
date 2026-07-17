package com.spt.bas.server.service;

import com.spt.bas.client.entity.BasContractRela;
import com.spt.tools.jpa.service.IBaseService;

public interface IBasContractRelaService extends IBaseService<BasContractRela> {

	void updateFileId(Long id, String fileId);
	
	String findSaleContractIdByBuyId(String buyContractId);
}

