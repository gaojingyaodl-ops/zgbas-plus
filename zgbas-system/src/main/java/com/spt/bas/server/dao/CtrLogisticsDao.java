package com.spt.bas.server.dao;

import com.spt.bas.client.entity.CtrLogistics;
import com.spt.tools.jpa.dao.BaseDao;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.List;

/**
 *
 */
public interface CtrLogisticsDao extends BaseDao<CtrLogistics> {

    @Modifying
    @Transactional
    @Query("update CtrLogistics c set c.warehouseId =?2,c.warehouseName=?3,c.supplierNo=?4 ,c.takeDelieveryAddr=?5,c.receiveDeliveryAddr=?6,c.logisticsDistance=?7 where c.id=?1")
    void updateCtrLogisticsInfo(Long id, Long warehouseId, String warehouseName, String supplierNo, String takeDelieveryAddr, String receiveDeliveryAddr, String logisticsDistance);

    @Query("from CtrLogistics c where c.buyContractId =?1 or c.sellContractId =?1")
    CtrLogistics findByContractId(Long contractId);

    List<CtrLogistics> findByBuyContractNo(String buyContractNo);

    List<CtrLogistics> findBySellContractNo(String sellContractNo);

    List<CtrLogistics> findByLogisticsNo(String logisticsNo);

    @Modifying
    @Transactional
    @Query("update CtrLogistics c set c.enableFlg = false where c.buyContractNo =?1 or c.sellContractNo =?1")
    void invalidLogistics(String contractNo);
}
