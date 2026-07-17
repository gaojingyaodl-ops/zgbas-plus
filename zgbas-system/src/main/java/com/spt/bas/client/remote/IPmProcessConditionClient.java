package com.spt.bas.client.remote;

import com.spt.bas.client.constant.BasConstants;
import com.spt.pm.entity.PmProcessCondition;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.http.feign.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;


@FeignClient(name = BasConstants.SERVER_NAME,path= BasConstants.SERVER_NAME+"/pm/processCondition",url=BasConstants.SERVER_URL,configuration=FeignConfig.class)
public interface IPmProcessConditionClient extends BaseClient<PmProcessCondition> {

    @RequestMapping(value = "findConditionsByProcessId")
    List<PmProcessCondition> findConditionsByProcessId(@RequestBody Long processId);
}

