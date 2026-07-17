package com.spt.bas.client.remote;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.CtrContractSchedule;
import com.spt.tools.core.bean.PageSearchVo;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.data.vo.PageDown;
import com.spt.tools.http.feign.FeignConfig;


@FeignClient(name = BasConstants.SERVER_NAME,path= BasConstants.SERVER_NAME+"/contract/schedule",url=BasConstants.SERVER_URL,configuration=FeignConfig.class)
public interface ICtrContractScheduleClient extends BaseClient<CtrContractSchedule> {
	
	@RequestMapping(value = "findSchedulePage")
	PageDown<CtrContractSchedule> findSchedulePage(@RequestBody PageSearchVo searchVo);
}

