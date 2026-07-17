package com.spt.bas.server.service;

import com.spt.bas.client.entity.ApplyCtrDCSX;
import com.spt.bas.client.entity.ApplyPay;
import com.spt.bas.client.entity.ApplyReceive;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.jpa.service.IBaseService;

import java.math.BigDecimal;


public interface IApplyDcsxReceiveService extends IBaseService<ApplyReceive> {


	/**
	 * 自动发起收款审批任务
	 */
	void doApplyDcsxReceiveTask(ApplyCtrDCSX ctrDCSX, ApplyPay pay);

	/**
	 * 定时任务自动发起收款审批任务
	 * @throws ApplicationException
	 */
	void ApplyDcsxReceiveTask(ApplyCtrDCSX ctrDCSX,int i) throws ApplicationException;


}

