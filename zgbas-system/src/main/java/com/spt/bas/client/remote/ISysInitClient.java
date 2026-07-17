package com.spt.bas.client.remote;

import com.spt.bas.client.vo.DeptNode;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.vo.SysInitRequestVo;
import com.spt.tools.http.feign.FeignConfig;

import java.util.List;


@FeignClient(name = BasConstants.SERVER_NAME,path= BasConstants.SERVER_NAME+"/sys/init",url=BasConstants.SERVER_URL,configuration=FeignConfig.class)
public interface ISysInitClient {
	
	@PostMapping("systemInit")
	void systemInit(@RequestBody SysInitRequestVo sysVo);
	
	@PostMapping("createUser")
	void createUser(@RequestBody SysInitRequestVo sysVo);

	@PostMapping("getDeptTree")
	List<DeptNode> getDeptTree();
}

