package com.spt.bas.report.server.service.impl;

import com.spt.bas.client.entity.StockVirtual;
import com.spt.bas.report.client.vo.RptStockVirtualSearchVo;
import com.spt.bas.report.client.vo.RptStockVirtualVo;
import com.spt.bas.report.client.vo.RptWxStockVirtualSearchVo;
import com.spt.bas.report.server.dao.RptStockVirtualMapper;
import com.spt.bas.report.server.service.IRptStockVirtualReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author 杨英承
 * @version 1.0.0
 * @date 2022/5/11 11:28
 */
@Service
public class RptStockVirtualReportServiceImpl implements IRptStockVirtualReportService {

    @Autowired
    private RptStockVirtualMapper stockVirtualMapper;

    @Override
    public void addStockVirtual(StockVirtual stockVirtual) {

    }

    /**
     * 查询虚拟库存的数据
     * @param queryVo 查询参数
     * @return 查询结果
     */
    @Override
    public Page<RptStockVirtualVo> getStockVirtualList(RptStockVirtualSearchVo queryVo) {
        // 查询数据
        List<RptStockVirtualVo> result = stockVirtualMapper.selectAssessment(queryVo);
        // 内存分页
        Pageable pageable = PageRequest.of(queryVo.getPage() - 1, queryVo.getRows());
        return new PageImpl<>(result, pageable, queryVo.getCount());
    }

    @Override
    public Page<RptStockVirtualVo> getWxStockVirtualPage(RptWxStockVirtualSearchVo queryVo) {
        // 查询数据
        List<RptStockVirtualVo> result = stockVirtualMapper.getWxStockVirtualPage(queryVo);
        // 内存分页
        Pageable pageable = PageRequest.of(queryVo.getPage() - 1, queryVo.getRows());
        return new PageImpl<>(result, pageable, queryVo.getCount());
    }
}
