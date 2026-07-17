package com.spt.bas.server.api;

import com.spt.bas.client.entity.ApplyInvoiceDelivery;
import com.spt.bas.client.vo.FileIdUpdateVo;
import com.spt.bas.server.service.IApplyInvoiceDeliveryService;
import com.spt.tools.data.service.BaseApi;
import com.spt.tools.jpa.service.IBaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(value = "apply/invoiceDelivery")
public class ApplyInvoiceDeliveryApi extends BaseApi<ApplyInvoiceDelivery> {
	@Autowired
	private IApplyInvoiceDeliveryService applyInvoiceDeliveryService;
	@Override
	public IBaseService<ApplyInvoiceDelivery> getService() {
		return applyInvoiceDeliveryService;
	}

	@PostMapping("updateFileId")
	public void updateFileId(@RequestBody FileIdUpdateVo vo){
		applyInvoiceDeliveryService.updateFileId(vo.getId(), vo.getFileId());
	}
}

