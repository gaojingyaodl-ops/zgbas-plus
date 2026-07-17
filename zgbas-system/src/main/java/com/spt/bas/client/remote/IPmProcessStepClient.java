package com.spt.bas.client.remote;

import org.springframework.cloud.openfeign.FeignClient;

import com.spt.bas.client.constant.BasConstants;
import com.spt.pm.entity.PmProcessStep;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.http.feign.FeignConfig;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;


@FeignClient(name = BasConstants.SERVER_NAME,path= BasConstants.SERVER_NAME+"/pm/processStep",url=BasConstants.SERVER_URL,configuration=FeignConfig.class)
public interface IPmProcessStepClient extends BaseClient<PmProcessStep> {

    @PostMapping("findEnable")
    List<PmProcessStep> findEnable();


    @PostMapping(value = "findStepByConditionId")
    List<PmProcessStep> findStepByConditionId(@RequestBody Long conditionId);
}

