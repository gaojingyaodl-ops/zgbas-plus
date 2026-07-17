package com.spt.bas.server.service.impl;

import com.spt.bas.client.entity.LitigationCase;
import com.spt.bas.client.entity.PiccPayApply;
import com.spt.bas.server.dao.LitigationCaseDao;
import com.spt.bas.server.service.ILitigationCaseService;
import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.service.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LitigationCaseServiceImpl extends BaseService<LitigationCase> implements ILitigationCaseService {
    @Autowired
    private LitigationCaseDao litigationCaseDao;

    @Override
    public BaseDao<LitigationCase> getBaseDao() {
        return litigationCaseDao;
    }
}
