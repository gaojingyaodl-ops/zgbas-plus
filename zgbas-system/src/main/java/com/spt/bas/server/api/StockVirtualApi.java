package com.spt.bas.server.api;

import com.spt.bas.client.entity.StockVirtual;
import com.spt.bas.client.vo.FileIdUpdateVo;
import com.spt.bas.server.stock.service.IStockVirtualService;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.data.service.BaseApi;
import com.spt.tools.jpa.service.IBaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: gaojy
 * @create 2022/5/9 10:41
 * @version: 1.0
 * @description:
 */
@RestController
@RequestMapping(value = "stock/virtual")
public class StockVirtualApi extends BaseApi<StockVirtual> {
    @Autowired
    private IStockVirtualService stockVirtualService;

    @Override
    public IBaseService<StockVirtual> getService() {
        return stockVirtualService;
    }

    @PostMapping("updateFileId")
    public void updateFileId(@RequestBody FileIdUpdateVo vo) {
        stockVirtualService.updateFileId(vo.getId(), vo.getFileId());
    }

    @PostMapping("existEnableVirtual")
    public boolean existEnableVirtual(){
        return stockVirtualService.existEnableVirtual();
    }

    @PostMapping("updateStockVirtual")
    public void updateStockVirtual(@RequestBody StockVirtual stockVirtual){
        stockVirtualService.updateStockVirtual(stockVirtual);
    }

    @PostMapping("invalidStockVirtual")
    public void invalidStockVirtual(@RequestBody Long stockVirtualId) throws ApplicationException {
        stockVirtualService.invalidStockVirtual(stockVirtualId);
    }
}
