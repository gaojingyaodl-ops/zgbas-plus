package com.spt.bas.report.server.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.spt.bas.report.client.entity.RptCtrContractAsseMentReport;
import com.spt.bas.report.client.vo.RptAssementSearchVo;
import com.spt.bas.report.server.service.IRptCtrContractAsseMentService;

@RestController
@RequestMapping(value = "/rpt/assement")
public class RptCtrContractAsseMentApi {
	@Autowired
	private IRptCtrContractAsseMentService ctrContractAsseMentService;
	
	@PostMapping("findPageAssessment")
	public Page<RptCtrContractAsseMentReport> findPageAssessment(@RequestBody RptAssementSearchVo vo){
		Page<RptCtrContractAsseMentReport> findPageAssessment = ctrContractAsseMentService.findPageAssessment(vo);
		return findPageAssessment;
	}
	@PostMapping("findPageTotal")
	public RptCtrContractAsseMentReport findPageTotal(@RequestBody RptAssementSearchVo vo){
		return ctrContractAsseMentService.findPageTotal(vo);
	}
}
