package com.spt.bas.client.remote;

import com.spt.bas.client.constant.BasConstants;
import com.spt.pm.entity.PmApprovePush;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.http.feign.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @Author: gaojy
 * @create 2022/4/26 11:44
 * @version: 1.0
 * @description:
 */
@FeignClient(name = BasConstants.SERVER_NAME,path= BasConstants.SERVER_NAME+"/pm/approvePush",url=BasConstants.SERVER_URL,configuration= FeignConfig.class)
public interface IPmApprovePushClient extends BaseClient<PmApprovePush> {
}
