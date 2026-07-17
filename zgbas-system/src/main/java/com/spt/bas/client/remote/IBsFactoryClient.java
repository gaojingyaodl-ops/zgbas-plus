package com.spt.bas.client.remote;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.BsFactory;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.http.feign.FeignConfig;


@FeignClient(name = BasConstants.SERVER_NAME,path= BasConstants.SERVER_NAME+"/bs/factory",url=BasConstants.SERVER_URL,configuration=FeignConfig.class)
public interface IBsFactoryClient extends BaseClient<BsFactory> {
	
	@PostMapping("findByEnterpriseId")
	List<BsFactory>findByEnterpriseId(@RequestBody Long enterpriseId);
	
	@PostMapping("countFactory")
	Long countFactory(@RequestBody BsFactory factory);
	
	@PostMapping("findByFactoryNameAndEnterpriseId")
	List<BsFactory> findByFactoryNameAndEnterpriseId(@RequestBody BsFactory factory);
}

