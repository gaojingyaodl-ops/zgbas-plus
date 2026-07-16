package com.spt.bas.server.dao;

import com.spt.bas.client.entity.ApplyMatters;
import com.spt.tools.jpa.dao.BaseDao;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface ApplyMattersDao extends BaseDao<ApplyMatters> {

    @Modifying
    @Query("update ApplyMatters a set a.fileId =?2 where a.id=?1 ")
    void updateFileId(Long id, String fileId);
}
