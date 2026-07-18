package com.spt.quartz.task;

import com.spt.bas.server.service.ICtrContractSettlementAmountService;
import com.spt.bas.server.service.ICtrContractSettlementService;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Phase 6 (06-02) — ported from {@code com.spt.bas.server.task.SettlementTask}.
 * Bean name {@code "settlementTask"} aligns with {@code sys_job.invoke_target}
 * short names {@code settlementTask.updateSettlementTask} and
 * {@code settlementTask.refreshBreachCommission('${contractNo}')}.
 */
@Component("settlementTask")
public class SettlementTask {
    private static final Logger log = LoggerFactory.getLogger(SettlementTask.class);

    @Autowired
    private ICtrContractSettlementService ctrContractSettlementService;
    @Autowired
    private ICtrContractSettlementAmountService settlementAmountService;

    /**
     * 定时任务，更新销售结算表
     */
    @SneakyThrows
    public void updateSettlementTask() {
        log.info("更新结算单任务开始======>");
        ctrContractSettlementService.refreshAllSettlement();
    }

    public void refreshBreachCommission(String contractNo) {
        log.info("补偿结算单收逾期罚息提成任务开始======>");
		if (StringUtils.isEmpty(contractNo)){
			throw new RuntimeException("合同编号参数为空，任务终止!");
		}
        log.info("param:{}", contractNo);
        settlementAmountService.refreshBreachCommission(contractNo);
    }

}
