package com.spt.bas.client.remote;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.spt.bas.client.constant.BasConstants;
import com.spt.pm.entity.PmProcessAccess;
import com.spt.pm.vo.PmProcessAccessVo;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.http.feign.FeignConfig;


@FeignClient(name = BasConstants.SERVER_NAME,path= BasConstants.SERVER_NAME+"/pm/processAccess",url=BasConstants.SERVER_URL,configuration=FeignConfig.class)
public interface IPmProcessAccessClient extends BaseClient<PmProcessAccess> {
	
	@PostMapping(value = "findByProcessId")
	List<PmProcessAccess> findByProcessId(@RequestBody Long processId);
	
	@PostMapping(value = "saveChanges")
	void saveChanges(@RequestBody List<PmProcessAccess> list);
	
	@PostMapping(value = "findByUserId")
	List<PmProcessAccess> findByUserId(@RequestBody Long userId);
	
	@PostMapping(value = "saveByUser")
	void saveByUser(@RequestBody PmProcessAccessVo vo);

	@PostMapping(value = "verifyUserProcessPermission")
	Boolean verifyUserProcessPermission(@RequestBody PmProcessAccessVo vo);
}

