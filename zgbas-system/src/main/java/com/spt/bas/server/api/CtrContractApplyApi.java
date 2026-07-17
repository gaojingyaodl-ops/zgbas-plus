package com.spt.bas.server.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.spt.bas.client.entity.CtrContractApply;
import com.spt.bas.server.service.ICtrContractApplyService;
import com.spt.tools.data.service.BaseApi;
import com.spt.tools.jpa.service.IBaseService;


@RestController
@RequestMapping(value = "bs/path")
public class CtrContractApplyApi extends BaseApi<CtrContractApply> {
	@Autowired
	private ICtrContractApplyService ctrContractApplyService;
	
	@Override
	public IBaseService<CtrContractApply> getService() {
		return ctrContractApplyService;
	}
	
	@PostMapping("findByContractId")
	CtrContractApply findByContractId(@RequestBody Long contractId){
		return ctrContractApplyService.findByContractId(contractId);
	}
	
}

