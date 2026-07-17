package com.spt.bas.server.service.impl;

import com.spt.bas.client.entity.*;
import com.spt.bas.server.dao.CompanyBusinessExpansionDao;
import com.spt.bas.server.service.ICompanyBusinessExpansionService;
import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.service.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


@Component("companyBusinessExpansionService")
@Transactional(readOnly = true)
public class CompanyBusinessExpansionServiceImpl extends BaseService<CompanyBusinessExpansion> implements ICompanyBusinessExpansionService {

    @Autowired
    private CompanyBusinessExpansionDao companyBusinessExpansionDao;
    @Override
    public BaseDao<CompanyBusinessExpansion> getBaseDao() {
        return companyBusinessExpansionDao;
    }
}
