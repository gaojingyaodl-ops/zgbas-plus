package com.spt.bas.server.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.spt.bas.client.entity.BsCompanyContacts;
import com.spt.bas.server.service.IBsCompanyContactsService;
import com.spt.tools.data.service.BaseApi;
import com.spt.tools.jpa.service.IBaseService;


@RestController
@RequestMapping(value = "bs/companyContacts")
public class BsCompanyContactsApi extends BaseApi<BsCompanyContacts> {
	@Autowired
	private IBsCompanyContactsService bsCompanyContactsService;
	
	@Override
	public IBaseService<BsCompanyContacts> getService() {
		return bsCompanyContactsService;
	}
	
	
	
}

