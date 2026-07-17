package com.spt.bas.server.service;

import com.spt.bas.client.entity.BasContract;
import com.spt.bas.client.entity.BasInvoice;
import com.spt.pm.vo.PmApproveWithdrawVo;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.jpa.service.IBaseService;

public interface IBasInvoiceService extends IBaseService<BasInvoice> {

	BasInvoice newEntity(BasContract contract);

	void updateFileId(Long id, String fileId);
	
	void doWithdraw(PmApproveWithdrawVo pwVo)throws ApplicationException;
}

