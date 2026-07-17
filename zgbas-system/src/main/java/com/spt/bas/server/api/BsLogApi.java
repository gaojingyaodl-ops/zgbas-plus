package com.spt.bas.server.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.spt.bas.client.entity.BsLog;
import com.spt.bas.server.service.IBsLogService;
import com.spt.tools.data.service.BaseApi;
import com.spt.tools.jpa.service.IBaseService;


@RestController
@RequestMapping(value = "bs/log")
public class BsLogApi extends BaseApi<BsLog> {
	@Autowired
	private IBsLogService bsLogService;
	
	@Override
	public IBaseService<BsLog> getService() {
		return bsLogService;
	}
	
	
	
}

