package com.spt.bas.server.dao;

import com.spt.bas.client.entity.ApplyEntrust;
import com.spt.tools.jpa.dao.BaseDao;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ApplyEntrustDao extends BaseDao<ApplyEntrust> {

    @Query("from ApplyEntrust a where a.status = 'D' and a.companyName = ?1 ")
    List<ApplyEntrust> findIsHaveEntrustUserByCompanyName(String companyName);
}
