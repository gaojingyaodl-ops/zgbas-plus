package com.spt.bas.server.dao;

import com.spt.bas.client.entity.CompanyLicense;
import com.spt.tools.jpa.dao.BaseDao;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * @author 杨英承
 * @version 1.0.0
 * @date 2024/3/20 14:37
 */

public interface CompanyLicenseDao extends BaseDao<CompanyLicense> {
    @Query("from CompanyLicense a where a.companyCode = ?1 and a.fileType = ?2")
    List<CompanyLicense> getCodeAndFileType(String companyCode, String fileType);
}
