package com.spt.bas.client.remote;


import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.ApplyInternalTransferMoney;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.http.feign.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = BasConstants.SERVER_NAME, path = BasConstants.SERVER_NAME + "/apply/internalTransferMoney", url = BasConstants.SERVER_URL, configuration = FeignConfig.class)
public interface IApplyInternalTransferMoneyClient extends BaseClient<ApplyInternalTransferMoney> {

}
