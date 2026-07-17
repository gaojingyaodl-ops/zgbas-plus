package com.spt.bas.client.remote;

import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.BsCompanyConfig;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.http.feign.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @Author: gaojy
 * @create 2022/4/2 11:08
 * @version: 1.0
 * @description:
 */
@FeignClient(name = BasConstants.SERVER_NAME,path= BasConstants.SERVER_NAME+"/bs/companyConfig",url=BasConstants.SERVER_URL,configuration= FeignConfig.class)
public interface IBsCompanyConfigClient extends BaseClient<BsCompanyConfig> {

    @PostMapping(value = "findByBsCompanyIdAndMatchUserId")
    BsCompanyConfig findByBsCompanyIdAndMatchUserId(@RequestBody BsCompanyConfig bsCompanyConfig);
}
