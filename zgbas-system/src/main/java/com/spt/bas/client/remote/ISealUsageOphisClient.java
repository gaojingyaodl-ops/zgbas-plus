package com.spt.bas.client.remote;

import org.springframework.cloud.openfeign.FeignClient;

import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.SealUsageOphis;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.http.feign.FeignConfig;


@FeignClient(name = BasConstants.SERVER_NAME,path= BasConstants.SERVER_NAME+"/usage/ophis",url=BasConstants.SERVER_URL,configuration=FeignConfig.class)
public interface ISealUsageOphisClient extends BaseClient<SealUsageOphis> {
	
}

