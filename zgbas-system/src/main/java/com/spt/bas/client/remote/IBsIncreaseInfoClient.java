package com.spt.bas.client.remote;

import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.BsIncreaseInfo;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.http.feign.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * @author shengong
 */
@FeignClient(name = BasConstants.SERVER_NAME, path = BasConstants.SERVER_NAME + "/bs/increaseInfo", url = BasConstants.SERVER_URL, configuration = FeignConfig.class)
public interface IBsIncreaseInfoClient extends BaseClient<BsIncreaseInfo> {

    @PostMapping("findByCompanyId")
    BsIncreaseInfo findByCompanyId(Long companyId);

}
