package com.spt.bas.client.remote;

import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.BsMatchProfitsConfig;
import com.spt.bas.client.entity.BsProductConfig;
import com.spt.bas.client.vo.BsProductConfigVo;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.http.feign.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;


@FeignClient(name = BasConstants.SERVER_NAME,path= BasConstants.SERVER_NAME+"/bs/matchConfig",url=BasConstants.SERVER_URL,configuration=FeignConfig.class)
public interface IBsMatchProfitsConfigClient extends BaseClient<BsMatchProfitsConfig> {

    @RequestMapping("findByMathUserId")
    public List<BsMatchProfitsConfig> findByMathUserId(@RequestBody Long mathUserId);
}

