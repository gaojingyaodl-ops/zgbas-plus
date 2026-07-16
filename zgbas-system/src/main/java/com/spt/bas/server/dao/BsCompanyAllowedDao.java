package com.spt.bas.server.dao;

import com.spt.bas.client.entity.BsCompanyAllowed;
import com.spt.tools.jpa.dao.BaseDao;

public interface BsCompanyAllowedDao extends BaseDao<BsCompanyAllowed> {
    BsCompanyAllowed findTopByCompanyIdOrderByCreatedDateDesc(Long companyId);
}
