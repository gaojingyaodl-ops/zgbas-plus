package com.spt.bas.server.dao;

import com.spt.bas.client.entity.BsInvestigate;
import com.spt.tools.jpa.dao.BaseDao;

public interface BsInvesigateDao extends BaseDao<BsInvestigate> {
    BsInvestigate findByCompanyId(Long companyId);
}
