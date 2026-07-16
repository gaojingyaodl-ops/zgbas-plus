package com.spt.bas.server.dao;

import com.spt.bas.client.entity.BsCompanyQuota;
import com.spt.tools.jpa.dao.BaseDao;

import java.util.Date;
import java.util.List;

public interface BsCompanyQuotaDao extends BaseDao<BsCompanyQuota> {
    BsCompanyQuota findTopByCompanyIdOrderByIdDesc(Long companyId);
    
    List<BsCompanyQuota> findAllByCreatedDateBetween(Date beforeDate,Date newDate);
}
