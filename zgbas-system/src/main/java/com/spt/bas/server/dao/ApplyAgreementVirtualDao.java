package com.spt.bas.server.dao;

import com.spt.bas.client.entity.ApplyAgreementVirtual;
import com.spt.tools.jpa.dao.BaseDao;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

/**
 * 协议采购申请
 * @Author: gaojy
 * @create 2024/08/19 14:15
 * @version: 1.0
 * @description:
 */
public interface ApplyAgreementVirtualDao extends BaseDao<ApplyAgreementVirtual> {
    @Modifying
    @Query("update ApplyAgreementVirtual c set c.contentTemplateId =?2 where c.id=?1 ")
    void updateContentTemplateId(Long id, String fileId);
}
