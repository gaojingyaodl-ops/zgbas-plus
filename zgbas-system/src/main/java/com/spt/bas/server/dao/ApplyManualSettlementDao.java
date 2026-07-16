package com.spt.bas.server.dao;

import com.spt.bas.client.entity.ApplyManualSettlement;
import com.spt.tools.jpa.dao.BaseDao;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;

public interface ApplyManualSettlementDao extends BaseDao<ApplyManualSettlement> {

    @Transactional
    @Modifying
    @Query("update ApplyManualSettlement c set c.fileId =?2 where c.id=?1 ")
    void updateFileId(Long id, String fileId);
}
