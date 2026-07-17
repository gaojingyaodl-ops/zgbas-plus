package com.spt.bas.client.remote;

import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.CtrContractApply;
import com.spt.bas.client.entity.CtrContractDcsxApply;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.http.feign.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@FeignClient(name = BasConstants.SERVER_NAME,path= BasConstants.SERVER_NAME+"/bs/contractDcsx",url=BasConstants.SERVER_URL,configuration=FeignConfig.class)
public interface ICtrContractDcsxApplyClient extends BaseClient<CtrContractDcsxApply> {

	@PostMapping("findByContractId")
	CtrContractDcsxApply findByContractId(@RequestBody Long contractId);
	
}

