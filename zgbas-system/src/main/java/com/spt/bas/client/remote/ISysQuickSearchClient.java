package com.spt.bas.client.remote;

import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.SysQuickSearch;
import com.spt.bas.client.vo.SysQuickSearchVo;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.http.feign.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(qualifier = "applyBrandClient",name = BasConstants.SERVER_NAME,path= BasConstants.SERVER_NAME+"/sys/quick/search",url=BasConstants.SERVER_URL,configuration= FeignConfig.class)
public interface ISysQuickSearchClient extends BaseClient<SysQuickSearch> {

    @PostMapping("findListByUserIdAndModuleUrl")
    List<SysQuickSearch> findListByUserIdAndModuleUrl(@RequestBody SysQuickSearchVo searchVo);
    
}
