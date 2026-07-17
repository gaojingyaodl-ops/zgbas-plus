package com.spt.bas.server.api;

import com.spt.bas.client.entity.BsConfig;
import com.spt.bas.server.service.IBsConfigService;
import com.spt.tools.data.service.BaseApi;
import com.spt.tools.jpa.service.IBaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping(value = "bs/bsConfig")
public class BsConfigApi extends BaseApi<BsConfig> {
	@Autowired
	private IBsConfigService bsConfigService;
	
	@Override
	public IBaseService<BsConfig> getService() {
		return bsConfigService;
	}


	@PostMapping(value = "findConfigMessageList")
	public List<String> findConfigMessageList(@RequestBody Long enterpriseId){
		return bsConfigService.findConfigMessageList(enterpriseId);
	}

	@PostMapping(value = "getBsConfigList")
	public List<BsConfig> getBsConfigList(@RequestBody Long enterpriseId){
		return bsConfigService.getBsConfigList(enterpriseId);
	}
}

