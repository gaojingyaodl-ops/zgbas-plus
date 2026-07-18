package com.spt.bas.report.server.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.spt.bas.report.client.entity.RptCtrContractSellOnCreditReport;
import com.spt.bas.report.client.vo.RptSellOnCreditSearchVo;
import com.spt.bas.report.server.service.IRptCtrContractSellOnCreditService;

@RestController
@RequestMapping(value = "/ctr/contractSellOnCredit")
public class RptCtrContractSellOnCreditlApi {
	@Autowired
	private IRptCtrContractSellOnCreditService ctrContractSellOnCreditService;
	
	@PostMapping("findPageSellOnCredit")
	public Page<RptCtrContractSellOnCreditReport> findPageSellOnCredit(@RequestBody RptSellOnCreditSearchVo vo){
		Page<RptCtrContractSellOnCreditReport> findSellOnCredit = ctrContractSellOnCreditService.findPageSellOnCredit(vo);
		return findSellOnCredit;
	}

}
