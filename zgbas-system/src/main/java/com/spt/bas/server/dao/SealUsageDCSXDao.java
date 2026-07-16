package com.spt.bas.server.dao;


import com.spt.bas.client.entity.SealUsageDCSX;
import com.spt.tools.jpa.dao.BaseDao;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SealUsageDCSXDao extends BaseDao<SealUsageDCSX> {
    @Modifying
    @Query("update SealUsageDCSX c set c.fileId =?2 where c.id=?1 ")
    void updateFileId(Long id, String fileId);

    @Query("from SealUsageDCSX s where s.contractNo = ?1")
    List<SealUsageDCSX> findSealUsageDcsxByContractNo(String contractNo);
}
