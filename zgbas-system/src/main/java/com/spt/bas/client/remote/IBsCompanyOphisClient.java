package com.spt.bas.client.remote;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.BsCompanyOphis;
import com.spt.bas.client.vo.BsCompanyOphisVo;
import com.spt.bas.client.vo.CompanyStatusVo;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.http.feign.FeignConfig;


@FeignClient(name = BasConstants.SERVER_NAME,path= BasConstants.SERVER_NAME+"/bs/companyOphis",url=BasConstants.SERVER_URL,configuration=FeignConfig.class)
public interface IBsCompanyOphisClient extends BaseClient<BsCompanyOphis> {
	@PostMapping("haveFllowByUser")
	public Boolean haveFllowByUser(@RequestBody CompanyStatusVo companyVo);
	
	@PostMapping("addCompanyHis")
	public void addCompanyHis(@RequestBody BsCompanyOphisVo opHis);
	
}

