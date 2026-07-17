package com.spt.bas.client.remote;

import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.ClaimRecovery;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.http.feign.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = BasConstants.SERVER_NAME,path= BasConstants.SERVER_NAME+"/bs/claimRecovery",url=BasConstants.SERVER_URL,configuration= FeignConfig.class)
public interface IClaimRecoveryClient extends BaseClient<ClaimRecovery> {
    @PostMapping("findBycorpSerialNo")
    ClaimRecovery findBycorpSerialNo(@RequestBody String corpSerialNo);
}
