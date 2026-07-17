package com.spt.bas.server.api;

import com.spt.bas.client.entity.StockInventory;
import com.spt.bas.server.service.IStockInventoryService;
import com.spt.tools.core.bean.PageSearchVo;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.data.service.BaseApi;
import com.spt.tools.data.service.IDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author MoonLight
 * @Date 2024/8/20 13:53
 * @Version 1.0
 */
@RestController
@RequestMapping(value = "stock/inventory")
public class StockInventoryApi extends BaseApi<StockInventory> {

    @Autowired
    private IStockInventoryService stockInventoryService;

    @Override
    public IDataService<StockInventory> getService() {
        return stockInventoryService;
    }

    @PostMapping("/invalidInventory")
    public void invalidInventory(@RequestBody Long id){
        stockInventoryService.invalidInventory(id);
    }

    @PostMapping("/updateInventory")
    public void updateInventory(@RequestBody StockInventory stockVirtual) throws ApplicationException {
        stockInventoryService.updateInventory(stockVirtual);
    }
    @PostMapping("findInventoryVirtualPage")
    public Page<StockInventory> findInventoryVirtualPage(@RequestBody PageSearchVo searchVo){
        return stockInventoryService.findInventoryVirtualPage(searchVo);
    }

}
