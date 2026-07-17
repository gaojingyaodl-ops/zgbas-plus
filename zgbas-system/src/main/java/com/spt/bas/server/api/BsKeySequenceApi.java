package com.spt.bas.server.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.spt.pm.entity.BsKeySequence;
import com.spt.pm.service.IBsKeySequenceService;
import com.spt.tools.data.service.BaseApi;
import com.spt.tools.jpa.service.IBaseService;


@RestController
@RequestMapping(value = "bs/keySequence")
public class BsKeySequenceApi extends BaseApi<BsKeySequence> {
	@Autowired
	private IBsKeySequenceService bsKeySequenceService;
	
	@Override
	public IBaseService<BsKeySequence> getService() {
		return bsKeySequenceService;
	}
	
	@PostMapping("getNextKey")
	public String getNextKey(@RequestBody BsKeySequence bsKeySequence){
		String nextKey = bsKeySequenceService.getNextKey(bsKeySequence.getKeyCategory(), bsKeySequence.getEnterpriseId());
		return nextKey;
	}
}

