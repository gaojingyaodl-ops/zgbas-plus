package com.spt.bas.report.server.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.spt.bas.report.client.entity.RptBusinessAccountReport;
import com.spt.bas.report.client.vo.RptBusinessSearchVo;
import com.spt.bas.report.server.service.IRptBusinessAccountService;

@RestController
@RequestMapping(value = "/rpt/business")
public class RptBusinessAccountApi {
	@Autowired
	private IRptBusinessAccountService businessAccountService;
	@PostMapping("findPage")
	public Page<RptBusinessAccountReport> findPage(@RequestBody RptBusinessSearchVo vo){
		return businessAccountService.findPage(vo);
	}
	
	@PostMapping("finePageSum")
	public RptBusinessAccountReport findPageSum(@RequestBody RptBusinessSearchVo vo) {
		return businessAccountService.findPageSum(vo);
	}
}
