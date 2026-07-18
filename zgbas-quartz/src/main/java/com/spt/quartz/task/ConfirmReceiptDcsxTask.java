package com.spt.quartz.task;

import com.spt.bas.server.service.IApplyConfrimReceiptDcsxService;
import com.spt.bas.server.service.ICtrContractProfitService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutionException;

/**
 * 中游确认收货
 *
 * <p>Phase 6 (06-02) — ported from {@code com.spt.bas.server.task.ConfirmReceiptDcsxTask}.
 * Bean name {@code "confirmReceiptDcsxTask"} aligns with {@code sys_job.invoke_target}
 * short name {@code confirmReceiptDcsxTask.initHistoryConfirmReceiptDcsx}.
 */
@Component("confirmReceiptDcsxTask")
public class ConfirmReceiptDcsxTask {

    private static final Logger log = LoggerFactory.getLogger(ConfirmReceiptDcsxTask.class);

    @Autowired
    private IApplyConfrimReceiptDcsxService confrimReceiptDcsxService;

    /**
     * 历史数据生成中游确认收货单
     *
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public void initHistoryConfirmReceiptDcsx() throws ExecutionException, InterruptedException {
        log.info("历史数据生成中游确认收货单任务开始======>");
        confrimReceiptDcsxService.initHistoryConfirmReceiptDcsx();
        log.info("历史数据生成中游确认收货单任务结束<======");
    }
}
