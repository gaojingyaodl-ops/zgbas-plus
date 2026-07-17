package com.spt.bas.report.client.remote;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.spt.bas.report.client.constant.ReportConstant;
import com.spt.bas.report.client.entity.RptBusinessAccountReport;
import com.spt.bas.report.client.vo.RptBusinessSearchVo;
import com.spt.tools.data.vo.PageDown;
import com.spt.tools.http.feign.FeignConfig;

@FeignClient(name = ReportConstant.SERVER_NAME,path= ReportConstant.SERVER_NAME+"/rpt/business",url=ReportConstant.SERVER_URL,configuration=FeignConfig.class)
public interface IRptBusinessAccountClient {
	
	@PostMapping("findPage")
	public PageDown<RptBusinessAccountReport> findPage(@RequestBody RptBusinessSearchVo vo);
	
	@PostMapping("finePageSum")
	public RptBusinessAccountReport findPageSum(@RequestBody RptBusinessSearchVo vo);
}
