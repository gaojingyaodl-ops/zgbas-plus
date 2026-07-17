package com.spt.bas.server.service;

import com.spt.bas.client.entity.ApplyInternalBuy;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.jpa.service.IBaseService;

public interface IApplyInternalBuyService extends IBaseService<ApplyInternalBuy> {
	
	public void doBackInternalBuy() throws ApplicationException;
	
	void updateApplyStatus(Long id);
	
	ApplyInternalBuy findByContractId(Long contractId);
}

