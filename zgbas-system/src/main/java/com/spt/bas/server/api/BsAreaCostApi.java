package com.spt.bas.server.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.spt.bas.client.entity.BsAreaCost;
import com.spt.bas.server.service.IBsAreaCostService;
import com.spt.tools.data.service.BaseApi;
import com.spt.tools.jpa.service.IBaseService;


@RestController
@RequestMapping(value = "bs/areaCost")
public class BsAreaCostApi extends BaseApi<BsAreaCost> {
	@Autowired
	private IBsAreaCostService bsAreaCostService;
	
	@Override
	public IBaseService<BsAreaCost> getService() {
		return bsAreaCostService;
	}
	
	@PostMapping("findByAreaCode")
	public List<BsAreaCost> findByAreaCode(@RequestBody String areaCode){
		return bsAreaCostService.findByAreaCode(areaCode);
		
	}
	
}

