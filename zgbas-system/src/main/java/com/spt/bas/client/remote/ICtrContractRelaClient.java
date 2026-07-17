package com.spt.bas.client.remote;

import org.springframework.cloud.openfeign.FeignClient;

import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.CtrContractRela;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.http.feign.FeignConfig;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@FeignClient(name = BasConstants.SERVER_NAME,path= BasConstants.SERVER_NAME+"/ctr/contractRela",url=BasConstants.SERVER_URL,configuration=FeignConfig.class)
public interface ICtrContractRelaClient extends BaseClient<CtrContractRela> {

    /**
     * 通过sellContractId获取合同关联关系
     * @param contractId
     * @return
     */
    @PostMapping("getRelaBySellContractId")
    CtrContractRela getRelaBySellContractId(@RequestBody Long contractId);
}

