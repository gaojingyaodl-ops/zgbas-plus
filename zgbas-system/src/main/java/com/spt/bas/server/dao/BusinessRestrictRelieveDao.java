package com.spt.bas.server.dao;

import com.spt.bas.client.entity.BusinessRestrictRelieve;
import com.spt.tools.jpa.dao.BaseDao;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;


public interface BusinessRestrictRelieveDao extends BaseDao<BusinessRestrictRelieve> {

    @Modifying
    @Query("update BusinessRestrictRelieve a set a.usableCount =?2 where a.id=?1 ")
    void updateUsableCount(Long id, Integer usableCount);

    @Query("from BusinessRestrictRelieve b where b.companyId=?1 and b.userId=?2 ")
    BusinessRestrictRelieve findByCompanyIdAndAndUserId(Long companyId, Long userId);

    @Modifying
    @Query("update BusinessRestrictRelieve a set a.usableCount = 0")
    void resetUsableCount();
}
