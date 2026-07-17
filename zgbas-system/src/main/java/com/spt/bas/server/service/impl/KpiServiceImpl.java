package com.spt.bas.server.service.impl;

import com.spt.bas.client.entity.Kpi;
import com.spt.bas.server.dao.KpiDao;
import com.spt.bas.server.service.IKpiService;
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
 * @Date: Created in 2021-05-10 13:48
 */
@Component
public class KpiServiceImpl extends BaseService<Kpi> implements IKpiService {

    @Autowired
    private KpiDao kpiDao;

    @Override
    public BaseDao<Kpi> getBaseDao() {
        return kpiDao;
    }
}
