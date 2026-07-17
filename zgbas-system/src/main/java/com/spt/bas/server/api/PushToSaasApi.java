package com.spt.bas.server.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.spt.bas.client.entity.PushToSaas;
import com.spt.bas.server.service.IPushToSaasService;
import com.spt.tools.data.service.BaseApi;
import com.spt.tools.jpa.service.IBaseService;


@RestController
@RequestMapping(value = "/push/toSaas")
public class PushToSaasApi extends BaseApi<PushToSaas> {
	@Autowired
	private IPushToSaasService pushToSaasService;
	
	@Override
	public IBaseService<PushToSaas> getService() {
		return pushToSaasService;
	}
	
	
	
}

