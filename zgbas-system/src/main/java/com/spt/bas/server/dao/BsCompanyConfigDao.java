package com.spt.bas.server.dao;

import com.spt.bas.client.entity.BsCompanyConfig;
import com.spt.tools.jpa.dao.BaseDao;

/**
 * @Author: gaojy
 * @create 2022/4/2 11:02
 * @version: 1.0
 * @description:
 */
public interface BsCompanyConfigDao extends BaseDao<BsCompanyConfig> {
    BsCompanyConfig findByBsCompanyIdAndMatchUserId(Long bsCompanyId, Long matchUserId);

    BsCompanyConfig findByBsCompanyId(Long bsCompanyId);
}
