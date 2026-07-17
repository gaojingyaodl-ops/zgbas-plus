package com.spt.bas.server.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.spt.bas.client.entity.ApplyDeliveryInAdjust;
import com.spt.bas.server.service.IApplyDeliveryInAdjustService;
import com.spt.tools.data.service.BaseApi;
import com.spt.tools.jpa.service.IBaseService;


@RestController
@RequestMapping(value = "apply/deliveryInAdjust")
public class ApplyDeliveryInAdjustApi extends BaseApi<ApplyDeliveryInAdjust> {
	@Autowired
	private IApplyDeliveryInAdjustService applyDeliveryInAdjustService;
	
	@Override
	public IBaseService<ApplyDeliveryInAdjust> getService() {
		return applyDeliveryInAdjustService;
	}
	
	
	
}

