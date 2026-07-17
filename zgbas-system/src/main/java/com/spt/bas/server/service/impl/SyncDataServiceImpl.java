package com.spt.bas.server.service.impl;


import com.spt.bas.client.entity.SyncData;
import com.spt.bas.server.dao.SyncDataDao;
import com.spt.bas.server.service.ISyncDataService;
import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.service.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SyncDataServiceImpl extends BaseService<SyncData> implements ISyncDataService {

    @Autowired
    private SyncDataDao syncDataDao;


    @Override
    public BaseDao<SyncData> getBaseDao() {
        return syncDataDao;
    }
}
