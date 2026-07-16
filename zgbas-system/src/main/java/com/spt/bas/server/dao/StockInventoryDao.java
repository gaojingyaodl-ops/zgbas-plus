package com.spt.bas.server.dao;

import com.spt.bas.client.entity.StockInventory;
import com.spt.tools.jpa.dao.BaseDao;

/**
 * @Author MoonLight
 * @Date 2024/8/20 13:49
 * @Version 1.0
 */
public interface StockInventoryDao extends BaseDao<StockInventory> {
    StockInventory findByStockVirtualNo(String inventoryVirtualId);
}
