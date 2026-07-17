package com.spt.bas.server.service.impl;

import com.spt.bas.client.entity.BsCompanyAllowedOphis;
import com.spt.bas.server.dao.BsCompanyAllowedOphisDao;
import com.spt.bas.server.service.IBsCompanyAllowedOphisService;
import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.service.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component("bsCompanyAllowedOphisService")
@Transactional(readOnly = true)
public class BsCompanyAllowedOphisServiceImpl extends BaseService<BsCompanyAllowedOphis> implements IBsCompanyAllowedOphisService {
    @Autowired
    private BsCompanyAllowedOphisDao bsCompanyAllowedOphisDao;

    @Override
    public BaseDao<BsCompanyAllowedOphis> getBaseDao() {
        return bsCompanyAllowedOphisDao;
    }
}
