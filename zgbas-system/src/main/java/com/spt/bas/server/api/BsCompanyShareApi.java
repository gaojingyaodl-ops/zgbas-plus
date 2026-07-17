package com.spt.bas.server.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.spt.bas.client.entity.BsCompanyShare;
import com.spt.bas.server.service.IBsCompanyShareService;
import com.spt.tools.data.service.BaseApi;
import com.spt.tools.jpa.service.IBaseService;


@RestController
@RequestMapping(value = "bs/companyShare")
public class BsCompanyShareApi extends BaseApi<BsCompanyShare> {
	@Autowired
	private IBsCompanyShareService bsCompanyShareService;
	
	@Override
	public IBaseService<BsCompanyShare> getService() {
		return bsCompanyShareService;
	}
	
	@PostMapping("findByCompanyIdAndSharedUserId")
	BsCompanyShare findByCompanyIdAndSharedUserId(@RequestBody BsCompanyShare share){
		return bsCompanyShareService.findByCompanyIdAndSharedUserId(share.getCompanyId(), share.getSharedUserId());
	}
	
	@PostMapping("findByCompanyIdAndCreateUserId")
	List<BsCompanyShare> findByCompanyIdAndCreateUserId(@RequestBody BsCompanyShare share){
		return bsCompanyShareService.findByCompanyIdAndCreateUserId(share.getCompanyId(), share.getCreateUserId());
	}
	
	@PostMapping("findBySharedUserId")
	List<BsCompanyShare> findBySharedUserId(@RequestBody BsCompanyShare share){
		return bsCompanyShareService.findBySharedUserId(share.getSharedUserId());
	}
	
}

