package com.spt.bas.server.dao;

import com.spt.bas.client.entity.ApplyBusinessRestrictRelieve;
import com.spt.tools.jpa.dao.BaseDao;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface ApplyBusinessRestrictRelieveDao extends BaseDao<ApplyBusinessRestrictRelieve> {

    @Modifying
    @Query("update ApplyBusinessRestrictRelieve a set a.fileId =?2 where a.id=?1 ")
    void updateFileId(Long id, String fileId);
}
