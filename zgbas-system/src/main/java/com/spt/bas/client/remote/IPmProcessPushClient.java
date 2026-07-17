package com.spt.bas.client.remote;

import com.spt.bas.client.constant.BasConstants;
import com.spt.pm.entity.PmProcessPush;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.http.feign.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @Author: gaojy
 * @create 2022/4/26 11:43
 * @version: 1.0
 * @description:
 */
@FeignClient(name = BasConstants.SERVER_NAME,path= BasConstants.SERVER_NAME+"/pm/processPush",url=BasConstants.SERVER_URL,configuration= FeignConfig.class)
public interface IPmProcessPushClient extends BaseClient<PmProcessPush> {
}
