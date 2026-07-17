package com.spt.bas.server.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.spt.bas.client.entity.BsCompanyOphis;
import com.spt.bas.client.vo.BsCompanyOphisVo;
import com.spt.bas.client.vo.CompanyStatusVo;
import com.spt.bas.server.service.IBsCompanyOphisService;
import com.spt.tools.data.service.BaseApi;
import com.spt.tools.jpa.service.IBaseService;


@RestController
@RequestMapping(value = "bs/companyOphis")
public class BsCompanyOphisApi extends BaseApi<BsCompanyOphis> {
	@Autowired
	private IBsCompanyOphisService bsCompanyOphisService;
	
	@Override
	public IBaseService<BsCompanyOphis> getService() {
		return bsCompanyOphisService;
	}
	
	@PostMapping("haveFllowByUser")
	public Boolean haveFllowByUser(@RequestBody CompanyStatusVo companyVo){
		return bsCompanyOphisService.haveFllowByUser(companyVo);
	}
	
	@PostMapping("addCompanyHis")
	public void addCompanyHis(@RequestBody BsCompanyOphisVo opHis){
		bsCompanyOphisService.addCompanyHis(opHis);
	}
}

