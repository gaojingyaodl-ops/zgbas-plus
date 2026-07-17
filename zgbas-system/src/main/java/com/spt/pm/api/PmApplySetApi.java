package com.spt.pm.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.spt.pm.entity.PmApplySet;
import com.spt.pm.service.IPmApplySetService;
import com.spt.tools.data.service.BaseApi;
import com.spt.tools.jpa.service.IBaseService;


@RestController
@RequestMapping(value = "pm/applySet")
public class PmApplySetApi extends BaseApi<PmApplySet> {
	@Autowired
	private IPmApplySetService pmApplySetService;
	
	@Override
	public IBaseService<PmApplySet> getService() {
		return pmApplySetService;
	}
	@PostMapping("findByProcessId")
	List<PmApplySet>findByProcessId(@RequestBody Long processId){
		return pmApplySetService.findByProcessId(processId);
	}
	
	
}

