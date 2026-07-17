package com.spt.bas.server.api;

import com.spt.bas.client.entity.BsCompanyCreditFlow;
import com.spt.bas.server.service.IBsCompanyCreditFlowService;
import com.spt.tools.data.service.BaseApi;
import com.spt.tools.jpa.service.IBaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(value = "bs/creditFlow")
public class BsCompanyCreditFlowApi extends BaseApi<BsCompanyCreditFlow> {
	@Autowired
	private IBsCompanyCreditFlowService bsCompanyCreditFlowService;
	
	@Override
	public IBaseService<BsCompanyCreditFlow> getService() {
		return bsCompanyCreditFlowService;
	}

}

