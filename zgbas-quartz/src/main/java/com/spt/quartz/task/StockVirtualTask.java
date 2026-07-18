package com.spt.quartz.task;

import com.spt.bas.server.stock.service.IStockVirtualService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Phase 6 (06-02) — ported from {@code com.spt.bas.server.task.StockVirtualTask}.
 * Bean name {@code "stockVirtualTask"} aligns with {@code sys_job.invoke_target}
 * short name {@code stockVirtualTask.autoDeleteStockVirtual}.
 */
@Component("stockVirtualTask")
@Slf4j
public class StockVirtualTask {

    @Autowired
    private IStockVirtualService stockVirtualService;

    /**
     * 定时任务，每天晚上定时清除过24小时，还未被使用的采购库存、销售负库存
     */
    public void autoDeleteStockVirtual() {
        // 自动更新私海客户没有成交单划入公海
        log.info("每天晚上定时清除过24小时，还未被使用的采购库存、销售负库存任务开始======>");
        stockVirtualService.autoDeleteStockVirtual();
        log.info("每天晚上定时清除过24小时，还未被使用的采购库存、销售负库存任务结束<======");
    }
}
