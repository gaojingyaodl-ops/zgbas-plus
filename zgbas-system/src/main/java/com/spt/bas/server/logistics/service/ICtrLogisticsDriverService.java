package com.spt.bas.server.logistics.service;

import com.spt.bas.client.entity.CtrLogisticsDriver;
import com.spt.tools.jpa.service.IBaseService;

import java.util.List;

/**
 * 合同物流提货司机表
 *
 * @Author MoonLight
 * @Date 2023/7/5 16:18
 * @Version 1.0
 */
public interface ICtrLogisticsDriverService extends IBaseService<CtrLogisticsDriver> {
    List<CtrLogisticsDriver> findByLogisticsIdAndLogisticsDeliveryId(Long logisticsId, Long logisticsDeliveryId);
}
