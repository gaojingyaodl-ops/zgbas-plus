package com.spt.bas.server.service;

import com.spt.bas.client.entity.BsProductTypeAccess;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.jpa.service.IBaseService;

import java.util.List;

public interface IBsProductTypeAccessService extends IBaseService<BsProductTypeAccess> {

	void saveAccess(Long enterpriseId, String productCds) throws ApplicationException;

	List<BsProductTypeAccess> findByEnterpriseId(Long enterpriseId);
	
	void countByProductCdAndEnterpriseId(BsProductTypeAccess vo);

	void reFreshCache();
}

