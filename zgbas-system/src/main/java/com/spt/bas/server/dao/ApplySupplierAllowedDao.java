package com.spt.bas.server.dao;

import com.spt.bas.client.entity.ApplySupplierAllowed;
import com.spt.tools.jpa.dao.BaseDao;

public interface ApplySupplierAllowedDao extends BaseDao<ApplySupplierAllowed> {
    ApplySupplierAllowed findTopByCompanyIdOrderByCreatedDateDesc(Long companyId);
}
