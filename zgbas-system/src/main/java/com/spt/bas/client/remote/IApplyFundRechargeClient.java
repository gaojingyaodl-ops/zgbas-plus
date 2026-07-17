package com.spt.bas.client.remote;

import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.ApplyFundRecharge;
import com.spt.bas.client.vo.FileIdUpdateVo;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.http.feign.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @Author MoonLight
 * @Date 2024/7/15 9:38
 * @Version 1.0
 */
@FeignClient(name = BasConstants.SERVER_NAME, path = BasConstants.SERVER_NAME+"/apply/fundRecharge", url = BasConstants.SERVER_URL, configuration = FeignConfig.class)
public interface IApplyFundRechargeClient extends BaseClient<ApplyFundRecharge> {
    @PostMapping("updateFileId")
    void updateFileId(@RequestBody FileIdUpdateVo vo);
}
