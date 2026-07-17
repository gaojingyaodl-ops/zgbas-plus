package com.spt.bas.server.service;

import com.spt.bas.client.entity.ApplyBuy;
import com.spt.bas.client.vo.ApplyBuyVo;
import com.spt.bas.client.vo.ApproveFormPrintVo;
import com.spt.pm.entity.PmApprove;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.jpa.service.IBaseService;

public interface IApplyBuyService extends IBaseService<ApplyBuy> {

	void updateFileId(Long id, String fileId);

	void updateApplyStatus(Long id);

	ApplyBuy findByContractId(Long contractId);

	public ApproveFormPrintVo printApplyBuy(Long applyId);

	/**
	 * 新建自营采购预算接口
	 * @param
	 */
	void applyBuy(ApplyBuyVo applyBuyVo)throws ApplicationException;

	/**
	 * 自动发起盖章申请
	 * @param applyBuy
	 * @param approve
	 */
	void autoInitiatedSealUsage(ApplyBuy applyBuy, PmApprove approve);
}

