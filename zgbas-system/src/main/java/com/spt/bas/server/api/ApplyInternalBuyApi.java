package com.spt.bas.server.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.spt.bas.client.entity.ApplyInternalBuy;
import com.spt.bas.server.service.IApplyInternalBuyService;
import com.spt.tools.data.service.BaseApi;
import com.spt.tools.jpa.service.IBaseService;


@RestController
@RequestMapping(value = "apply/internalBuy")
public class ApplyInternalBuyApi extends BaseApi<ApplyInternalBuy> {
	@Autowired
	private IApplyInternalBuyService applyInternalBuyService;
	
	@Override
	public IBaseService<ApplyInternalBuy> getService() {
		return applyInternalBuyService;
	}
	
	
	
}

