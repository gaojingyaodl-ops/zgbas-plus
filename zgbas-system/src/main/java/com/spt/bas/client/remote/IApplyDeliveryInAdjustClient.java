package com.spt.bas.client.remote;

import org.springframework.cloud.openfeign.FeignClient;

import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.ApplyDeliveryInAdjust;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.http.feign.FeignConfig;


@FeignClient(qualifier="applyDeliveryInAdjustClient", name = BasConstants.SERVER_NAME,path= BasConstants.SERVER_NAME+"/apply/deliveryInAdjust",url=BasConstants.SERVER_URL,configuration=FeignConfig.class)
public interface IApplyDeliveryInAdjustClient extends BaseClient<ApplyDeliveryInAdjust> {
	
}

