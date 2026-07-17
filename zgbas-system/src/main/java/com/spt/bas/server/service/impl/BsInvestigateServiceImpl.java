package com.spt.bas.server.service.impl;

import com.spt.bas.client.entity.BsInvestigate;
import com.spt.bas.server.dao.BsInvesigateDao;
import com.spt.bas.server.service.IBsInvestigateService;
import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.service.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * <p>
 *
 * </p>
 *
 * @Author: shengong
 * @Date: Created in 2021-01-12 17:12
 */
@Component
public class BsInvestigateServiceImpl extends BaseService<BsInvestigate> implements IBsInvestigateService {
    @Autowired
    private BsInvesigateDao bsInvesigateDao;
    @Override
    public BaseDao<BsInvestigate> getBaseDao() {
        return bsInvesigateDao;
    }

    @Override
    public BsInvestigate findByCompanyId(Long companyId) {
        return bsInvesigateDao.findByCompanyId(companyId);
    }

}
