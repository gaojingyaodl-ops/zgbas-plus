package com.spt.bas.server.logistics.service;

import com.spt.bas.client.entity.CtrLogisticsFile;
import com.spt.bas.client.vo.CtrLogisticsFileRespVo;
import com.spt.tools.jpa.service.IBaseService;

/**
 * 合同物流提货表附件表
 *
 * @Author MoonLight
 * @Date 2023/7/5 16:18
 * @Version 1.0
 */
public interface ICtrLogisticsFileService extends IBaseService<CtrLogisticsFile> {

    CtrLogisticsFile saveLogisticsFile(CtrLogisticsFile logisticsFile);
    
    CtrLogisticsFileRespVo findByLogisticsIdAndLogisticsDeliveryId(Long logisticsId,Long logisticsDeliveryId);
    
}
