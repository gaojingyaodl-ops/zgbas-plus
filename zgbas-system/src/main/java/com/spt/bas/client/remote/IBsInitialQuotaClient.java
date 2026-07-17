package com.spt.bas.client.remote;

import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.BsInitialQuota;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.http.feign.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * 企业初始额度
 * @author shengong
 */
@FeignClient(name = BasConstants.SERVER_NAME, path = BasConstants.SERVER_NAME + "/bs/initialQuota", url = BasConstants.SERVER_URL, configuration = FeignConfig.class)
public interface IBsInitialQuotaClient extends BaseClient<BsInitialQuota> {
}
