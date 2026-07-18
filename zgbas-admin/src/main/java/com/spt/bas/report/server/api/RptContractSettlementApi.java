package com.spt.bas.report.server.api;

import com.spt.bas.report.client.entity.RptContractSettlementVo;
import com.spt.bas.report.client.vo.RptContractSettlementSearchVo;
import com.spt.bas.report.server.service.IRptContractSettlementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(value = "/rpt/contract/settlement")
public class RptContractSettlementApi {
	@Autowired
	private IRptContractSettlementService contractSettlementService;

	@PostMapping("findRptContractSettlementPage")
	public Page<RptContractSettlementVo> findRptContractSettlementPage(@RequestBody RptContractSettlementSearchVo searchVo){
		return contractSettlementService.findRptContractSettlementPage(searchVo);
	}

	@PostMapping("findRptContractSettlementSum")
	public RptContractSettlementVo findRptContractSettlementSum(@RequestBody RptContractSettlementSearchVo searchVo){
		return contractSettlementService.findRptContractSettlementSum(searchVo);
	}

}
