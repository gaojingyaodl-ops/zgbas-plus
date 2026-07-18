package com.spt.bas.report.server.api;

import com.spt.bas.client.entity.StockVirtual;
import com.spt.bas.report.client.vo.RptStockVirtualSearchVo;
import com.spt.bas.report.client.vo.RptStockVirtualVo;
import com.spt.bas.report.client.vo.RptWxStockVirtualSearchVo;
import com.spt.bas.report.server.service.IRptStockVirtualReportService;
import com.spt.tools.core.exception.ApplicationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 杨英承
 * @version 1.0.0
 * @date 2022/5/11 11:26
 */
@RestController
@RequestMapping(value = "/stockReport/virtual")
public class RptStockVirtualReportApi  {
    @Autowired
    private IRptStockVirtualReportService stockVirtualService;

    /**
     * 添加虚拟库存(虚拟采购库存/虚拟销售库存)
     * @param stockVirtual 虚拟库存实体
     */
    @PostMapping("/addStockVirtual")
    public void addStockVirtual(@RequestBody StockVirtual stockVirtual) throws ApplicationException {
        stockVirtualService.addStockVirtual(stockVirtual);
    }

    /**
     * 查询虚拟库存记录
     * @param queryVo 查询参数
     * @return 查询结果
     */
    @PostMapping("findPageVo")
    public Page<RptStockVirtualVo> getStockVirtualList(@RequestBody RptStockVirtualSearchVo queryVo){
        queryVo.setSort("publishTime");
        queryVo.setOrder("ASC");
        return stockVirtualService.getStockVirtualList(queryVo);
    }

    @PostMapping("getWxStockVirtualPage")
    public Page<RptStockVirtualVo> getWxStockVirtualPage(@RequestBody RptWxStockVirtualSearchVo queryVo){
        return stockVirtualService.getWxStockVirtualPage(queryVo);
    }



}
