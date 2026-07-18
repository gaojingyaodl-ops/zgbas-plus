package com.spt.quartz.task;

import com.google.common.base.Stopwatch;
import com.spt.bas.server.service.IBusinessRestrictRelieveService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * Phase 6 (06-02) — ported from {@code com.spt.bas.server.task.BusinessRestrictRelieveTask}.
 * Bean name {@code "businessRestrictRelieveTask"} aligns with
 * {@code sys_job.invoke_target} short name
 * {@code businessRestrictRelieveTask.resetUsableCount}.
 */
@Component("businessRestrictRelieveTask")
public class BusinessRestrictRelieveTask {
    private static final Logger log = LoggerFactory.getLogger(BusinessRestrictRelieveTask.class);

    @Autowired
    private IBusinessRestrictRelieveService businessRestrictRelieveService;

    /**
     * 重置业务限制解除可用次数
     */
    public void resetUsableCount() {
        Stopwatch stopwatch = Stopwatch.createStarted();
        log.info("重置业务限制解除可用次数======>");
        businessRestrictRelieveService.resetUsableCount();
        log.info("重置业务限制解除可用次数任务结束耗时" + stopwatch.elapsed(TimeUnit.MILLISECONDS));
    }


}
