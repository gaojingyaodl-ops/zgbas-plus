package com.spt.bas.client.remote;

import org.springframework.cloud.openfeign.FeignClient;

import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.PushToSaas;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.http.feign.FeignConfig;


@FeignClient(name = BasConstants.SERVER_NAME,path= BasConstants.SERVER_NAME+"/push/toSaas",url=BasConstants.SERVER_URL,configuration=FeignConfig.class)
public interface IPushToSaasClient extends BaseClient<PushToSaas> {
	
}

