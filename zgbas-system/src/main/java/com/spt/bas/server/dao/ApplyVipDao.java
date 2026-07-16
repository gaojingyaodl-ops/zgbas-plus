package com.spt.bas.server.dao;

import com.spt.bas.client.entity.ApplyVip;
import com.spt.tools.jpa.dao.BaseDao;
import org.springframework.data.jpa.repository.Query;

public interface ApplyVipDao extends BaseDao<ApplyVip> {
    @Query(nativeQuery = true, value="SELECT  *  FROM `t_apply_vip` WHERE  company_id=?1  ORDER BY `created_date` DESC LIMIT 1")
    ApplyVip findvip(Long contractId);
}
