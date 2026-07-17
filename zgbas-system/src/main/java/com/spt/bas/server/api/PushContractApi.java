package com.spt.bas.server.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.spt.bas.client.entity.PushContract;
import com.spt.bas.server.service.IPushContractService;
import com.spt.tools.data.service.BaseApi;
import com.spt.tools.jpa.service.IBaseService;


@RestController
@RequestMapping(value = "push/contract")
public class PushContractApi extends BaseApi<PushContract> {
	@Autowired
	private IPushContractService pushContractService;
	
	@Override
	public IBaseService<PushContract> getService() {
		return pushContractService;
	}
	
	
	
}

