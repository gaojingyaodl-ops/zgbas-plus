package com.spt.bas.client.remote;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.spt.bas.client.constant.BasConstants;
import com.spt.pm.entity.PmApprove;
import com.spt.pm.entity.PmApproveHistory;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.http.feign.FeignConfig;


@FeignClient(name = BasConstants.SERVER_NAME,path= BasConstants.SERVER_NAME+"/pm/approveHistory",url=BasConstants.SERVER_URL,configuration=FeignConfig.class)
public interface IPmApproveHistoryClient extends BaseClient<PmApproveHistory> {
	
	@PostMapping("findByApproveId")
	public List<PmApproveHistory> findByApproveId(@RequestBody Long approveId);
	
	@PostMapping("findByApproveIdOrProcessId")
	public List<PmApproveHistory> findByApproveIdOrProcessId(@RequestBody PmApprove pmApprove);
}

