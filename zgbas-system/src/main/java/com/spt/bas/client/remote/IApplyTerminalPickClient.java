package com.spt.bas.client.remote;

import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.ApplyTerminalPick;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.http.feign.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(qualifier = "applyTerminalPick", name = BasConstants.SERVER_NAME, path = BasConstants.SERVER_NAME + "/", url = BasConstants.SERVER_URL, configuration = FeignConfig.class)
public interface IApplyTerminalPickClient extends BaseClient<ApplyTerminalPick> {

}
