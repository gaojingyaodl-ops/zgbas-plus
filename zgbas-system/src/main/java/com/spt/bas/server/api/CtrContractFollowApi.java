package com.spt.bas.server.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.spt.bas.client.entity.CtrContractFollow;
import com.spt.bas.server.service.ICtrContractFollowService;
import com.spt.tools.data.service.BaseApi;
import com.spt.tools.jpa.service.IBaseService;

import java.util.List;


@RestController
@RequestMapping(value = "bs/follow")
public class CtrContractFollowApi extends BaseApi<CtrContractFollow> {
	@Autowired
	private ICtrContractFollowService ctrContractFollowService;
	
	@Override
	public IBaseService<CtrContractFollow> getService() {
		return ctrContractFollowService;
	}
	
	@PostMapping("findByCtrContractId")
	List<CtrContractFollow> findByCtrContractId(@RequestBody Long ctrContractId){
		return ctrContractFollowService.findByCtrContractId(ctrContractId);
	}
	
	@PostMapping("toNotify")
	public void toNotify(@RequestBody CtrContractFollow follow){
		ctrContractFollowService.toNotify(follow);
	}
	
	
}

