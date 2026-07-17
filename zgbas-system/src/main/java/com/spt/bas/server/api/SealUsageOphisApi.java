package com.spt.bas.server.api;

import com.spt.bas.server.service.ISealUsageOphisService;
import com.spt.tools.data.service.BaseApi;
import com.spt.tools.jpa.service.IBaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.spt.bas.client.entity.SealUsageOphis;


@RestController
@RequestMapping(value = "usage/ophis")
public class SealUsageOphisApi extends BaseApi<SealUsageOphis> {
	@Autowired
	private ISealUsageOphisService sealUsageOphisService;
	
	@Override
	public IBaseService<SealUsageOphis> getService() {
		return sealUsageOphisService;
	}
	
	
	
}

