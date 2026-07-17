package com.spt.bas.server.service.impl;

import com.spt.bas.client.entity.PiccLog;
import com.spt.bas.server.dao.PiccLogDao;
import com.spt.bas.server.service.IPiccLogService;
import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.service.BaseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class PiccLogServiceImpl extends BaseService<PiccLog> implements IPiccLogService {
    @Autowired
    private PiccLogDao piccLogDao;

    @Override
    public BaseDao<PiccLog> getBaseDao() {
        return piccLogDao;
    }




}
