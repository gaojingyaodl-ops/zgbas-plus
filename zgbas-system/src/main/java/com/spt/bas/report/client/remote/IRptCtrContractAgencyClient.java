package com.spt.bas.report.client.remote;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.spt.bas.report.client.constant.ReportConstant;
import com.spt.bas.report.client.entity.RptCtrContractAgencyReport;
import com.spt.bas.report.client.vo.RptAssementSearchVo;
import com.spt.tools.data.vo.PageDown;
import com.spt.tools.http.feign.FeignConfig;

@FeignClient(name = ReportConstant.SERVER_NAME,path= ReportConstant.SERVER_NAME+"/rpt/ageny",url=ReportConstant.SERVER_URL,configuration=FeignConfig.class)
public interface IRptCtrContractAgencyClient {
	
	@PostMapping("findPageAgency")
	public PageDown<RptCtrContractAgencyReport> findPageAgency(@RequestBody RptAssementSearchVo vo);
	
	@PostMapping("findPageTotal")
	public RptCtrContractAgencyReport findPageTotal(@RequestBody RptAssementSearchVo vo);
	
	@PostMapping("findAgencyBySellId")
	public List<RptCtrContractAgencyReport> findAgencyBySellId(@RequestBody RptAssementSearchVo vo);
	
	@PostMapping("findSecondCalculatePage")
	public PageDown<RptCtrContractAgencyReport> findSecondCalculatePage(@RequestBody RptAssementSearchVo vo);
}
