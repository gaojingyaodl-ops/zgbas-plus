package com.spt.bas.client.remote;

import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.ApplyCompanyCredit;
import com.spt.bas.client.entity.ApplyCompanyLicense;
import com.spt.bas.client.entity.BsCompanyCredit;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.http.feign.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = BasConstants.SERVER_NAME, path = BasConstants.SERVER_NAME + "/apply/companyCredit", url = BasConstants.SERVER_URL, configuration = FeignConfig.class)
public interface IApplyCompanyCreditClient extends BaseClient<ApplyCompanyCredit> {
    @RequestMapping("/findByCompanyIdAndType")
    public ApplyCompanyCredit findByCompanyIdAndType(@RequestParam("type") String type,@RequestParam("companyId") Long companyId, @RequestParam("creditType") String creditType);
}
