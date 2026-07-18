package com.spt.quartz.task;

import com.spt.bas.server.service.IApplyInternalBuyService;
import com.spt.tools.core.exception.ApplicationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Phase 6 (06-02) — ported from {@code com.spt.bas.server.task.InternalBuyTask}.
 * Bean name {@code "internalBuyTask"} aligns with {@code sys_job.invoke_target}
 * short name {@code internalBuyTask.internalBuyTask}.
 */
@Component("internalBuyTask")
public class InternalBuyTask{

	private static final Logger log = LoggerFactory.getLogger(InternalBuyTask.class);

	@Autowired
	private IApplyInternalBuyService applyInternalBuyService;
	/**
	 * 当天内部采购的库存若未销售则原路退回
	 * */
	public void internalBuyTask() {
		log.info("当天内部采购的库存若未销售则原路退回任务开始======>");
		try {
			applyInternalBuyService.doBackInternalBuy();
		} catch (ApplicationException e) {
			log.error("internalBuyTask error", e);
		}
	}
}
