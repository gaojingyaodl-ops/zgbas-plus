package com.spt.bas.server.dao;

import com.spt.bas.client.entity.BsIncreaseInfo;
import com.spt.tools.jpa.dao.BaseDao;

/**
 * @author shengong
 */
public interface BsIncreaseInfoDao extends BaseDao<BsIncreaseInfo> {
    BsIncreaseInfo findByCompanyId(Long companyId);
}
