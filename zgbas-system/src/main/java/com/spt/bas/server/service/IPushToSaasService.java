package com.spt.bas.server.service;

import com.spt.bas.client.entity.PushToSaas;
import com.spt.tools.jpa.service.IBaseService;

public interface IPushToSaasService extends IBaseService<PushToSaas> {
	
	void pushDataToSaas();
}

