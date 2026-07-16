package com.spt.bas.server.dao.logistics;

import com.spt.bas.client.entity.CtrLogisticsDriver;
import com.spt.tools.jpa.dao.BaseDao;

import java.util.List;

/**
 * 合同物流提货司机表
 *
 * @Author MoonLight
 * @Date 2023/7/5 16:14
 * @Version 1.0
 */
public interface CtrLogisticsDriverDao extends BaseDao<CtrLogisticsDriver> {

    List<CtrLogisticsDriver> findByLogisticsDeliveryId(Long logisticsDeliveryId);

    List<CtrLogisticsDriver> findByLogisticsIdAndLogisticsDeliveryId(Long logisticsId, Long logisticsDeliveryId);
}
