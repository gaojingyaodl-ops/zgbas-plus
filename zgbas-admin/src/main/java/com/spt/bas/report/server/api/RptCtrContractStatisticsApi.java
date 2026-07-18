package com.spt.bas.report.server.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.spt.bas.report.client.entity.RptCtrContractStatistics;
import com.spt.bas.report.client.vo.RptStatisticsVo;
import com.spt.bas.report.server.service.IRptCtrContractStatisticsService;
import com.spt.tools.core.bean.PageSearchVo;

@RestController
@RequestMapping(value = "/ctr/statistics")
public class RptCtrContractStatisticsApi {
	@Autowired
	private IRptCtrContractStatisticsService ctrContractStatisticsService;
	
	@PostMapping("findBuyCtrContract")
	public Page<RptCtrContractStatistics> findBuyCtrContract(@RequestBody RptStatisticsVo vo){
		return ctrContractStatisticsService.findBuyCtrContract(vo);
	}
	
	@PostMapping("getContractStatistics")
	public RptCtrContractStatistics getContractStatistics(@RequestBody RptStatisticsVo vo){
		return ctrContractStatisticsService.getContractStatistics(vo);
	}
	
	@PostMapping("findSaleCtrContract")
	public Page<RptCtrContractStatistics> findSaleCtrContract(@RequestBody PageSearchVo searchVo){
		return ctrContractStatisticsService.findSaleCtrContract(searchVo);
	}
	
	@PostMapping("showStatistics")
	public Page<RptCtrContractStatistics> showStatistics(@RequestBody RptStatisticsVo vo){
		return ctrContractStatisticsService.BuyOrSellStatistics(vo);
	}
}

