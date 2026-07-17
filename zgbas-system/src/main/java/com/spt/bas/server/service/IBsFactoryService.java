package com.spt.bas.server.service;

import java.util.List;

import com.spt.bas.client.entity.BsFactory;
import com.spt.tools.jpa.service.IBaseService;

public interface IBsFactoryService extends IBaseService<BsFactory> {
	
	List<BsFactory>findByEnterpriseId(Long enterpriseId);

	Long countFactory(BsFactory factory);
	
	List<BsFactory> findByFactoryNameAndEnterpriseId(BsFactory factory);
}

