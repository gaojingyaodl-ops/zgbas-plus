package com.spt.bas.server.service;

import com.spt.bas.client.entity.BasContract;
import com.spt.bas.client.entity.BasDelivery;
import com.spt.tools.jpa.service.IBaseService;

public interface IBasDeliveryService extends IBaseService<BasDelivery> {

	BasDelivery newEntity(BasContract contract);

	void updateFileId(Long id, String fileId);
	
}

