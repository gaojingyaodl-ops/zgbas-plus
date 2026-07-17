package com.spt.bas.server.service;

import com.spt.bas.client.entity.BsCompanyDcsx;
import com.spt.tools.jpa.service.IBaseService;

import java.util.List;
import java.util.Map;


public interface IBsCompanyDcsxService extends IBaseService<BsCompanyDcsx> {

	BsCompanyDcsx findByCompanyName(String companyName);

	Map<String, BsCompanyDcsx> getCompanyConfigMap();

	List<BsCompanyDcsx> findDcsxCompanyList();

	BsCompanyDcsx findByCompanyCd(String companyCd);

	Boolean verifySignCompany(String companyName);
}
