package com.spt.bas.server.service.impl;

import com.spt.bas.client.entity.CompanyLicense;
import com.spt.bas.server.dao.CompanyLicenseDao;
import com.spt.bas.server.service.ICompanyLicenseService;
import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.service.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author 杨英承
 * @version 1.0.0
 * @date 2024/3/20 14:34
 */
@Service
public class CompanyLicenseServiceImpl extends BaseService<CompanyLicense> implements ICompanyLicenseService {

    @Autowired
    private CompanyLicenseDao companyLicenseDao;

    @Override
    public BaseDao<CompanyLicense> getBaseDao() {
        return companyLicenseDao;
    }

    @Override
    public List<CompanyLicense> getCodeAndFileType(String companyCode, String fileType) {
        return companyLicenseDao.getCodeAndFileType(companyCode, fileType);
    }
}
