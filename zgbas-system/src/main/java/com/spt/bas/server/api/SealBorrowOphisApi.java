package com.spt.bas.server.api;

import com.spt.bas.server.service.ISealBorrowOphisService;
import com.spt.tools.data.service.BaseApi;
import com.spt.tools.jpa.service.IBaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.spt.bas.client.entity.SealBorrowOphis;


@RestController
@RequestMapping(value = "seal/borrowOphis")
public class SealBorrowOphisApi extends BaseApi<SealBorrowOphis> {
	@Autowired
	private ISealBorrowOphisService sealBorrowOphisService;
	
	@Override
	public IBaseService<SealBorrowOphis> getService() {
		return sealBorrowOphisService;
	}
	
	
	
}

