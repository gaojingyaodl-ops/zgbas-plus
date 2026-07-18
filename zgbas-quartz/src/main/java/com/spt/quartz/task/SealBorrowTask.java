package com.spt.quartz.task;

import com.spt.bas.client.entity.ApplyDeliveryOut;
import com.spt.bas.report.client.entity.RptConfirmReceiptDetail;
import com.spt.bas.report.client.entity.RptConfirmReceiptVo;
import com.spt.bas.server.service.ISealBorrowService;
import com.spt.sign.client.entity.SignContract;
import com.spt.sign.client.entity.SignInfo;
import com.spt.tools.data.annotation.ServiceTransactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Phase 6 (06-02) — ported from {@code com.spt.bas.server.task.SealBorrowTask}.
 * Bean name {@code "sealBorrowTask"} aligns with {@code sys_job.invoke_target}
 * short name {@code sealBorrowTask.updateSealBorrow}.
 */
@Component("sealBorrowTask")
public class SealBorrowTask {

	private static final Logger log = LoggerFactory.getLogger(SealBorrowTask.class);

	@Autowired
	private ISealBorrowService sealBorrowService;
	/**
	 * 定时任务，更新逾期印章外借状态
	 */
	public void updateSealBorrow() {
		log.info("更新逾期印章外借状态任务开始======>");
		sealBorrowService.doSealBorrowTask();
	}




}
