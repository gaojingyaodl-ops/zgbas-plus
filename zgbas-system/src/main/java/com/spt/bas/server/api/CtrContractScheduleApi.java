package com.spt.bas.server.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.spt.bas.client.entity.CtrContractSchedule;
import com.spt.bas.server.service.ICtrContractScheduleService;
import com.spt.tools.core.bean.PageSearchVo;
import com.spt.tools.data.service.BaseApi;
import com.spt.tools.jpa.service.IBaseService;


@RestController
@RequestMapping(value = "contract/schedule")
public class CtrContractScheduleApi extends BaseApi<CtrContractSchedule> {
	@Autowired
	private ICtrContractScheduleService ctrContractScheduleService;
	
	@Override
	public IBaseService<CtrContractSchedule> getService() {
		return ctrContractScheduleService;
	}
	
	@RequestMapping(value = "findSchedulePage")
	public Page<CtrContractSchedule> findSchedulePage(@RequestBody PageSearchVo searchVo){
		return ctrContractScheduleService.findSchedulePage(searchVo);
	}
	
}

