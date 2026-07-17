package com.spt.bas.server.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.spt.bas.client.entity.BasContractOphis;
import com.spt.bas.server.service.IBasContractOphisService;
import com.spt.tools.data.service.BaseApi;
import com.spt.tools.jpa.service.IBaseService;


@RestController
@RequestMapping(value = "bs/contractOphis")
public class BasContractOphisApi extends BaseApi<BasContractOphis> {
	@Autowired
	private IBasContractOphisService basContractOphisService;
	
	@Override
	public IBaseService<BasContractOphis> getService() {
		return basContractOphisService;
	}
	
}

