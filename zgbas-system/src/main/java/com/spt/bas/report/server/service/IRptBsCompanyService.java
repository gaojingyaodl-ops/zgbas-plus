package com.spt.bas.report.server.service;

import com.spt.bas.report.client.entity.RptCompanyCreditInfo0;
import com.spt.bas.report.client.vo.RptOpenCompanyCreditQueryVo;
import com.spt.bas.report.client.vo.RptOpenCompanyCreditVo;
import com.spt.bas.report.client.vo.RptPartBsCompanyVo;

import java.util.List;

public interface IRptBsCompanyService {
	
	List<RptPartBsCompanyVo> findCompanyList(RptPartBsCompanyVo vo);
	
	RptPartBsCompanyVo findCompanyById(RptPartBsCompanyVo vo);
	
	List<RptPartBsCompanyVo> findCompany(RptPartBsCompanyVo vo);

	int countCompanyByName(String companyName);

	List<Long> getRelationShipApproveIdByCompanyId(Long matchUserId);

	List<Long> getRelationShipApproveIdByCompanyIds(List<Long> matchUserIds);

	List<RptCompanyCreditInfo0> getCompanyCreditInfo0();

	List<RptOpenCompanyCreditVo> findOpenCreditList(RptOpenCompanyCreditQueryVo vo);
}
