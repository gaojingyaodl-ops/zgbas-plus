package com.spt.bas.server.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.spt.bas.client.entity.BasContractText;
import com.spt.bas.server.service.IBasContractTextService;
import com.spt.tools.data.service.BaseApi;
import com.spt.tools.jpa.service.IBaseService;

@RestController
@RequestMapping(value = "/bas/contractText")
public class BasContractTextApi extends BaseApi<BasContractText> {
	@Autowired
	private IBasContractTextService basContractTextService;
	
	@Override
	public IBaseService<BasContractText> getService() {
		return basContractTextService;
	}
	
	@PostMapping("getContractTextById")
	public BasContractText getContractTextById(@RequestBody Long contractTextId){
		return basContractTextService.getContractTextById(contractTextId);
	}
	
	
}

