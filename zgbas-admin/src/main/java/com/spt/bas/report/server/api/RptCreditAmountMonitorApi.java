package com.spt.bas.report.server.api;

import com.spt.bas.report.client.entity.RptCreditAmountMonitor;
import com.spt.bas.report.client.vo.RptCreditAmountMonitorSearchVo;
import com.spt.bas.report.server.service.IRptCreditAmountMonitorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(value = "/credit/amount/monitor")
public class RptCreditAmountMonitorApi {
	@Autowired
	private IRptCreditAmountMonitorService creditAmountMonitorService;

	@PostMapping("findCreditAmountMonitorPage")
	public Page<RptCreditAmountMonitor> findCreditAmountMonitorPage(@RequestBody RptCreditAmountMonitorSearchVo searchVo){
		return creditAmountMonitorService.findCreditAmountMonitorPage(searchVo);
	}

}
