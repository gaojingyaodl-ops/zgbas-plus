package com.spt.bas.report.server.service;

import com.spt.bas.report.client.entity.RptCompany;
import com.spt.bas.report.client.vo.RptCompanySearchVo;
import org.springframework.data.domain.Page;

import java.util.List;

public interface IRptCompanyService {
	
	List<RptCompany> findRptCompanyList(RptCompanySearchVo vo);

	Page<RptCompany> findRptCompanyPage(RptCompanySearchVo searchVo);

	List<RptCompany> selectAllRptCompany();

}
