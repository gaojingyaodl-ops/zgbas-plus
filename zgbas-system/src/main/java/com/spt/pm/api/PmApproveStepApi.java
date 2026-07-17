package com.spt.pm.api;

import com.spt.pm.entity.PmApproveStep;
import com.spt.pm.service.IPmApproveStepService;
import com.spt.tools.data.service.BaseApi;
import com.spt.tools.jpa.service.IBaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping(value = "pm/approveStep")
public class PmApproveStepApi extends BaseApi<PmApproveStep> {
	@Autowired
	private IPmApproveStepService pmApproveStepService;
	
	@Override
	public IBaseService<PmApproveStep> getService() {
		return pmApproveStepService;
	}
	
	@PostMapping(value = "getFirstStep")
	public PmApproveStep getFirstStep(@RequestBody Long approveId) {
		return pmApproveStepService.getFirstStep(approveId);
	}
	
	@PostMapping(value = "findByApproveId")
	public List<PmApproveStep> findByApproveId(@RequestBody Long approveId){
		return pmApproveStepService.findByApproveId(approveId);
	}

	@PostMapping(value = "findStepByIds")
	public List<PmApproveStep> findStepByIds(@RequestBody List<Long> stepIdList){
		return pmApproveStepService.findStepByIds(stepIdList);
	}
}

