package com.spt.bas.server.stock.service;

import com.spt.bas.client.entity.ApplyMatch;
import com.spt.bas.client.entity.ApplyMatchDetail;
import com.spt.bas.client.entity.StockVirtual;
import com.spt.pm.entity.PmApprove;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.jpa.service.IBaseService;

import java.util.List;

/**
 * @Author: gaojy
 * @create 2022/5/9 10:36
 * @version: 1.0
 * @description:
 */
public interface IStockVirtualService extends IBaseService<StockVirtual> {
    void updateStockVirtualStatus(Long id, String status);

    /**
     * 定时自动清理大于24h未使用的
     */
    void autoDeleteStockVirtual();

    void bindStockVirtual(PmApprove approve, ApplyMatch match, List<ApplyMatchDetail> matchDetailList);

    void updateFileId(Long id, String fileId);

    boolean existEnableVirtual();

    void updateStockVirtual(StockVirtual stockVirtual);

    void invalidStockVirtual(Long stockVirtualId) throws ApplicationException;
}
