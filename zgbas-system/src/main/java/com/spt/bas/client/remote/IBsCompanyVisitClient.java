package com.spt.bas.client.remote;

import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.BsCompanyQuota;
import com.spt.bas.client.entity.BsCompanyVisit;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.http.feign.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @Author 田起立
 * @Date 2024/6/3 14:30
 * @Description:
 */
@FeignClient(name = BasConstants.SERVER_NAME,path= BasConstants.SERVER_NAME+"/bs/companyVisit",url=BasConstants.SERVER_URL,configuration= FeignConfig.class)

public interface IBsCompanyVisitClient extends BaseClient<BsCompanyQuota> {
    @RequestMapping("getCompanyVisitById")
    public BsCompanyVisit getCompanyVisitById(@RequestParam("id") Long id);
}
