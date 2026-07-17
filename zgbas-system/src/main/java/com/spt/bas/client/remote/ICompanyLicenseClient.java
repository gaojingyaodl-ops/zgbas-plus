package com.spt.bas.client.remote;

import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.CompanyLicense;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.http.feign.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;


@FeignClient(name = BasConstants.SERVER_NAME, path = BasConstants.SERVER_NAME + "/company/license", url = BasConstants.SERVER_URL, configuration = FeignConfig.class)
public interface ICompanyLicenseClient extends BaseClient<CompanyLicense> {


    @GetMapping("/getCodeAndFileType")
    List<CompanyLicense> getCodeAndFileType(@RequestParam(name = "companyCode", required = false) String companyCode,
                                            @RequestParam(name = "fileType", required = false) String fileType);
}
