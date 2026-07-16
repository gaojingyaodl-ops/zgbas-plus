package com.spt.bas.server.dao.sign;

import com.spt.bas.client.entity.BizSign;
import com.spt.tools.jpa.dao.BaseDao;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Author MoonLight
 * @Date 2024/10/28 11:45
 * @Version 1.0
 */
@Repository
public interface BizSignDao extends BaseDao<BizSign> {
    List<BizSign> findByApproveId(Long approveId);
}
