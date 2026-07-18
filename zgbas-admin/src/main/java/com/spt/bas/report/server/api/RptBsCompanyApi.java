package com.spt.bas.report.server.api;

import com.spt.bas.report.client.entity.RptCompanyCreditInfo0;
import com.spt.bas.report.client.vo.RptOpenCompanyCreditQueryVo;
import com.spt.bas.report.client.vo.RptOpenCompanyCreditVo;
import com.spt.bas.report.client.vo.RptPartBsCompanyVo;
import com.spt.bas.report.server.service.IRptBsCompanyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/bs/company")
public class RptBsCompanyApi {
	@Autowired
	private IRptBsCompanyService bsCompanyService;
	
	@PostMapping("findCompanyList")
	public List<RptPartBsCompanyVo> findCompanyList(@RequestBody RptPartBsCompanyVo vo){
		return bsCompanyService.findCompanyList(vo);
		
	}
	
	@PostMapping("findCompanyById")
	public RptPartBsCompanyVo findCompanyById(@RequestBody RptPartBsCompanyVo vo){
		return bsCompanyService.findCompanyById(vo);
		
	}
	
	@PostMapping(value = "findCompany")
	public List<RptPartBsCompanyVo> findCompany(@RequestBody RptPartBsCompanyVo vo){
		return bsCompanyService.findCompany(vo);
	}

	@PostMapping(value = "countCompanyByName")
	public int countCompanyByName(@RequestBody String companyName){
		return bsCompanyService.countCompanyByName(companyName);
	}

	@PostMapping(value = "getRelationShipApproveIdByCompanyId")
	public List<Long> getRelationShipApproveIdByCompanyId(@RequestBody Long matchUserId){
		return bsCompanyService.getRelationShipApproveIdByCompanyId(matchUserId);
	}

	@PostMapping(value = "getRelationShipApproveIdByCompanyIds")
	public List<Long> getRelationShipApproveIdByCompanyIds(@RequestBody List<Long> matchUserIds){
		return bsCompanyService.getRelationShipApproveIdByCompanyIds(matchUserIds);
	}
	@PostMapping(value = "getCompanyCreditInfo0")
	public List<RptCompanyCreditInfo0> getCompanyCreditInfo0(){
		return bsCompanyService.getCompanyCreditInfo0();
	}

	@PostMapping("findOpenCreditList")
	public List<RptOpenCompanyCreditVo> findOpenCreditList(@RequestBody RptOpenCompanyCreditQueryVo searchVo){
		return bsCompanyService.findOpenCreditList(searchVo);
	}
}
