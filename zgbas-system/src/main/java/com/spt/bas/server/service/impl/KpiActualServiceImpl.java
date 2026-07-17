package com.spt.bas.server.service.impl;

import com.spt.bas.client.entity.KpiActual;
import com.spt.bas.server.dao.KpiActualDao;
import com.spt.bas.server.service.IKpiActualService;
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
public class KpiActualServiceImpl extends BaseService<KpiActual> implements IKpiActualService {

    @Autowired
    private KpiActualDao kpiActualDao;

    @Override
    public BaseDao<KpiActual> getBaseDao() {
        return kpiActualDao;
    }
}
