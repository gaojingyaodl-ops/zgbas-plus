package com.spt.bas.server.logistics.service;

import com.spt.bas.client.constant.LogisticsEnum;
import com.spt.bas.client.entity.CtrLogisticsDelivery;
import com.spt.bas.client.entity.CtrLogisticsFile;
import com.spt.bas.client.vo.CtrLogisticsReqVo;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.jpa.service.IBaseService;

import java.util.List;
import java.util.Map;

/**
 * 合同物流提货表
 *
 * @Author MoonLight
 * @Date 2023/7/5 16:17
 * @Version 1.0
 */
public interface ICtrLogisticsDeliveryService extends IBaseService<CtrLogisticsDelivery> {

    /**
     * 创建指定类型物流单据-PDF
     *
     * @param logisticsDeliveryId
     * @param logisticsEnum
     */
    boolean generateLogisticsPdfFile(Long logisticsDeliveryId, LogisticsEnum logisticsEnum);

    /**
     * 根据物流单据明细，提货次数查询
     * @param logisticsId
     * @param logisticsCount
     * @return
     */
    CtrLogisticsDelivery findByLogisticsIdAndLogisticsCount(Long logisticsId,String logisticsCount);

    /**
     *
     * @param logisticsId
     * @return
     */
    List<CtrLogisticsDelivery> findByLogisticsId(Long logisticsId);

    CtrLogisticsFile generateDeliveryFile(CtrLogisticsReqVo reqVo) throws ApplicationException;

    CtrLogisticsFile generateLogisticsSealUsage(CtrLogisticsReqVo reqVo) throws ApplicationException;

    CtrLogisticsFile successLogisticsPdfFile(String ourCompanyName, String sealType, Long logisticsFileId, Long approveId, String approveNo);

    Map<String, String> exportExcelTemplate(CtrLogisticsReqVo reqVo) throws ApplicationException;
}
