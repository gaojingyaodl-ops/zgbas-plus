package com.spt.bas.server.dao;

import com.spt.bas.client.entity.ApplyInvalid;
import com.spt.tools.jpa.dao.BaseDao;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

/**
 * 作废申请
 *
 * @Author MoonLight
 * @Date 2023/9/11 14:33
 * @Version 1.0
 */
public interface ApplyInvalidDao extends BaseDao<ApplyInvalid> {

    @Modifying
    @Query("update ApplyInvalid c set c.fileId =?2 where c.id=?1 ")
    void updateFileId(Long id, String fileId);
}
