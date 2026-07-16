package com.spt.bas.server.dao.fund;

import com.spt.bas.client.entity.ApplyFundRecharge;
import com.spt.tools.jpa.dao.BaseDao;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;

/**
 * @Author MoonLight
 * @Date 2024/7/12 16:20
 * @Version 1.0
 */
public interface ApplyFundRechargeDao extends BaseDao<ApplyFundRecharge> {

    @Modifying
    @Transactional
    @Query("update ApplyFundRecharge f set f.fileId =?2 where f.id=?1 ")
    void updateFileId(Long id, String fileId);
}
