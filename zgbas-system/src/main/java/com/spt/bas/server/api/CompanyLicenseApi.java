package com.spt.bas.server.api;

import com.spt.bas.client.entity.CompanyLicense;
import com.spt.bas.server.service.ICompanyLicenseService;
import com.spt.tools.data.service.BaseApi;
import com.spt.tools.jpa.service.IBaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author 杨英承
 * @version 1.0.0
 * @date 2024/3/20 14:31
 */
@RestController
@RequestMapping(value = "/company/license")
public class CompanyLicenseApi extends BaseApi<CompanyLicense> {

    @Autowired
    private ICompanyLicenseService companyLicenseService;

    @Override
    public IBaseService<CompanyLicense> getService() {
        return companyLicenseService;
    }

    @GetMapping("/getCodeAndFileType")
    List<CompanyLicense> getCodeAndFileType(@RequestParam(name = "companyCode", required = false) String companyCode,
                                            @RequestParam(name = "fileType", required = false) String fileType) {
        return companyLicenseService.getCodeAndFileType(companyCode, fileType);
    }


}
