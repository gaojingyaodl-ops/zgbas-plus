package com.spt.bas.server.service.impl;

import com.spt.bas.client.entity.*;
import com.spt.bas.server.dao.PiccSupplementInfoDao;
import com.spt.bas.server.service.*;
import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.service.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


@Component
@Transactional(readOnly = true)
public class PiccSupplementInfoServiceImpl extends BaseService<PiccSupplementInfo> implements IPiccSupplementInfoService {

    @Autowired
    private PiccSupplementInfoDao piccSupplementInfoDao;
    
    @Override
    public BaseDao<PiccSupplementInfo> getBaseDao() {
        return piccSupplementInfoDao;
    }

    @Override
    public PiccSupplementInfo findByCompanyId(Long companyId) {
        return piccSupplementInfoDao.findByCompanyId(companyId);
    }
}

