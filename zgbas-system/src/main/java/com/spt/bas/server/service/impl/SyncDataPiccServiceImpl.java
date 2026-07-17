package com.spt.bas.server.service.impl;

import com.spt.bas.client.entity.SyncDataPicc;
import com.spt.bas.server.dao.SyncDataPiccDao;
import com.spt.bas.server.service.ISyncDataPiccService;
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
 * @Date: Created in 2021-03-03 10:50
 */
@Component
public class SyncDataPiccServiceImpl extends BaseService<SyncDataPicc> implements ISyncDataPiccService {
    @Autowired
    private SyncDataPiccDao syncDataPiccDao;

    @Override
    public BaseDao<SyncDataPicc> getBaseDao() {
        return syncDataPiccDao;
    }
}
