package com.spt.bas.server.dao;

import com.spt.bas.client.entity.ApplyInvoiceDelivery;
import com.spt.tools.jpa.dao.BaseDao;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface ApplyInvoiceDeliveryDao extends BaseDao<ApplyInvoiceDelivery> {
    @Modifying
    @Query("update ApplyInvoiceDelivery c set c.fileId =?2 where c.id=?1 ")
    void updateFileId(Long id, String fileId);
}
