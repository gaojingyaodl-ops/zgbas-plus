package com.spt.bas.client.remote;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.spt.bas.client.constant.BasConstants;

import com.spt.bas.client.entity.ApplyImportBuy;
import com.spt.bas.client.vo.FileIdUpdateVo;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.http.feign.FeignConfig;


@FeignClient(qualifier="applyImportBuyClient", name = BasConstants.SERVER_NAME,path= BasConstants.SERVER_NAME+"/apply/importBuy",url=BasConstants.SERVER_URL,configuration=FeignConfig.class)
public interface IApplyImportBuyClient extends BaseClient<ApplyImportBuy> {
	@PostMapping("updateFileId")
	public void updateFileId(@RequestBody FileIdUpdateVo vo);
	
	@PostMapping("findByContractId")
	public ApplyImportBuy findByContractId(@RequestBody Long contractId);
}

