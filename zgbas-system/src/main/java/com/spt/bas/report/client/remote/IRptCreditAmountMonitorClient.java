package com.spt.bas.report.client.remote;

import com.spt.bas.report.client.constant.ReportConstant;
import com.spt.bas.report.client.entity.RptCreditAmountMonitor;
import com.spt.bas.report.client.vo.RptCreditAmountMonitorSearchVo;
import com.spt.tools.data.vo.PageDown;
import com.spt.tools.http.feign.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@FeignClient(name = ReportConstant.SERVER_NAME,path= ReportConstant.SERVER_NAME+"/credit/amount/monitor",url=ReportConstant.SERVER_URL,configuration=FeignConfig.class)
public interface IRptCreditAmountMonitorClient {
	
	@PostMapping("findCreditAmountMonitorPage")
	PageDown<RptCreditAmountMonitor> findCreditAmountMonitorPage(@RequestBody RptCreditAmountMonitorSearchVo searchVo);
	
}
