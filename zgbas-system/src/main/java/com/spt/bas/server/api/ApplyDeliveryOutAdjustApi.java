package com.spt.bas.server.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.spt.bas.client.entity.ApplyDeliveryOutAdjust;
import com.spt.bas.server.service.IApplyDeliveryOutAdjustService;
import com.spt.tools.data.service.BaseApi;
import com.spt.tools.jpa.service.IBaseService;


@RestController
@RequestMapping(value = "apply/deliveryOutAdjust")
public class ApplyDeliveryOutAdjustApi extends BaseApi<ApplyDeliveryOutAdjust> {
	@Autowired
	private IApplyDeliveryOutAdjustService applyDeliveryOutAdjustService;
	
	@Override
	public IBaseService<ApplyDeliveryOutAdjust> getService() {
		return applyDeliveryOutAdjustService;
	}
	
	
	
}

