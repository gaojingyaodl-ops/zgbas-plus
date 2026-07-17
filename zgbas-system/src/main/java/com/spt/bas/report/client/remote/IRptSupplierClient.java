package com.spt.bas.report.client.remote;

import com.spt.bas.report.client.constant.ReportConstant;
import com.spt.bas.report.client.entity.RptCompany;
import com.spt.bas.report.client.entity.RptSupplier;
import com.spt.bas.report.client.vo.RptCompanySearchVo;
import com.spt.tools.data.vo.PageDown;
import com.spt.tools.http.feign.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = ReportConstant.SERVER_NAME,path= ReportConstant.SERVER_NAME+"/rpt/supplier",url=ReportConstant.SERVER_URL,configuration=FeignConfig.class)
public interface IRptSupplierClient {
	
	@PostMapping("findRptSupplierList")
	List<RptSupplier> findRptSupplierList(@RequestBody RptCompanySearchVo vo);

	@PostMapping("findRptSupplierPage")
	PageDown<RptSupplier> findRptSupplierPage(@RequestBody RptCompanySearchVo searchVo);

	@PostMapping("selectAllRptSupplier")
	List<RptSupplier> selectAllRptSupplier();
	
}
