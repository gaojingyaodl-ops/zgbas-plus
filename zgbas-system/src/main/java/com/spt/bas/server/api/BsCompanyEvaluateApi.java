package com.spt.bas.server.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.spt.bas.client.entity.BsCompanyEvaluate;
import com.spt.bas.server.service.IBsCompanyEvaluateService;
import com.spt.tools.data.service.BaseApi;
import com.spt.tools.jpa.service.IBaseService;


@RestController
@RequestMapping(value = "/bs/companyEvaluate")
public class BsCompanyEvaluateApi extends BaseApi<BsCompanyEvaluate> {
	@Autowired
	private IBsCompanyEvaluateService bsCompanyEvaluateService;
	
	@Override
	public IBaseService<BsCompanyEvaluate> getService() {
		return bsCompanyEvaluateService;
	}
	
	
	
}

