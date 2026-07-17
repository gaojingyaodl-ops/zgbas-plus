package com.spt.bas.server.service;

import com.spt.bas.client.entity.ApplyInvoiceReceived;
import com.spt.pm.vo.PmApproveWithdrawVo;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.jpa.service.IBaseService;

import java.util.List;

public interface IApplyInvoiceReceivedDcsxService extends IBaseService<ApplyInvoiceReceived> {
    void updateFileId(Long id, String fileId);

    void doWithdraw(PmApproveWithdrawVo pwVo) throws ApplicationException;

    List<ApplyInvoiceReceived> findByContractId(Long contractId);
}

