package com.spt.bas.client.remote;

import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.ApplyDelivery;
import com.spt.bas.client.vo.*;
import com.spt.tools.core.bean.PageSearchVo;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.data.vo.PageDown;
import com.spt.tools.http.feign.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;



@FeignClient(qualifier="applyDeliveryClient",name = BasConstants.SERVER_NAME,path= BasConstants.SERVER_NAME+"/apply/delivery",url=BasConstants.SERVER_URL,configuration=FeignConfig.class)
public interface IApplyDeliveryClient extends BaseClient<ApplyDelivery> {

	@PostMapping("startPrint")
	void startPrint(@RequestBody ApplyDeliveryVo vo);
		
	@PostMapping("getApplyDeliveryEntity")
	ApplyDelivery getApplyDeliveryEntity(@RequestBody ApplyDeliveryApplyIdVo vo);
	
	@PostMapping("getApplyDeliverySendSingleEntity")
	ApplyDelivery getApplyDeliverySendSingleEntity(@RequestBody ApplyDeliveryApplyIdVo vo);
	
	@PostMapping("getApplyDeliveryInvoiceEntity")
	ApplyDelivery getApplyDeliveryInvoiceEntity(@RequestBody ApplyDeliveryApplyIdVo vo);
	
	@PostMapping("getApplyDeliveryDistributionEntity")
	ApplyDelivery getApplyDeliveryDistributionEntity(@RequestBody ApplyDeliveryApplyIdVo vo);
	
	@PostMapping("doCancel")
	void doCancel(@RequestBody ApplyDeliveryCancelVo cancelVo) throws ApplicationException;
	
	@PostMapping("findPageDetail")
	PageDown<DeliveryDetailVo> findPageDetail(@RequestBody PageSearchVo searchVo);
	
	@PostMapping("saveDeliveryDetail")
	void saveDeliveryDetail(@RequestBody ApplyDeliveryReportVo delivery);

}

