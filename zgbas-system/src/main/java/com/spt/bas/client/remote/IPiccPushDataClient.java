package com.spt.bas.client.remote;

import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.PiccPushData;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.http.feign.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;


/**
 * PICC推送报文接口
 */
@FeignClient(qualifier="piccPushDataClient", name = BasConstants.SERVER_NAME,path= BasConstants.SERVER_NAME+"/bs/piccPushData",url=BasConstants.SERVER_URL,configuration= FeignConfig.class)
public interface IPiccPushDataClient extends BaseClient<PiccPushData> {

    /**
     * 赊销发起记录
     *
     * @param contractNo
     * @return
     */
    @PostMapping("findContractNoSx")
    PiccPushData findContractNoSx(@RequestParam("contractNo") String contractNo);

    /**
     * 回款发起记录
     *
     * @param contractNo
     * @return
     */
    @PostMapping("findContractNoRecovery")
    PiccPushData findContractNoRecovery(@RequestParam("contractNo") String contractNo);
}
