package com.spt.bas.client.remote;

import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.ApplyFactorSign;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.http.feign.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 签署保理申请
 */
@FeignClient(qualifier = "applyFactorSignClient", name = BasConstants.SERVER_NAME, path = BasConstants.SERVER_NAME + "/apply/factorSign", url = BasConstants.SERVER_URL, configuration = FeignConfig.class)
public interface IApplyFactorSignClient extends BaseClient<ApplyFactorSign> {
    @PostMapping(value = "applyFactorSign")
    void applyFactorSign(@RequestBody ApplyFactorSign factorSign) throws ApplicationException;
}
