package com.spt.bas.server.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.spt.bas.client.entity.BsCompanyFollow;
import com.spt.bas.server.service.IBsCompanyFollowService;
import com.spt.tools.data.service.BaseApi;
import com.spt.tools.jpa.service.IBaseService;


@RestController
@RequestMapping(value = "/bs/companyFollow")
public class BsCompanyFollowApi extends BaseApi<BsCompanyFollow> {
	@Autowired
	private IBsCompanyFollowService bsCompanyFollowService;
	
	@Override
	public IBaseService<BsCompanyFollow> getService() {
		return bsCompanyFollowService;
	}
	
}

