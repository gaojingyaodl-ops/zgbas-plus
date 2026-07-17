package com.spt.bas.server.api;

import com.spt.bas.client.entity.CtrContractApply;
import com.spt.bas.client.entity.CtrContractDcsxApply;
import com.spt.bas.server.service.ICtrContractDcsxApplyService;
import com.spt.tools.data.service.BaseApi;
import com.spt.tools.jpa.service.IBaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(value = "bs/contractDcsx")
public class CtrContractDcsxApplyApi extends BaseApi<CtrContractDcsxApply> {
	@Autowired
	private ICtrContractDcsxApplyService ctrContractDcsxApplyService;
	
	@Override
	public IBaseService<CtrContractDcsxApply> getService() {
		return ctrContractDcsxApplyService;
	}
	
	@PostMapping("findByContractId")
	CtrContractDcsxApply findByContractId(@RequestBody Long contractId){
		return ctrContractDcsxApplyService.findByContractId(contractId);
	}
	
}

