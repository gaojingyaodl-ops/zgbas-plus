package com.spt.bas.server.service;

import com.spt.bas.client.entity.ApplyCtrContractFactor;
import com.spt.tools.jpa.service.IBaseService;

import java.math.BigDecimal;
import java.util.Date;

public interface IApplyCtrContractFactoService extends IBaseService<ApplyCtrContractFactor> {

    ApplyCtrContractFactor findByApproveId(Long contractNo);


    ApplyCtrContractFactor findByContractNo(String contrcact);

    void updateFacto(String status,BigDecimal londamount, Date londDate, String contrcact);

    void updateStatusByContractNo(String contractNo,String factorStatus);

    void autoLaunchApplyPay(String contractNo);
}
