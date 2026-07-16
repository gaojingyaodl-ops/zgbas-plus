package com.spt.bas.server.dao;

import com.spt.bas.client.entity.StockVirtual;
import com.spt.tools.jpa.dao.BaseDao;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * @Author: gaojy
 * @create 2022/5/9 10:38
 * @version: 1.0
 * @description:
 */
public interface StockVirtualDao extends BaseDao<StockVirtual> {
    List<StockVirtual> findAllByVirtualStatus(String virtualStatus);

    @Modifying
    @Query("update StockVirtual c set c.fileId =?2 where c.id=?1 ")
    void updateFileId(Long id, String fileId);

    @Query("select count(s.id) from StockVirtual s where s.virtualStatus = 'N'")
    Long findEnableVirtualCount();

    @Query("from StockVirtual v where v.stockVirtualNo like %?1%")
    List<StockVirtual> findByStockVirtualNoLike(String stockVirtualNo);

    @Query("from StockVirtual v where v.stockVirtualNo =?1")
    StockVirtual findByStockVirtualNo(String stockVirtualNo);

    @Query("from StockVirtual v where v.bizApplyVirtualId =?1 and v.virtualBuyType =?2 and v.virtualStatus != 'C'")
    List<StockVirtual> findBizStockVirtual(Long id, String virtualBuyType);

    @Query("from StockVirtual v where v.bizApplyVirtualId =?1 and v.virtualBuyType =?2")
    List<StockVirtual> findAllBizStockVirtual(Long id, String virtualBuyType);
}
