package com.spt.bas.report.client.remote;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.spt.bas.report.client.constant.ReportConstant;
import com.spt.bas.report.client.entity.RptCtrContractAsseMentReport;
import com.spt.bas.report.client.vo.RptAssementSearchVo;
import com.spt.tools.data.vo.PageDown;
import com.spt.tools.http.feign.FeignConfig;

@FeignClient(name = ReportConstant.SERVER_NAME,path= ReportConstant.SERVER_NAME+"/rpt/assement",url=ReportConstant.SERVER_URL,configuration=FeignConfig.class)
public interface IRptCtrContractAsseMentClient {
	
	@PostMapping("findPageAssessment")
	public PageDown<RptCtrContractAsseMentReport> findPageAssessment(@RequestBody RptAssementSearchVo vo);
	
	@PostMapping("findPageTotal")
	public RptCtrContractAsseMentReport findPageTotal(@RequestBody RptAssementSearchVo vo);
}
