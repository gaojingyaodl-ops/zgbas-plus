package com.spt.quartz.task;

import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.ApplyCtrDCSX;
import com.spt.bas.server.service.IApplyDcsxService;
import com.spt.bas.server.service.IApplyReceiveService;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * 代采赊销自动发起付款申请
 *
 * <p>Phase 6 (06-02) — ported from {@code com.spt.bas.server.task.DcsxAutoApplyPayTask}.
 * Bean name {@code "dcsxAutoApplyPayTask"} aligns with {@code sys_job.invoke_target}
 * short name {@code dcsxAutoApplyPayTask.autoHb60DayNotApplyDcsxPay}.
 */
@Component("dcsxAutoApplyPayTask")
public class DcsxAutoApplyPayTask {

    private static final Logger log = LoggerFactory.getLogger(DcsxAutoApplyPayTask.class);

    @Autowired
    private IApplyReceiveService applyReceiveService;
    @Autowired
    private IApplyDcsxService applyDcsxService;

    /**
     * 自动发起原阳鸿博60天内未申请代采赊销付款申请
     *
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public void autoHb60DayNotApplyDcsxPay() throws ExecutionException, InterruptedException {
        log.info("自动发起原阳鸿博60天内未申请代采赊销付款申请任务开始======>");
        applyReceiveService.autoApplyDcsxPayScheduled();
        log.info("自动发起原阳鸿博60天内未申请代采赊销付款申请任务结束<======");
    }
}
