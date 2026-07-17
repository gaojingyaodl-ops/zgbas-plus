package com.spt.bas.client.remote;

import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.FundAmountFlow;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.http.feign.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @Author MoonLight
 * @Date 2024/7/15 9:39
 * @Version 1.0
 */
@FeignClient(name = BasConstants.SERVER_NAME, path = BasConstants.SERVER_NAME+"/fund/amountFlow", url = BasConstants.SERVER_URL, configuration = FeignConfig.class)
public interface IFundAmountFlowClient extends BaseClient<FundAmountFlow> {

}
