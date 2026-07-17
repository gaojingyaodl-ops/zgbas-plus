package com.spt.bas.server.service;

import com.spt.bas.client.entity.CompanyLicense;
import com.spt.tools.jpa.service.IBaseService;

import java.util.List;

/**
 * @author 杨英承
 * @version 1.0.0
 * @date 2024/3/20 14:34
 */

public interface ICompanyLicenseService extends IBaseService<CompanyLicense> {
    List<CompanyLicense> getCodeAndFileType(String companyCode, String fileType);
}
