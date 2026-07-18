package com.spt.bas.report.server.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.spt.bas.report.client.entity.RptApplyInternalReport;
import com.spt.bas.report.server.service.IRptApplyInternalService;

@RestController
@RequestMapping(value = "/rpt/internal")
public class RptApplyInternalApi {
	@Autowired
	private IRptApplyInternalService applyInternalService;
	
	@PostMapping("findPageInternalBuy")
	public Page<RptApplyInternalReport> findPageInternalBuy(@RequestBody RptApplyInternalReport vo){
		Page<RptApplyInternalReport> findPageInternalBuy = applyInternalService.findPageInternalBuy(vo);
		return findPageInternalBuy;
		
	}
}
