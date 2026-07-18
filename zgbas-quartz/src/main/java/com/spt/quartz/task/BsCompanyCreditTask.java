package com.spt.quartz.task;

import com.spt.bas.server.service.IBsCompanyCreditService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**
 * Phase 6 (06-02) — ported from {@code com.spt.bas.server.task.BsCompanyCreditTask}.
 * Bean name {@code "bsCompanyCreditTask"} aligns with {@code sys_job.invoke_target}
 * short names {@code bsCompanyCreditTask.recoverTemporaryAmount} and
 * {@code bsCompanyCreditTask.initCompanyCredit}.
 */
@Component("bsCompanyCreditTask")
@Slf4j
public class BsCompanyCreditTask {

    @Autowired
    private IBsCompanyCreditService companyCreditService;

    /**
     * 临时额度到期自动恢复
     */
    public void recoverTemporaryAmount() {
        log.info("临时额度到期自动恢复任务开始======>");
        companyCreditService.recoverTemporaryAmount();
        log.info("临时额度到期自动恢复任务结束<======");
    }

    /**
     * 初始化企业授信额度
     */
    public void initCompanyCredit() {
        log.info("初始化企业授信额度任务开始======>");
        companyCreditService.initCompanyCredit();
        log.info("初始化企业授信额度务结束<======");
    }
}
