package com.spt.pm.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.spt.pm.entity.PmProcessAccess;
import com.spt.pm.service.IPmProcessAccessService;
import com.spt.pm.vo.PmProcessAccessVo;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.data.service.BaseApi;
import com.spt.tools.jpa.service.IBaseService;


@RestController
@RequestMapping(value = "pm/processAccess")
public class PmProcessAccessApi extends BaseApi<PmProcessAccess> {
	@Autowired
	private IPmProcessAccessService pmProcessAccessService;
	
	@Override
	public IBaseService<PmProcessAccess> getService() {
		return pmProcessAccessService;
	}
	
	@PostMapping(value = "findByProcessId")
	public List<PmProcessAccess> findByProcessId(@RequestBody Long processId){
		return pmProcessAccessService.findByProcessId(processId);
	}
	
	@PostMapping(value = "saveChanges")
	public void saveChanges(@RequestBody List<PmProcessAccess> list)throws ApplicationException {
		pmProcessAccessService.saveChanges(list);
	}
	
	@PostMapping(value = "findByUserId")
	public List<PmProcessAccess> findByUserId(@RequestBody Long userId){
		return pmProcessAccessService.findByUserId(userId);
	}
	
	@PostMapping(value = "saveByUser")
	public void saveByUser(@RequestBody PmProcessAccessVo vo){
		pmProcessAccessService.saveByUser(vo);
	}

	@PostMapping(value = "verifyUserProcessPermission")
	public Boolean verifyUserProcessPermission(@RequestBody PmProcessAccessVo vo){
		return pmProcessAccessService.verifyUserProcessPermission(vo);
	}
}

