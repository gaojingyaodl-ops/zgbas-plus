package com.spt.bas.client.remote;


import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.BsAreaCost;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.http.feign.FeignConfig;


@FeignClient(name = BasConstants.SERVER_NAME,path= BasConstants.SERVER_NAME+"/bs/areaCost",url=BasConstants.SERVER_URL,configuration=FeignConfig.class)
public interface IBsAreaCostClient extends BaseClient<BsAreaCost> {

	
	@PostMapping("findByAreaCode")
	public List<BsAreaCost> findByAreaCode(@RequestBody String areaCode);
	
}

