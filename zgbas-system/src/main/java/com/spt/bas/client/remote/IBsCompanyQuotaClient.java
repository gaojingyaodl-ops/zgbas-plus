package com.spt.bas.client.remote;

import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.BsCompanyQuota;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.http.feign.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = BasConstants.SERVER_NAME,path= BasConstants.SERVER_NAME+"/bs/companyQuota",url=BasConstants.SERVER_URL,configuration= FeignConfig.class)
public interface IBsCompanyQuotaClient extends BaseClient<BsCompanyQuota> {

    /**
     * 获取最新完成了的额度申请
     * @param companyId
     * @return
     */
    @PostMapping("getLatestApply")
    BsCompanyQuota getLatestApply(@RequestBody Long companyId);
}
