package com.spt.bas.server.service.impl;


import com.spt.bas.client.entity.PartnerCompany;
import com.spt.bas.server.dao.PartnerCompanyDao;
import com.spt.bas.server.service.IPartnerCompanyService;
import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.service.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PartnerCompanyServiceImpl extends BaseService<PartnerCompany> implements IPartnerCompanyService {

    @Autowired
    private PartnerCompanyDao partnerCompanyDao;


    @Override
    public BaseDao<PartnerCompany> getBaseDao() {
        return partnerCompanyDao;
    }
}
