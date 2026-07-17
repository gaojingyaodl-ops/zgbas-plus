package com.spt.bas.client.remote;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.CtrContractFollow;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.http.feign.FeignConfig;

import java.util.List;


@FeignClient(name = BasConstants.SERVER_NAME,path= BasConstants.SERVER_NAME+"/bs/follow",url=BasConstants.SERVER_URL,configuration=FeignConfig.class)
public interface ICtrContractFollowClient extends BaseClient<CtrContractFollow> {
	@PostMapping("toNotify")
	public void toNotify(@RequestBody CtrContractFollow follow);
	
	@PostMapping("findByCtrContractId")
	List<CtrContractFollow> findByCtrContractId(@RequestBody Long ctrContractId);
}