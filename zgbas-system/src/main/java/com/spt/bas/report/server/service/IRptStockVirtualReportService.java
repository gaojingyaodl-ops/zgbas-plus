package com.spt.bas.report.server.service;

import com.spt.bas.client.entity.StockVirtual;
import com.spt.bas.report.client.vo.RptStockVirtualSearchVo;
import com.spt.bas.report.client.vo.RptStockVirtualVo;
import com.spt.bas.report.client.vo.RptWxStockVirtualSearchVo;
import org.springframework.data.domain.Page;

/**
 * @author 杨英承
 * @version 1.0.0
 * @date 2022/5/11 11:28
 */

public interface IRptStockVirtualReportService {

    void addStockVirtual(StockVirtual stockVirtual);

    /**
     * 查询虚拟库存的数据
     * @param queryVo 查询参数
     * @return 查询结果
     */
    Page<RptStockVirtualVo> getStockVirtualList(RptStockVirtualSearchVo queryVo);

    Page<RptStockVirtualVo> getWxStockVirtualPage(RptWxStockVirtualSearchVo queryVo);
}
