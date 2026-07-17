package com.spt.bas.report.client.remote;

import com.spt.bas.report.client.constant.ReportConstant;
import com.spt.bas.report.client.entity.RptCompanyReceivables;
import com.spt.bas.report.client.vo.RptCompanyReceivablesSearchVo;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.data.vo.PageDown;
import com.spt.tools.http.feign.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = ReportConstant.SERVER_NAME,path= ReportConstant.SERVER_NAME+"/rpt/companyReceivables",url=ReportConstant.SERVER_URL,configuration=FeignConfig.class)
public interface IRptCompanyReceivablesClient extends BaseClient<RptCompanyReceivables> {
	
	@PostMapping("findRptCompanyReceivablesPage")
	PageDown<RptCompanyReceivables> findRptCompanyReceivablesPage(@RequestBody RptCompanyReceivablesSearchVo searchVo);
	
	@PostMapping("findRptCompanyReceivablesDetailPage")
	PageDown<RptCompanyReceivables> findRptCompanyReceivablesDetailPage(@RequestBody RptCompanyReceivablesSearchVo searchVo);
	/**
	 * 客户应收款合计查询
	 * @param searchVo
	 * @return
	 */
	@PostMapping("findRptCompanyReceivablesSum")
	RptCompanyReceivables findRptCompanyReceivablesSum(@RequestBody RptCompanyReceivablesSearchVo searchVo);
}
