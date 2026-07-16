package com.spt.bas.server.dao;

import com.spt.bas.client.entity.ApplyCompanyLicense;
import com.spt.tools.jpa.dao.BaseDao;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

/**
 * @author 杨英承
 * @version 1.0.0
 * @date 2024/3/22 11:27
 */

public interface ApplyCompanyLicenseDao extends BaseDao<ApplyCompanyLicense> {

    @Modifying
    @Query("update ApplyCompanyLicense a set a.fileId =?2 where a.id=?1 ")
    void updateFileId(Long id, String fileId);
}
