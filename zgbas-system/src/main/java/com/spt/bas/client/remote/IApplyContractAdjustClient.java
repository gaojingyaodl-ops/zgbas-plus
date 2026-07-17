package com.spt.bas.client.remote;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.ApplyContractAdjust;
import com.spt.bas.client.vo.FileIdUpdateVo;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.http.feign.FeignConfig;


@FeignClient(qualifier="applyContractAdjustClient", name = BasConstants.SERVER_NAME,path= BasConstants.SERVER_NAME+"/apply/contractAdjust",url=BasConstants.SERVER_URL,configuration=FeignConfig.class)
public interface IApplyContractAdjustClient extends BaseClient<ApplyContractAdjust> {
	@PostMapping("updateSellFileId")
	void updateSellFileId(@RequestBody FileIdUpdateVo vo);

	@PostMapping("updateBuyFileId")
	void updateBuyFileId(@RequestBody FileIdUpdateVo vo);

	@PostMapping("updateFileId")
	void updateFileId(@RequestBody FileIdUpdateVo vo);
}

