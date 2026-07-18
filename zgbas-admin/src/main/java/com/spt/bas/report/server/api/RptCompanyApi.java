package com.spt.bas.report.server.api;

import com.spt.bas.report.client.entity.RptCompany;
import com.spt.bas.report.client.vo.RptCompanySearchVo;
import com.spt.bas.report.server.service.IRptCompanyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/rpt/company")
public class RptCompanyApi {
	@Autowired
	private IRptCompanyService rptCompanyService;
	
	@PostMapping("findCompanyList")
	public List<RptCompany> findCompanyList(@RequestBody RptCompanySearchVo vo){
		return rptCompanyService.findRptCompanyList(vo);
		
	}
	@PostMapping("findRptCompanyPage")
	public Page<RptCompany> findRptCompanyPage(@RequestBody RptCompanySearchVo searchVo){
		return rptCompanyService.findRptCompanyPage(searchVo);
	}

	@PostMapping("selectAllRptCompany")
	public List<RptCompany> selectAllRptCompany(){
		return rptCompanyService.selectAllRptCompany();
	}
	
	
	
}
