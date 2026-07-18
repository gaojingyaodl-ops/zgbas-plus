package com.spt.bas.report.server.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.spt.bas.report.client.entity.RptCtrContractAgencyReport;
import com.spt.bas.report.client.vo.RptAssementSearchVo;
import com.spt.bas.report.server.service.IRptCtrContractAgencyService;

@RestController
@RequestMapping(value = "/rpt/ageny")
public class RptCtrContractAgencyApi {
	@Autowired
	private IRptCtrContractAgencyService ctrContractAgencyService;
	
	@PostMapping("findPageAgency")
	public Page<RptCtrContractAgencyReport> findPageAgency(@RequestBody RptAssementSearchVo vo){
		Page<RptCtrContractAgencyReport> findPageAgency = ctrContractAgencyService.findPageAgency(vo);
		return findPageAgency;
	}
	
	@PostMapping("findPageTotal")
	public RptCtrContractAgencyReport findPageTotal(@RequestBody RptAssementSearchVo vo) {
		return ctrContractAgencyService.findPageAgencyTotal(vo);
	}
	
	@PostMapping("findAgencyBySellId")
	public List<RptCtrContractAgencyReport> findAgencyBySellId(@RequestBody RptAssementSearchVo vo){
		return ctrContractAgencyService.findAgencyBySellId(vo);
	}
	
	@PostMapping("findSecondCalculatePage")
	public Page<RptCtrContractAgencyReport> findSecondCalculatePage(@RequestBody RptAssementSearchVo vo){
		return ctrContractAgencyService.findSecondCalculatePage(vo);
	}
}
