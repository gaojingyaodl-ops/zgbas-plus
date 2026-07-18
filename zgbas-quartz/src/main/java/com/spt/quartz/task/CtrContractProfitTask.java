package com.spt.quartz.task;

import com.spt.bas.server.service.ICtrContractProfitService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutionException;

/**
 * 风控利润统计定时任务
 *
 * @Author MoonLight
 * @Date 2023/3/6 13:48
 * @Version 1.0
 *
 * <p>Phase 6 (06-02) — ported from {@code com.spt.bas.server.task.CtrContractProfitTask}.
 * Bean name {@code "ctrContractProfitTask"} aligns with {@code sys_job.invoke_target}
 * short names {@code ctrContractProfitTask.initHistoryProfit} and
 * {@code ctrContractProfitTask.refreshProfitData('${approveNo}')}.
 */
@Component("ctrContractProfitTask")
public class CtrContractProfitTask {

    private static final Logger log = LoggerFactory.getLogger(CtrContractProfitTask.class);

    @Autowired
    private ICtrContractProfitService ctrContractProfitService;

    /**
     * 风控利润统计历史数据入库
     *
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public void initHistoryProfit() throws ExecutionException, InterruptedException {
        log.info("风控利润统计历史数据入库任务开始======>");
        ctrContractProfitService.initHistoryProfit();
        log.info("风控利润统计历史数据入库任务结束<======");
    }

    /**
     * 风控利润统计数据更新
     */
    public void refreshProfitData(String approveNo) throws Exception {
        log.info("approveNo:{}", approveNo);
        log.info("风控利润统计数据更新任务开始======>");
        ctrContractProfitService.refreshProfitData(approveNo);
        log.info("风控利润统计数据更新任务结束<======");
    }
}
