package com.spt.bas.report.client.remote;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.spt.bas.report.client.constant.ReportConstant;
import com.spt.bas.report.client.entity.RptCtrContractOrverdur;
import com.spt.tools.data.vo.PageDown;
import com.spt.tools.http.feign.FeignConfig;

@FeignClient(name = ReportConstant.SERVER_NAME,path= ReportConstant.SERVER_NAME+"/rpt/orver",url=ReportConstant.SERVER_URL,configuration=FeignConfig.class)
public interface IRptCtrContractOrverdurClient {
	
	@PostMapping("findPageOrverdur")
	public PageDown<RptCtrContractOrverdur> findPageOrverdur(@RequestBody RptCtrContractOrverdur vo);
	@PostMapping("findAllOrverdur")
	public List<RptCtrContractOrverdur> findAllOrverdur(@RequestBody RptCtrContractOrverdur vo);
	@PostMapping("findPageTotal")
	public RptCtrContractOrverdur findPageTotal(@RequestBody RptCtrContractOrverdur vo);
}
