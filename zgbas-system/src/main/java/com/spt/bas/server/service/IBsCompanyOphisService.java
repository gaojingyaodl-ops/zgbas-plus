package com.spt.bas.server.service;

import com.spt.bas.client.entity.BsCompanyOphis;
import com.spt.bas.client.vo.BsCompanyOphisVo;
import com.spt.bas.client.vo.CompanyStatusVo;
import com.spt.tools.jpa.service.IBaseService;

public interface IBsCompanyOphisService extends IBaseService<BsCompanyOphis> {

	Boolean haveFllowByUser(CompanyStatusVo companyVo);
	
	void addCompanyHis(BsCompanyOphisVo opHis);
	
}

