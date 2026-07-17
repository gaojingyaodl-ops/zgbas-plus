package com.spt.bas.server.logistics.service.impl;

import com.spt.bas.client.entity.CtrLogisticsDriver;
import com.spt.bas.server.dao.logistics.CtrLogisticsDriverDao;
import com.spt.bas.server.logistics.service.ICtrLogisticsDriverService;
import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.service.BaseService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * 合同物流提货司机
 *
 * @Author MoonLight
 * @Date 2023/7/5 16:24
 * @Version 1.0
 */
@Component
public class CtrLogisticsDriverServiceImpl extends BaseService<CtrLogisticsDriver> implements ICtrLogisticsDriverService {

    @Resource
    private CtrLogisticsDriverDao ctrLogisticsDriverDao;

    @Override
    public BaseDao<CtrLogisticsDriver> getBaseDao() {
        return ctrLogisticsDriverDao;
    }

    @Override
    public List<CtrLogisticsDriver> findByLogisticsIdAndLogisticsDeliveryId(Long logisticsId, Long logisticsDeliveryId) {
        return ctrLogisticsDriverDao.findByLogisticsIdAndLogisticsDeliveryId(logisticsId,logisticsDeliveryId);
    }
}
