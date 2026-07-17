package com.spt.bas.server.service;

import com.spt.bas.client.entity.StockInventory;
import com.spt.tools.core.bean.PageSearchVo;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.jpa.service.IBaseService;
import org.springframework.data.domain.Page;

/**
 * @Author MoonLight
 * @Date 2024/8/20 13:50
 * @Version 1.0
 */
public interface IStockInventoryService extends IBaseService<StockInventory> {

    void invalidInventory(Long id);

    void updateInventory(StockInventory stockVirtual) throws ApplicationException;

    Page<StockInventory> findInventoryVirtualPage(PageSearchVo searchVo);
}
