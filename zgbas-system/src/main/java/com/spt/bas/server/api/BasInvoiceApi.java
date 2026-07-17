package com.spt.bas.server.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.spt.bas.client.entity.BasInvoice;
import com.spt.bas.client.vo.FileIdUpdateVo;
import com.spt.bas.server.service.IBasInvoiceService;
import com.spt.tools.data.service.BaseApi;
import com.spt.tools.jpa.service.IBaseService;


@RestController
@RequestMapping(value = "bas/invoice")
public class BasInvoiceApi extends BaseApi<BasInvoice> {
	@Autowired
	private IBasInvoiceService basInvoiceService;
	
	@Override
	public IBaseService<BasInvoice> getService() {
		return basInvoiceService;
	}
	
	

	@PostMapping("updateFileId")
	public void updateFileId(@RequestBody FileIdUpdateVo vo) {
		basInvoiceService.updateFileId(vo.getId(), vo.getFileId());
	}
}

