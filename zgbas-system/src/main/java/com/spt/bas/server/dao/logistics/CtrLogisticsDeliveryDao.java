package com.spt.bas.server.dao.logistics;

import com.spt.bas.client.entity.CtrLogisticsDelivery;
import com.spt.tools.jpa.dao.BaseDao;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * 合同物流提货表
 *
 * @Author MoonLight
 * @Date 2023/7/5 16:14
 * @Version 1.0
 */
public interface CtrLogisticsDeliveryDao extends BaseDao<CtrLogisticsDelivery> {
    
    CtrLogisticsDelivery findByLogisticsIdAndLogisticsCount(Long logisticsId,String logisticsCount);
    
    List<CtrLogisticsDelivery> findByLogisticsId(Long logisticsId);

    @Query("delete from CtrLogisticsDelivery c where c.logisticsId =?1 and c.logisticsCount > ?2")
    void deleteAllLogisticsIdAndGtLogisticsCount(Long logisticsId,String logisticsCount);
    
}
