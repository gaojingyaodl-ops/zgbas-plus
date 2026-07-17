package com.spt.bas.client.remote;

import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.BsEntrust;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.http.feign.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = BasConstants.SERVER_NAME, path = BasConstants.SERVER_NAME+"/api/bsEntrust", url = BasConstants.SERVER_URL, configuration = FeignConfig.class)
public interface IBsEntrustClient extends BaseClient<BsEntrust> {

    @PostMapping(value = "findByWxUserId")
    BsEntrust findByWxUserId(@RequestBody Long wxUserId);

    @PostMapping(value = "findByCompanyId")
    List<BsEntrust> findByCompanyId(@RequestBody Long companyId);
}
