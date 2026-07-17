package com.spt.bas.server.service.impl;

import com.spt.bas.client.entity.BsInitialQuota;
import com.spt.bas.server.dao.BsInitialQuotaDao;
import com.spt.bas.server.service.IBsInitialQuotaService;
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
 * @Date: Created in 2021-01-19 18:15
 */
@Component
public class BsInitialQuotaServiceImpl extends BaseService<BsInitialQuota> implements IBsInitialQuotaService {
    @Autowired
    private BsInitialQuotaDao bsInitialQuotaDao;

    @Override
    public BaseDao<BsInitialQuota> getBaseDao() {
        return bsInitialQuotaDao;
    }

}
