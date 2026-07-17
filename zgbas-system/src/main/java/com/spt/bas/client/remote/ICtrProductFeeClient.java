package com.spt.bas.client.remote;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.CtrProductFee;
import com.spt.bas.client.vo.ApplyDeliveryReportVo;
import com.spt.bas.client.vo.CtrProductFeeVo;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.http.feign.FeignConfig;


@FeignClient(name = BasConstants.SERVER_NAME,path= BasConstants.SERVER_NAME+"/product/fee",url=BasConstants.SERVER_URL,configuration=FeignConfig.class)
public interface ICtrProductFeeClient extends BaseClient<CtrProductFee> {
	
	@PostMapping(value = "findByDeliveryId")
	public CtrProductFee findByDeliveryId(@RequestBody CtrProductFeeVo feeVo);
	
	@PostMapping(value = "saveProductFee")
	public void saveProductFee(@RequestBody ApplyDeliveryReportVo delivery);
	
	@PostMapping(value = "getDefaultCtrProductFee")
	public CtrProductFee getDefaultCtrProductFee(@RequestBody Long deliveryOutId);
}

