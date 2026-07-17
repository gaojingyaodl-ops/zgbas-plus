package com.spt.pm.api;

import com.spt.pm.entity.PmProcessAutoStep;
import com.spt.pm.service.IPmProcessAutoStepService;
import com.spt.tools.data.service.BaseApi;
import com.spt.tools.jpa.service.IBaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(value = "pm/processAutoStep")
public class PmProcessAutoStepApi extends BaseApi<PmProcessAutoStep> {
	@Autowired
	private IPmProcessAutoStepService pmProcessAutoStepService;
	
	@Override
	public IBaseService<PmProcessAutoStep> getService() {
		return pmProcessAutoStepService;
	}
}

