package com.spt.bas.report.client.remote;

import com.spt.bas.report.client.constant.ReportConstant;
import com.spt.bas.report.client.entity.RptCompanyCreditInfo0;
import com.spt.bas.report.client.vo.RptOpenCompanyCreditQueryVo;
import com.spt.bas.report.client.vo.RptOpenCompanyCreditVo;
import com.spt.bas.report.client.vo.RptPartBsCompanyVo;
import com.spt.tools.http.feign.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = ReportConstant.SERVER_NAME,path= ReportConstant.SERVER_NAME+"/bs/company",url=ReportConstant.SERVER_URL,configuration=FeignConfig.class)
public interface IRptBsCompanyClient {
	
	@PostMapping("findCompanyList")
	public List<RptPartBsCompanyVo> findCompanyList(@RequestBody RptPartBsCompanyVo vo);
	
	@PostMapping("findCompanyById")
	public RptPartBsCompanyVo findCompanyById(@RequestBody RptPartBsCompanyVo vo);
	
	@PostMapping(value = "findCompany")
	public List<RptPartBsCompanyVo> findCompany(@RequestBody RptPartBsCompanyVo vo);

	@PostMapping(value = "countCompanyByName")
	public int countCompanyByName(@RequestBody String companyName);

	@PostMapping(value = "getRelationShipApproveIdByCompanyId")
	public List<Long> getRelationShipApproveIdByCompanyId(@RequestBody Long matchUserId);

	@PostMapping(value = "getRelationShipApproveIdByCompanyIds")
	public List<Long> getRelationShipApproveIdByCompanyIds(@RequestBody List<Long> matchUserIds);
	@PostMapping(value = "getCompanyCreditInfo0")
	public List<RptCompanyCreditInfo0> getCompanyCreditInfo0();

	@PostMapping("findOpenCreditList")
	List<RptOpenCompanyCreditVo> findOpenCreditList(@RequestBody RptOpenCompanyCreditQueryVo searchVo);
}
