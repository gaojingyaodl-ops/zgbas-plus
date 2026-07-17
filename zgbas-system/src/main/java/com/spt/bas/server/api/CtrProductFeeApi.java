package com.spt.bas.server.api;

import com.spt.bas.server.service.ICtrProductFeeService;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.data.service.BaseApi;
import com.spt.tools.jpa.service.IBaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.spt.bas.client.entity.CtrProductFee;
import com.spt.bas.client.vo.ApplyDeliveryReportVo;
import com.spt.bas.client.vo.CtrProductFeeVo;


@RestController
@RequestMapping(value = "product/fee")
public class CtrProductFeeApi extends BaseApi<CtrProductFee> {
	@Autowired
	private ICtrProductFeeService ctrProductFeeService;
	
	@Override
	public IBaseService<CtrProductFee> getService() {
		return ctrProductFeeService;
	}
	
	@PostMapping(value = "findByDeliveryId")
	public CtrProductFee findByDeliveryId(@RequestBody CtrProductFeeVo feeVo) {
		return ctrProductFeeService.findByDeliveryId(feeVo);
	}
	
	@PostMapping(value = "saveProductFee")
	public void saveProductFee(@RequestBody ApplyDeliveryReportVo delivery) throws ApplicationException{
		ctrProductFeeService.saveProductFee(delivery);
	}
	
	@PostMapping(value = "getDefaultCtrProductFee")
	public CtrProductFee getDefaultCtrProductFee(@RequestBody Long deliveryOutId) {
		CtrProductFee fee = new CtrProductFee();
		try {
			fee = ctrProductFeeService.getDefaultCtrProductFee(deliveryOutId);
		} catch (Exception e) {
			// TODO: handle exception
		}
		return fee;
	}
	
}

