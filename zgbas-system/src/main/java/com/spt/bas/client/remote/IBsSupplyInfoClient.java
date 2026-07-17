package com.spt.bas.client.remote;

import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.BsSupplyInfo;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.http.feign.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = BasConstants.SERVER_NAME,path= BasConstants.SERVER_NAME+"/bs/supplyInfo",url=BasConstants.SERVER_URL,configuration= FeignConfig.class)
public interface IBsSupplyInfoClient extends BaseClient<BsSupplyInfo> {

    @PostMapping(value = "findByWxUserId")
    BsSupplyInfo findByWxUserId(@RequestBody Long wxUserId);

    @PostMapping(value = "findByCompanyId")
    BsSupplyInfo findByCompanyId(@RequestBody Long companyId);

}
