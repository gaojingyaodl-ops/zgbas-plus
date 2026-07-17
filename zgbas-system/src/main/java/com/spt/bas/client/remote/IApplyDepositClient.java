package com.spt.bas.client.remote;

import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.ApplyDeposit;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.http.feign.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(qualifier="applyDepositClient",name = BasConstants.SERVER_NAME,path= BasConstants.SERVER_NAME+"/apply/deposit",url=BasConstants.SERVER_URL,configuration= FeignConfig.class)
public interface IApplyDepositClient extends BaseClient<ApplyDeposit> {
}
