package com.spt.bas.server.dao;

import com.spt.bas.client.entity.PiccSupplementInfo;
import com.spt.tools.jpa.dao.BaseDao;

public interface PiccSupplementInfoDao extends BaseDao<PiccSupplementInfo> {

    PiccSupplementInfo findByCompanyId(Long companyId);
}

