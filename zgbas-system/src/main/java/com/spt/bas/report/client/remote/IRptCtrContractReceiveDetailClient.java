package com.spt.bas.report.client.remote;

import com.spt.bas.report.client.vo.RptContractDateSearchVo;
import com.spt.bas.report.client.vo.RptContractDateVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.spt.bas.report.client.constant.ReportConstant;
import com.spt.bas.report.client.entity.RptCtrContractReceiveDetailReport;
import com.spt.bas.report.client.vo.RptReceiveDetailSearchVo;
import com.spt.tools.data.vo.PageDown;
import com.spt.tools.http.feign.FeignConfig;

import java.util.List;

@FeignClient(name = ReportConstant.SERVER_NAME,path= ReportConstant.SERVER_NAME+"/rpt/ReceiveDetail",url=ReportConstant.SERVER_URL,configuration=FeignConfig.class)
public interface IRptCtrContractReceiveDetailClient {

	@PostMapping("findPageReceiveDetail")
	public PageDown<RptCtrContractReceiveDetailReport> findPageReceiveDetail(@RequestBody RptReceiveDetailSearchVo vo);
	
	@PostMapping("findPageReceiveDetailSum")
	public RptCtrContractReceiveDetailReport findPageReceiveDetailSum(@RequestBody RptReceiveDetailSearchVo searchVo);

	@PostMapping("selectSellReceiveDateList")
	public List<RptContractDateVo> selectSellReceiveDateList(@RequestBody RptContractDateSearchVo searchVo);
}
