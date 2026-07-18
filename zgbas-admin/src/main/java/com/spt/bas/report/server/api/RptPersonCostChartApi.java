package com.spt.bas.report.server.api;

import com.spt.bas.report.client.entity.RptPersonCostChart;
import com.spt.bas.report.client.vo.RptPersonCostChartSearchVo;
import com.spt.bas.report.server.service.IRptPersonCostChartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/person/cost/chart")
public class RptPersonCostChartApi {
	@Autowired
	private IRptPersonCostChartService personCostChartService;

	@PostMapping("personCostChartDataList")
	public List<RptPersonCostChart> personCostChartDataList(@RequestBody RptPersonCostChartSearchVo searchVo){
		return personCostChartService.personCostChartDataList(searchVo);
	}
	


}
