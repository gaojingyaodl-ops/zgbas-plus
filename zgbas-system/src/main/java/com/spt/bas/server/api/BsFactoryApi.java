package com.spt.bas.server.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.spt.bas.client.entity.BsFactory;
import com.spt.bas.server.service.IBsFactoryService;
import com.spt.tools.data.service.BaseApi;
import com.spt.tools.jpa.service.IBaseService;


@RestController
@RequestMapping(value = "bs/factory")
public class BsFactoryApi extends BaseApi<BsFactory> {
	@Autowired
	private IBsFactoryService bsFactoryService;
	
	@Override
	public IBaseService<BsFactory> getService() {
		return bsFactoryService;
	}
	
	@PostMapping("findByEnterpriseId")
	List<BsFactory> findByEnterpriseId(@RequestBody Long enterpriseId){
		return bsFactoryService.findByEnterpriseId(enterpriseId);
	}
	
	@PostMapping("countFactory")
	Long countFactory(@RequestBody BsFactory factory){
		return bsFactoryService.countFactory(factory);
	}
	
	@PostMapping("findByFactoryNameAndEnterpriseId")
	List<BsFactory> findByFactoryNameAndEnterpriseId(@RequestBody BsFactory factory){
		return bsFactoryService.findByFactoryNameAndEnterpriseId(factory);
	}
	
}

