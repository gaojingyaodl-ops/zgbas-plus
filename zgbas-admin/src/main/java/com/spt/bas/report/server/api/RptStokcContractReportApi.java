package com.spt.bas.report.server.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.spt.bas.report.client.entity.RptStockContractReportVo;
import com.spt.bas.report.client.vo.RptStockContractSearchReportVo;
import com.spt.bas.report.server.service.IRptStokcContractReportService;

@RestController
@RequestMapping(value = "/rpt/stockContract")
public class RptStokcContractReportApi {
	@Autowired
	private IRptStokcContractReportService stockContractReportService;
	
	@PostMapping("findStockContractPage")
	public Page<RptStockContractReportVo> findStockContractPage(@RequestBody RptStockContractSearchReportVo vo){
		return stockContractReportService.findStockContractPage(vo);
	}
	
	@PostMapping("findPage")
	public Page<RptStockContractReportVo> findPage(@RequestBody RptStockContractSearchReportVo vo){
		return stockContractReportService.findPage(vo);
	}
	
}
