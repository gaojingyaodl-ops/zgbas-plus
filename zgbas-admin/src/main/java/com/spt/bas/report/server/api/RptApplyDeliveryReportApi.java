package com.spt.bas.report.server.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.spt.bas.report.client.entity.RptApplyDeliveryReport;
import com.spt.bas.report.client.vo.RptApplyDeliverySearchVo;
import com.spt.bas.report.server.service.IRptApplyDeliveryReportService;

@RestController
@RequestMapping(value = "/rpt/delivery")
public class RptApplyDeliveryReportApi {
	@Autowired
	private IRptApplyDeliveryReportService applyDeliveryReportService;
	
	@PostMapping("findApplyDeliveryPage")
	public Page<RptApplyDeliveryReport> findApplyDeliveryPage(@RequestBody RptApplyDeliverySearchVo searchVo){
		return applyDeliveryReportService.findApplyDeliveryPage(searchVo);
	}
}
