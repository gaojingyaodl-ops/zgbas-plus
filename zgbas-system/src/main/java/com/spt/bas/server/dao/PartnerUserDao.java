package com.spt.bas.server.dao;

import com.spt.bas.client.entity.BsLog;
import com.spt.bas.client.entity.PartnerUser;
import com.spt.tools.jpa.dao.BaseDao;
import org.springframework.data.jpa.repository.Query;

public interface PartnerUserDao extends BaseDao<PartnerUser> {
    @Query(value = "from PartnerUser u where u.sysUserId = ?1 ")
    PartnerUser findByUserId(Long userId);
}

