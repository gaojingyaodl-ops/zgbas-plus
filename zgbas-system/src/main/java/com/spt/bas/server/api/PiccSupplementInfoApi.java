package com.spt.bas.server.api;

import com.spt.bas.client.entity.PiccSupplementInfo;
import com.spt.bas.server.service.IPiccSupplementInfoService;
import com.spt.tools.data.service.BaseApi;
import com.spt.tools.data.service.IDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;



@RestController
@RequestMapping(value = "picc/supplementInfo")
public class PiccSupplementInfoApi extends BaseApi<PiccSupplementInfo> {
	
	@Autowired
	private IPiccSupplementInfoService piccSupplementInfoService;


	@Override
	public IDataService<PiccSupplementInfo> getService() {
		return piccSupplementInfoService;
	}

	@PostMapping("findByCompanyId")
	PiccSupplementInfo findByCompanyId(@RequestBody Long companyId){
		return piccSupplementInfoService.findByCompanyId(companyId);
	}
	
}

