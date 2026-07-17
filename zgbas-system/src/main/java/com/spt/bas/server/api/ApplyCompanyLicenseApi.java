package com.spt.bas.server.api;

import com.spt.bas.client.entity.ApplyCompanyLicense;
import com.spt.bas.client.entity.CompanyLicense;
import com.spt.bas.client.vo.FileIdUpdateVo;
import com.spt.bas.server.service.IApplyCompanyLicenseService;
import com.spt.tools.data.service.BaseApi;
import com.spt.tools.jpa.service.IBaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author 杨英承
 * @version 1.0.0
 * @date 2024/3/22 15:15
 */
@RestController
@RequestMapping(value = "/apply/companyLicense")
public class ApplyCompanyLicenseApi extends BaseApi<ApplyCompanyLicense> {

    @Autowired
    private IApplyCompanyLicenseService companyLicenseService;

    @Override
    public IBaseService<ApplyCompanyLicense> getService() {
        return companyLicenseService;
    }

    @PostMapping("/updateFileId")
    public void updateFileId(FileIdUpdateVo vo)
    {
        companyLicenseService.updateFileId(vo.getId(), vo.getFileId());
    }

    @PostMapping("/downloadPicUrl")
    List<CompanyLicense> downloadPicUrl(@RequestBody ApplyCompanyLicense license){
        return companyLicenseService.downloadPicUrl(license);
    }
}
