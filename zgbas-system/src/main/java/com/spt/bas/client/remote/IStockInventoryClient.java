package com.spt.bas.client.remote;

import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.StockInventory;
import com.spt.tools.core.bean.PageSearchVo;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.data.vo.PageDown;
import com.spt.tools.http.feign.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @Author MoonLight
 * @Date 2024/8/20 13:54
 * @Version 1.0
 */
@FeignClient(qualifier = "stockInventoryClient", name = BasConstants.SERVER_NAME, path = BasConstants.SERVER_NAME + "/stock/inventory", url = BasConstants.SERVER_URL, configuration = FeignConfig.class)
public interface IStockInventoryClient extends BaseClient<StockInventory> {

    @PostMapping("/invalidInventory")
    void invalidInventory(@RequestBody Long id);

    @PostMapping("/updateInventory")
    void updateInventory(@RequestBody StockInventory stockVirtual);

    @PostMapping("findInventoryVirtualPage")
    public PageDown<StockInventory> findInventoryVirtualPage(@RequestBody PageSearchVo searchVo);
}
