package com.spt.bas.client.remote;

import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.BizSign;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.http.feign.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @Author MoonLight
 * @Date 2024/10/29 9:40
 * @Version 1.0
 */
@FeignClient(name = BasConstants.SERVER_NAME, path = BasConstants.SERVER_NAME + "/biz/sign", url = BasConstants.SERVER_URL, configuration = FeignConfig.class)
public interface IBizSignClient extends BaseClient<BizSign> {

    @PostMapping(value = "getBizSignList")
    List<BizSign> getBizSignList(@RequestBody Long approveId);
}