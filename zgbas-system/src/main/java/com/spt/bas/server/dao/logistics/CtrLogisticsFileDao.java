package com.spt.bas.server.dao.logistics;

import com.spt.bas.client.entity.CtrLogisticsFile;
import com.spt.tools.jpa.dao.BaseDao;

import java.util.List;

/**
 * 合同物流提货表附件表
 *
 * @Author MoonLight
 * @Date 2023/7/5 16:14
 * @Version 1.0
 */
public interface CtrLogisticsFileDao extends BaseDao<CtrLogisticsFile> {
    CtrLogisticsFile findByLogisticsDeliveryIdAndFileType(Long logisticsDeliveryId, String fileType);
    
    List<CtrLogisticsFile> findByLogisticsIdAndLogisticsDeliveryId(Long logisticsId,Long logisticsDeliveryId);
}
