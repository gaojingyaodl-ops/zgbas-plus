package com.spt.bas.server.service;

import com.spt.bas.client.entity.ApplyInvoice;
import com.spt.pm.vo.PmApproveWithdrawVo;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.jpa.service.IBaseService;

import java.util.Date;
import java.util.List;

public interface IApplyInvoiceService extends IBaseService<ApplyInvoice> {
	void updateFileId(Long id, String fileId);

	void doWithdraw(PmApproveWithdrawVo pwVo)throws ApplicationException;

	List<ApplyInvoice> findByContractId(Long contractId);

	void autoInitiatedInvoice(Long contractId);

	Date findMaxBillDate(Long contractId);
}

