package com.spt.pm.api;

import com.spt.pm.entity.PmProcessCondition;
import com.spt.pm.service.IPmProcessConditionService;
import com.spt.tools.data.service.BaseApi;
import com.spt.tools.jpa.service.IBaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping(value = "pm/processCondition")
public class PmProcessConditionApi extends BaseApi<PmProcessCondition> {
	@Autowired
	private IPmProcessConditionService pmProcessConditionService;
	
	@Override
	public IBaseService<PmProcessCondition> getService() {
		return pmProcessConditionService;
	}


	@RequestMapping(value = "findConditionsByProcessId")
	public List<PmProcessCondition> findConditionsByProcessId(@RequestBody Long processId){
		return pmProcessConditionService.findConditionsByProcessId(processId);
	}
}

