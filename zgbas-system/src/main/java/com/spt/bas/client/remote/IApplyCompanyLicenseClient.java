package com.spt.bas.client.remote;

import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.ApplyCompanyLicense;
import com.spt.bas.client.entity.CompanyLicense;
import com.spt.bas.client.vo.FileIdUpdateVo;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.http.feign.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;


@FeignClient(name = BasConstants.SERVER_NAME, path = BasConstants.SERVER_NAME + "/apply/companyLicense", url = BasConstants.SERVER_URL, configuration = FeignConfig.class)
public interface IApplyCompanyLicenseClient extends BaseClient<ApplyCompanyLicense> {


    @PostMapping("/updateFileId")
    void updateFileId(FileIdUpdateVo vo);

    @PostMapping("/downloadPicUrl")
    List<CompanyLicense> downloadPicUrl(@RequestBody ApplyCompanyLicense license);
}
