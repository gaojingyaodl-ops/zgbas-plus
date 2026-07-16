package com.spt.bas.server.dao;

import com.spt.bas.client.entity.ZhongYinSupplementInfo;
import com.spt.tools.jpa.dao.BaseDao;

public interface ZhongYinSupplementInfoDao extends BaseDao<ZhongYinSupplementInfo> {

    ZhongYinSupplementInfo findByCompanyId(Long companyId);
}

