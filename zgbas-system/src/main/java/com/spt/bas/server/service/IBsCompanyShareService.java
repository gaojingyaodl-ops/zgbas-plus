package com.spt.bas.server.service;

import com.spt.bas.client.entity.BsCompanyShare;
import com.spt.tools.jpa.service.IBaseService;

import java.util.List;

public interface IBsCompanyShareService extends IBaseService<BsCompanyShare> {

	BsCompanyShare findByCompanyIdAndSharedUserId(Long companyId, Long userId);

	List<BsCompanyShare> findByCompanyIdAndCreateUserId(Long companyId, Long createUserId);
	
	List<BsCompanyShare> findBySharedUserId(Long shareUserId);
	
	List<BsCompanyShare> findByCompanyId(Long companyId);

	void deleteCompanyShare(Long companyId);
}

