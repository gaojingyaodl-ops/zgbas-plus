package com.spt.bas.server.service;

import com.spt.bas.client.entity.ApplyContractAdjust;
import com.spt.tools.jpa.service.IBaseService;

public interface IApplyContractAdjustService extends IBaseService<ApplyContractAdjust> {
	void updateSellFileId(Long id, String fileId);
	void updateBuyFileId(Long id, String fileId);
	void updateFileId(Long id, String fileId);
}

