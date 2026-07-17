package com.spt.bas.server.service;

import com.spt.bas.client.entity.ApplyCompanyLicense;
import com.spt.bas.client.entity.CompanyLicense;
import com.spt.tools.jpa.service.IBaseService;

import java.util.List;

/**
 * @author 杨英承
 * @version 1.0.0
 * @date 2024/3/22 15:15
 */

public interface IApplyCompanyLicenseService extends IBaseService<ApplyCompanyLicense> {
    void updateFileId(Long id, String fileId);

    List<CompanyLicense> downloadPicUrl(ApplyCompanyLicense license);
}
