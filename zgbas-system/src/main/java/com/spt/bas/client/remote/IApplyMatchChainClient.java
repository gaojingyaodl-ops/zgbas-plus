package com.spt.bas.client.remote;

import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.ApplyMatchChain;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.http.feign.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * 代采赊销中间链条表
 * @Author: gaojy
 * @create 2022/9/14 10:46
 * @version: 1.0
 * @description:
 */
@FeignClient(name = BasConstants.SERVER_NAME, path = BasConstants.SERVER_NAME + "/apply/matchChain", url = BasConstants.SERVER_URL, configuration = FeignConfig.class)
public interface IApplyMatchChainClient extends BaseClient<ApplyMatchChain> {

    @PostMapping(value = "findMatchChains")
    List<ApplyMatchChain> findMatchChains(@RequestBody Long applyMatchId);
}
