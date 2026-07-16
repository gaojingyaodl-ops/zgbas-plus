package com.spt.bas.server.dao;

import com.spt.bas.client.entity.ApplyProtocolDocument;
import com.spt.tools.jpa.dao.BaseDao;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

/**
 * 协议文件
 * @Author MoonLight
 * @Date 2024/5/24 9:35
 * @Version 1.0
 */
public interface ApplyProtocolDocumentDao extends BaseDao<ApplyProtocolDocument> {
    @Modifying
    @Query("update ApplyProtocolDocument a set a.fileId =?2 where a.id=?1 ")
    void updateFileId(Long id, String fileId);
}
