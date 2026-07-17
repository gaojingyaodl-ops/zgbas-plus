package com.spt.bas.client.remote;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.ApplyContractAdjustDetail;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.http.feign.FeignConfig;


@FeignClient(name = BasConstants.SERVER_NAME,path= BasConstants.SERVER_NAME+"/apply/contractAdjustDetail",url=BasConstants.SERVER_URL,configuration=FeignConfig.class)
public interface IApplyContractAdjustDetailClient extends BaseClient<ApplyContractAdjustDetail> {
	
	@PostMapping(value="findByContractAdjustId")
	public List<ApplyContractAdjustDetail> findByContractAdjustId(@RequestBody Long contractAdjustId);
	
}

