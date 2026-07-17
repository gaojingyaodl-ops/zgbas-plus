package com.spt.bas.server.service;

import com.spt.bas.client.entity.ApplySell;
import com.spt.bas.client.vo.ApplySellVo;
import com.spt.bas.client.vo.ApproveFormPrintVo;
import com.spt.pm.entity.PmApprove;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.jpa.service.IBaseService;

public interface IApplySellService extends IBaseService<ApplySell> {

	void updateFileId(Long id, String fileId);

	void updateApplyStatus(Long contractId);

	ApplySell findByContractId(Long contractId);

	ApproveFormPrintVo printApplySell(Long applyId);

	/**
	 * 2021-3-1
	 * shaoanwei
	 * 自营销售预算
	 */
	void applySell(ApplySellVo applySellVo)throws ApplicationException;

	/**
	 * 自动生成盖章申请-自营销售预算
	 * @param applySell
	 * @param approve
	 */
	void autoInitiatedSealUsage(ApplySell applySell, PmApprove approve);
}

