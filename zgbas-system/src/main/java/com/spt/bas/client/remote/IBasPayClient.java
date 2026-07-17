package com.spt.bas.client.remote;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.BasPay;
import com.spt.bas.client.vo.BasPayTicketVo;
import com.spt.bas.client.vo.FileIdUpdateVo;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.http.feign.FeignConfig;


@FeignClient(qualifier="basPayClient",name = BasConstants.SERVER_NAME,path= BasConstants.SERVER_NAME+"/bas/pay",url=BasConstants.SERVER_URL,configuration=FeignConfig.class)
public interface IBasPayClient extends BaseClient<BasPay> {
	
	@PostMapping("updateFileId")
	public void updateFileId(@RequestBody FileIdUpdateVo vo);
	
	/**保存发票信息*/
	@PostMapping("saveTicket")
	public BasPay saveTicket(@RequestBody BasPayTicketVo vo);
	
}

