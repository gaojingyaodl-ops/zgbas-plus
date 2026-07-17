package com.spt.pm.api;

import com.spt.pm.entity.PmProcessStep;
import com.spt.pm.service.IPmProcessStepService;
import com.spt.tools.data.service.BaseApi;
import com.spt.tools.jpa.service.IBaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping(value = "pm/processStep")
public class PmProcessStepApi extends BaseApi<PmProcessStep> {
	@Autowired
	private IPmProcessStepService pmProcessStepService;

	@Override
	public IBaseService<PmProcessStep> getService() {
		return pmProcessStepService;
	}

	@PostMapping("findEnable")
	public List<PmProcessStep> findEnable(){
		return pmProcessStepService.findEnable();
	}

	@PostMapping(value = "findStepByConditionId")
	public List<PmProcessStep> findStepByConditionId(@RequestBody Long conditionId){
		return pmProcessStepService.findStepByConditionId(conditionId);
	}
}

