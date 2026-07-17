package com.spt.bas.server.service;

import com.spt.bas.client.entity.ApplyDeposit;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.jpa.service.IBaseService;

public interface IApplyDepositService extends IBaseService<ApplyDeposit> {
    void startFlow(String bizEntityJson, Long companyId) throws ApplicationException;
}
