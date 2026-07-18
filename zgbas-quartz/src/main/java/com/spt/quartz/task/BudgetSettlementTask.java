package com.spt.quartz.task;

import com.spt.bas.server.service.IApplyReceiveService;
import com.spt.bas.server.service.IBsCompanyService;
import com.spt.bas.server.service.IBudgetSettlementService;
import com.spt.tools.core.exception.ApplicationException;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 预算结算任务
 * @author shengong
 *
 * <p>Phase 6 (06-02) — ported from {@code com.spt.bas.server.task.BudgetSettlementTask}.
 * Bean name {@code "budgetSettlementTask"} aligns with {@code sys_job.invoke_target}
 * short names {@code budgetSettlementTask.updateBudgetSettlement},
 * {@code budgetSettlementTask.updateBudgetSettlementByContractNo('${contractNo}')}, etc.
 */
@Component("budgetSettlementTask")
public class BudgetSettlementTask{
	private Logger logger = LoggerFactory.getLogger(BudgetSettlementTask.class);

	@Autowired
	private IBudgetSettlementService budgetSettlementService;

	@Autowired
	private IApplyReceiveService applyReceiveService;

	@Autowired
	private IBsCompanyService companyService;


	/**
	 * 定时任务，更新销售结算表
	 */
	public void updateBudgetSettlement() throws ApplicationException {
		logger.info("凌晨零点30分更新结算单======>");
		budgetSettlementService.doTask();
	}

	/**
	 * 手动执行，根据合同编号更新销售结算表
	 */
	public void updateBudgetSettlementByContractNo(String param) throws ApplicationException {
		logger.info("手动执行更新结算单======>");
		if(StringUtils.isNotBlank(param)) {
			budgetSettlementService.doTaskByContractNo(param);
		} else {
			logger.info("合同编号为空,执行任务请输入合同编号");
		}
	}

	/**
	 * 定时任务，更新vip剩余时长
	 */
	public void updateVipRemainingTime() throws ApplicationException {
		logger.info("更新vip剩余时长任务开始======>");
		companyService.doTask();
	}

	/**
	 * 定时任务，白条到期日9点自动发起收款审批
	 */
	public void applyReceive() throws ApplicationException {
//		logger.info("定时任务，白条到期日9点自动发起收款审批任务开始======>");
//		applyReceiveService.doApplyReceiveTask();
//		logger.info("定时任务，白条到期日9点自动发起收款审批任务结束<======");
	}

}
