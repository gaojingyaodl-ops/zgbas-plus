package com.spt.bas.server.service;

import com.spt.bas.client.entity.ApplyPay;
import com.spt.pm.vo.PmApproveWithdrawVo;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.jpa.service.IBaseService;

import java.util.List;

public interface IApplyDcsxPayService extends IBaseService<ApplyPay> {

    void updateFileId(Long id, String fileId);

    List<ApplyPay> findByContractId(Long contractId);

    void updateApplyStatus(Long contractId);


    void doWithdraw(PmApproveWithdrawVo pwVo) throws ApplicationException;

    List<ApplyPay> findApplyPay(String contractNo);

}

