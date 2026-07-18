package com.spt.quartz.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Phase 6 (06-02) — ported from {@code com.spt.bas.server.task.DepositPaymentTask}.
 * Source had no xxl-job handler method (placeholder class, empty body);
 * preserved for historical bean wiring compatibility. Bean name
 * {@code "depositPaymentTask"} reserved for future sys_job rows.
 */
@Component("depositPaymentTask")
public class DepositPaymentTask {
    private Logger logger = LoggerFactory.getLogger(BudgetSettlementTask.class);
}
