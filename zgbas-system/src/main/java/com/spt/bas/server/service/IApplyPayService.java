package com.spt.bas.server.service;

import com.spt.bas.client.entity.ApplyPay;
import com.spt.bas.client.entity.CtrContract;
import com.spt.pm.entity.PmApprove;
import com.spt.pm.vo.PmApproveSaveVo;
import com.spt.pm.vo.PmApproveWithdrawVo;
import com.spt.tools.core.bean.PageSearchVo;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.jpa.service.IBaseService;

import java.util.List;

public interface IApplyPayService extends IBaseService<ApplyPay> {
	void updateFileId(Long id, String fileId);

	List<ApplyPay> findByContractId(Long contractId);

	void updateApplyStatus(Long contractId);

	ApplyPay findPageSum(PageSearchVo searchVo);

	void doWithdraw(PmApproveWithdrawVo pwVo) throws ApplicationException;

	void rollbackContractApply(ApplyPay entity) throws ApplicationException;

	void updateCtrContractApply(ApplyPay pay, CtrContract contract, Long processId) throws ApplicationException;

	ApplyPay findApplyPayByContractNo(String contractNo);

	/**
	 * 按批次批量发起付款申请
	 * @param saveVo
	 * @return
	 */
	PmApprove startBatchPayApply(PmApproveSaveVo saveVo) throws ApplicationException;

	ApplyPay getBrushBrushAmount(ApplyPay applyPay);

	void autoInitiatedCompleteFLKDcsxPay(CtrContract contract) throws ApplicationException;
}

