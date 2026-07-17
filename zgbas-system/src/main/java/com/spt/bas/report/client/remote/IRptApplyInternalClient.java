package com.spt.bas.report.client.remote;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.spt.bas.report.client.constant.ReportConstant;
import com.spt.bas.report.client.entity.RptApplyInternalReport;
import com.spt.tools.data.vo.PageDown;
import com.spt.tools.http.feign.FeignConfig;
@FeignClient(name = ReportConstant.SERVER_NAME,path= ReportConstant.SERVER_NAME+"/rpt/internal",url=ReportConstant.SERVER_URL,configuration=FeignConfig.class)

public interface IRptApplyInternalClient {
	@PostMapping("findPageInternalBuy")
	public PageDown<RptApplyInternalReport> findPageInternalBuy(@RequestBody RptApplyInternalReport vo);
}
