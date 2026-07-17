package com.spt.bas.client.remote;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.BasReceive;
import com.spt.bas.client.vo.BasReceiveVo;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.http.feign.FeignConfig;


@FeignClient(name = BasConstants.SERVER_NAME,path= BasConstants.SERVER_NAME+"/bas/receive",url=BasConstants.SERVER_URL,configuration=FeignConfig.class)
public interface IBasReceiveClient extends BaseClient<BasReceive> {
	
	@PostMapping("saveStatus")
	void saveStatus(@RequestBody BasReceive receive);
	
	@PostMapping("findByReceiveVo")
	BasReceive findByReceiveVo(@RequestBody BasReceiveVo vo);

}

