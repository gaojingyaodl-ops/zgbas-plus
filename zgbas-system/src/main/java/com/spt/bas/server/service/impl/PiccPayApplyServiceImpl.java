package com.spt.bas.server.service.impl;

import com.spt.bas.client.entity.PiccPayApply;
import com.spt.bas.server.dao.PiccPayApplyDao;
import com.spt.bas.server.service.IPiccPayApplyService;
import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.service.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PiccPayApplyServiceImpl extends BaseService<PiccPayApply> implements IPiccPayApplyService {
    @Autowired
    private PiccPayApplyDao piccPayApplyDao;
    @Override
    public BaseDao<PiccPayApply> getBaseDao() {
        return piccPayApplyDao;
    }
}
