package com.spt.quartz.task;

import com.spt.bas.server.service.IBsCompanyManageService;
import com.spt.bas.server.service.IBsCompanyService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Phase 6 (06-02) — ported from {@code com.spt.bas.server.task.BsCompanyTask}.
 * Bean name {@code "bsCompanyTask"} aligns with {@code sys_job.invoke_target}
 * short names {@code bsCompanyTask.updateCompanyGrey('${companyName}')},
 * {@code bsCompanyTask.refreshOwnerOfAccountId}, etc.
 */
@Component("bsCompanyTask")
@Slf4j
public class BsCompanyTask  {

	@Autowired
	private IBsCompanyService bsCompanyService;
	@Autowired
	private IBsCompanyManageService bsCompanyManageService;

	/**
	 * 定时任务，更新私海客户没有成交单则划入公海
	 */
	public void updateCompanyGrey(String companyName) {
		// 自动更新私海客户没有成交单划入公海
		log.info("自动更新私海客户没有成交单划入公海任务开始======>");
		bsCompanyManageService.updateStatusByTask(companyName);
		log.info("自动更新私海客户没有成交单划入公海任务结束<======");

		// 自动归入供应商灰名单与终端工厂灰名单
		log.info("自动归入供应商灰名单与终端工厂灰名单任务开始======>");
		bsCompanyService.updateGreyListByTask();
		log.info("自动归入供应商灰名单与终端工厂灰名单任务结束<======");
	}

	/**
	 * 开户人id 刷新历史数据
	 */
	public void refreshOwnerOfAccountId(){
		// 开户人id 刷新历史数据
		log.info("开户人id 刷新历史数据任务开始======>");
		bsCompanyService.updateOwnerOfAccountId();
		log.info("开户人id 刷新历史数据任务结束<======");
	}

	/**
	 * 超保额度到期自动恢复 -- 废弃
	 */
	public void recoverTotalCreditAmount(){
		// 开户人id 刷新历史数据
		log.info("超保额度到期自动恢复任务开始======>");
		bsCompanyService.recoverTotalCreditAmount();
		log.info("超保额度到期自动恢复任务结束<======");
	}

	/**
	 * 离职员工名下客户转移给各区域总
	 */
	public void leaveReleasePublic(){
		log.info("离职员工名下客户转移给各区域总任务开始======>");
		bsCompanyService.leaveReleasePublic();
		log.info("离职员工名下客户转移给各区域总任务结束<======");
	}


	/**
	 * 企业业务扩展表数据同步
	 */
	public void syncCompanyBusinessExpansion(){
		log.info("企业业务扩展表数据同步任务开始======>");
		bsCompanyService.syncCompanyBusinessExpansion();
		log.info("企业业务扩展表数据同步任务结束<======");
	}

	/**
	 * 恢复企业授信额度为人保批复额度 -- 废弃
	 */
	public void recoverCompanyCreditAmount(){
		log.info("恢复企业授信额度为人保批复额度任务开始======>");
		bsCompanyService.recoverCompanyCreditAmount();
		log.info("恢复企业授信额度为人保批复额度任务结束<======");
	}
}
