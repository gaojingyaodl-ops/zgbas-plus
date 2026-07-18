package com.spt.bas.report.server.api;

import com.spt.bas.report.client.entity.RptBusinessOverview;
import com.spt.bas.report.client.vo.RptBusinessOverviewSearchVo;
import com.spt.bas.report.server.service.IRptBusinessOverviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/business/overview/api")
public class RptBusinessOverviewApi {
	@Autowired
	private IRptBusinessOverviewService businessOverviewService;

	@PostMapping("findBusinessOverviewList")
	public List<RptBusinessOverview> findBusinessOverviewList(@RequestBody RptBusinessOverviewSearchVo searchVo) {
		return businessOverviewService.findBusinessOverviewList(searchVo);
	}
	
	
}
