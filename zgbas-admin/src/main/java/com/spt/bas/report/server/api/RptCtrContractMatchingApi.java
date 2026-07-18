package com.spt.bas.report.server.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.spt.bas.report.client.entity.RptCtrContractMatchingReport;
import com.spt.bas.report.client.vo.RptAssementSearchVo;
import com.spt.bas.report.server.service.IRptCtrContractMatchingService;

@RestController
@RequestMapping(value = "/rpt/match")
public class RptCtrContractMatchingApi {
	@Autowired
	private IRptCtrContractMatchingService ctrContractMatchingService;
	
	@PostMapping("findPageMatching")
	public Page<RptCtrContractMatchingReport> findPageMatching(@RequestBody RptAssementSearchVo vo){
		Page<RptCtrContractMatchingReport> findPageMatching = ctrContractMatchingService.findPageMatching(vo);
		return findPageMatching;
	}
	@PostMapping("findPageTotal")
	public RptCtrContractMatchingReport findPageTotal(@RequestBody RptAssementSearchVo vo){
		return ctrContractMatchingService.findPageTotal(vo);
	}
}
