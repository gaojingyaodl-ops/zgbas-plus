package com.spt.bas.server.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.spt.bas.client.entity.ApplyContractAdjustDetail;
import com.spt.bas.server.service.IApplyContractAdjustDetailService;
import com.spt.tools.data.service.BaseApi;
import com.spt.tools.jpa.service.IBaseService;


@RestController
@RequestMapping(value = "apply/contractAdjustDetail")
public class ApplyContractAdjustDetailApi extends BaseApi<ApplyContractAdjustDetail> {
	@Autowired
	private IApplyContractAdjustDetailService applyContractAdjustDetailService;
	
	@Override
	public IBaseService<ApplyContractAdjustDetail> getService() {
		return applyContractAdjustDetailService;
	}
	
	@PostMapping(value="findByContractAdjustId")
	public List<ApplyContractAdjustDetail> findByContractAdjustId(@RequestBody Long contractAdjustId){
		return applyContractAdjustDetailService.findByContractAdjustId(contractAdjustId);
	}
	
}

