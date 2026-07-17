package com.spt.bas.server.service;

import com.spt.bas.client.entity.BasContract;
import com.spt.bas.client.entity.BasContractText;
import com.spt.tools.jpa.service.IBaseService;

public interface IBasContractTextService extends IBaseService<BasContractText> {
	
	public BasContractText saveContract(BasContract bc);
	
	public BasContractText getContractTextById(Long contractTextId);
}

