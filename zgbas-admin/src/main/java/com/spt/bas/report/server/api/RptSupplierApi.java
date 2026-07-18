package com.spt.bas.report.server.api;

import com.spt.bas.report.client.entity.RptCompany;
import com.spt.bas.report.client.entity.RptSupplier;
import com.spt.bas.report.client.vo.RptCompanySearchVo;
import com.spt.bas.report.server.service.IRptCompanyService;
import com.spt.bas.report.server.service.IRptSupplierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/rpt/supplier")
public class RptSupplierApi {
	@Autowired
	private IRptSupplierService rptSupplierService;
	
	@PostMapping("findSupplierList")
	public List<RptSupplier> findSupplierList(@RequestBody RptCompanySearchVo vo){
		return rptSupplierService.findRptSupplierList(vo);
		
	}
	@PostMapping("findRptSupplierPage")
	public Page<RptSupplier> findRptSupplierPage(@RequestBody RptCompanySearchVo searchVo){
		return rptSupplierService.findRptSupplierPage(searchVo);
	}

	@PostMapping("selectAllRptSupplier")
	public List<RptSupplier> selectAllRptSupplier(){
		return rptSupplierService.selectAllRptSupplier();
	}
}
