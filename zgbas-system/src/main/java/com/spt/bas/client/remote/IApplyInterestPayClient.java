package com.spt.bas.client.remote;

import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.ApplyInterestPay;
import com.spt.bas.client.vo.FileIdUpdateVo;
import com.spt.bas.client.vo.api.RespVo;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.http.feign.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@FeignClient(qualifier = "applyInterestPayClient", name = BasConstants.SERVER_NAME, path = BasConstants.SERVER_NAME + "/apply/interestPay", url = BasConstants.SERVER_URL, configuration = FeignConfig.class)
public interface IApplyInterestPayClient extends BaseClient<ApplyInterestPay> {

    @PostMapping("updateFileId")
    void updateFileId(@RequestBody FileIdUpdateVo vo);

    @PostMapping("batchPayInterest")
    RespVo<String> batchPayInterest(@RequestBody ApplyInterestPay entity);
}

