package com.spt.bas.report.server.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.spt.bas.report.client.entity.RptCtrContractPayAndReceiveReport;
import com.spt.bas.report.client.vo.RptPayAndReceiveSearchVo;
import com.spt.bas.report.server.service.IRptCtrContractPayAndReceiveService;

@RestController
@RequestMapping(value = "/rpt/payAndReceive")
public class RptCtrContractPayAndReceiveApi {
	@Autowired
	private IRptCtrContractPayAndReceiveService ctrContractPayAndReceiveService;
	
	@PostMapping("findPagePay")
	public Page<RptCtrContractPayAndReceiveReport> findPagePay(@RequestBody RptPayAndReceiveSearchVo vo){
		Page<RptCtrContractPayAndReceiveReport> findPagePay = ctrContractPayAndReceiveService.findPagePay(vo);
		return findPagePay;
	}
	
	@PostMapping("findPayTotalPage")
	public RptCtrContractPayAndReceiveReport findPayTotalPage(@RequestBody RptPayAndReceiveSearchVo vo) {
		return ctrContractPayAndReceiveService.findPayTotalPage(vo);
	}
	
	@PostMapping("findPageReceive")
	public Page<RptCtrContractPayAndReceiveReport> findPageReceive(@RequestBody RptPayAndReceiveSearchVo vo){
		Page<RptCtrContractPayAndReceiveReport> findPageReceive = ctrContractPayAndReceiveService.findPageReceive(vo);
		return findPageReceive;
	}
	
	@PostMapping("findPageReceiveSum")
	public RptCtrContractPayAndReceiveReport findPageReceiveSum(@RequestBody RptPayAndReceiveSearchVo vo){
		RptCtrContractPayAndReceiveReport total = ctrContractPayAndReceiveService.findPageReceiveDetailSum(vo);
		return total;
		
	}
}
