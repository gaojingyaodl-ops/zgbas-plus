package com.spt.bas.client.remote;

import com.spt.bas.client.constant.BasConstants;
import com.spt.pm.entity.PmApproveStep;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.http.feign.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;


@FeignClient(name = BasConstants.SERVER_NAME,path= BasConstants.SERVER_NAME+"/pm/approveStep",url=BasConstants.SERVER_URL,configuration=FeignConfig.class)
public interface IPmApproveStepClient extends BaseClient<PmApproveStep> {
	
	@PostMapping(value = "getFirstStep")
	PmApproveStep getFirstStep(@RequestBody Long approveId);
	
	@PostMapping(value = "findByApproveId")
	List<PmApproveStep> findByApproveId(@RequestBody Long approveId);

	@PostMapping(value = "findStepByIds")
	List<PmApproveStep> findStepByIds(@RequestBody List<Long> stepIdList);
	
}

