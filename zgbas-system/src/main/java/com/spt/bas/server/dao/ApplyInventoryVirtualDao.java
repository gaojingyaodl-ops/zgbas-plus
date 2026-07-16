package com.spt.bas.server.dao;

import com.spt.bas.client.entity.ApplyInventoryVirtual;
import com.spt.tools.jpa.dao.BaseDao;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

/**
 * 库存采购申请
 * @Author MoonLight
 * @Date 2024/8/20 11:04
 * @Version 1.0
 */
public interface ApplyInventoryVirtualDao extends BaseDao<ApplyInventoryVirtual> {

    @Modifying
    @Query("update ApplyInventoryVirtual c set c.contentTemplateId =?2 where c.id=?1 ")
    void updateContentTemplateId(Long id, String contentTemplateId);
}
