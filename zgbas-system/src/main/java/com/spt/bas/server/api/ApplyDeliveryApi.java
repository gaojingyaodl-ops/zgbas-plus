package com.spt.bas.server.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.spt.bas.client.entity.ApplyDelivery;
import com.spt.bas.client.vo.ApplyDeliveryApplyIdVo;
import com.spt.bas.client.vo.ApplyDeliveryCancelVo;
import com.spt.bas.client.vo.ApplyDeliveryReportVo;
import com.spt.bas.client.vo.ApplyDeliveryVo;
import com.spt.bas.client.vo.DeliveryDetailVo;
import com.spt.bas.server.service.IApplyDeliveryService;
import com.spt.tools.core.bean.PageSearchVo;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.data.service.BaseApi;
import com.spt.tools.jpa.service.IBaseService;


@RestController
@RequestMapping(value = "apply/delivery")
public class ApplyDeliveryApi extends BaseApi<ApplyDelivery> {
	@Autowired
	private IApplyDeliveryService applyDeliveryService;
	
	@Override
	public IBaseService<ApplyDelivery> getService() {
		return applyDeliveryService;
	}
	
	@PostMapping("startPrint")
	public void startPrint(@RequestBody ApplyDeliveryVo vo){
		applyDeliveryService.startPrint(vo);
	}	

	@PostMapping("getApplyDeliveryEntity")
	public ApplyDelivery getApplyDeliveryEntity(@RequestBody ApplyDeliveryApplyIdVo vo){
		return applyDeliveryService.getApplyDeliveryEntity(vo);
	}
	
	@PostMapping("getApplyDeliverySendSingleEntity")
	public ApplyDelivery getApplyDeliverySendSingleEntity(@RequestBody ApplyDeliveryApplyIdVo vo){
		return applyDeliveryService.getApplyDeliverySendSingleEntity(vo);
	}
	
	@PostMapping("getApplyDeliveryInvoiceEntity")
	public ApplyDelivery getApplyDeliveryInvoiceEntity(@RequestBody ApplyDeliveryApplyIdVo vo){
		return applyDeliveryService.getApplyDeliveryInvoiceEntity(vo);
	}
	
	@PostMapping("doCancel")
	public void doCancel(@RequestBody ApplyDeliveryCancelVo cancelVo) throws ApplicationException {
		applyDeliveryService.doCancel(cancelVo);
	}
	
	@PostMapping("findPageDetail")
	public Page<DeliveryDetailVo> findPageDetail(@RequestBody PageSearchVo searchVo){
		return applyDeliveryService.findPageDetail(searchVo);
	}
	
	@PostMapping("getApplyDeliveryDistributionEntity")
	public ApplyDelivery getApplyDeliveryDistributionEntity(@RequestBody ApplyDeliveryApplyIdVo vo) {
		return applyDeliveryService.getApplyDeliveryDistributionEntity(vo);
	}
	
	@PostMapping("saveDeliveryDetail")
	public void saveDeliveryDetail(@RequestBody ApplyDeliveryReportVo delivery) throws ApplicationException{
		applyDeliveryService.saveDetail(delivery);
	}
}

