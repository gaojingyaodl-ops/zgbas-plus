package com.spt.bas.client.remote;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.BasInvoice;
import com.spt.bas.client.vo.FileIdUpdateVo;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.http.feign.FeignConfig;


@FeignClient(qualifier="basInvoiceClient",name = BasConstants.SERVER_NAME,path= BasConstants.SERVER_NAME+"/bas/invoice",url=BasConstants.SERVER_URL,configuration=FeignConfig.class)
public interface IBasInvoiceClient extends BaseClient<BasInvoice> {
	
	@PostMapping("updateFileId")
	public void updateFileId(@RequestBody FileIdUpdateVo vo);
}

