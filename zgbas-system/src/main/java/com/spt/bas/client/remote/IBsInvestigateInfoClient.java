package com.spt.bas.client.remote;

import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.BsInvestigate;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.http.feign.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * <p>
 * 企业实地调研信息
 * </p>
 *
 * @Author: shengong
 * @Date: Created in 2021-01-12 17:06
 */
@FeignClient(name = BasConstants.SERVER_NAME, path = BasConstants.SERVER_NAME + "/bs/investigateInfo", url = BasConstants.SERVER_URL, configuration = FeignConfig.class)
public interface IBsInvestigateInfoClient extends BaseClient<BsInvestigate> {
    @PostMapping(value = "findByCompanyId")
    BsInvestigate findByCompanyId(@RequestBody Long companyId);
}
