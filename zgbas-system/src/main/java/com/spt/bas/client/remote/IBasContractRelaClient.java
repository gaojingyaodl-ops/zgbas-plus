package com.spt.bas.client.remote;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.BasContractRela;
import com.spt.bas.client.vo.FileIdUpdateVo;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.http.feign.FeignConfig;


@FeignClient(qualifier="basContractRelaClient",name = BasConstants.SERVER_NAME,path= BasConstants.SERVER_NAME+"/bas/contractRela",url=BasConstants.SERVER_URL,configuration=FeignConfig.class)
public interface IBasContractRelaClient extends BaseClient<BasContractRela> {
	
	@PostMapping("updateFileId")
	public void updateFileId(@RequestBody FileIdUpdateVo vo);
	
	@PostMapping("findSaleContractIdByBuyId")
	public String findSaleContractIdByBuyId(@RequestBody String buyContractId);
}

