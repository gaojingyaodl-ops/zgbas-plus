package com.spt.bas.report.client.remote;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.spt.bas.report.client.constant.ReportConstant;
import com.spt.bas.report.client.entity.RptCtrContractPayAndReceiveReport;
import com.spt.bas.report.client.vo.RptPayAndReceiveSearchVo;
import com.spt.tools.data.vo.PageDown;
import com.spt.tools.http.feign.FeignConfig;

@FeignClient(name = ReportConstant.SERVER_NAME,path= ReportConstant.SERVER_NAME+"/rpt/payAndReceive",url=ReportConstant.SERVER_URL,configuration=FeignConfig.class)
public interface IRptCtrContractPayAndReceiveClient {

	@PostMapping("findPagePay")
	public PageDown<RptCtrContractPayAndReceiveReport> findPagePay(@RequestBody RptPayAndReceiveSearchVo vo);
	
	@PostMapping("findPayTotalPage")
	public RptCtrContractPayAndReceiveReport findPayTotalPage(@RequestBody RptPayAndReceiveSearchVo vo);
	
	@PostMapping("findPageReceive")
	public PageDown<RptCtrContractPayAndReceiveReport> findPageReceive(@RequestBody RptPayAndReceiveSearchVo vo);

	@PostMapping("findPageReceiveSum")
	public RptCtrContractPayAndReceiveReport findPageReceiveSum(@RequestBody RptPayAndReceiveSearchVo searchVo);
}
