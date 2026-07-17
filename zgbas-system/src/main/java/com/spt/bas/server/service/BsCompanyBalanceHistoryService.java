package com.spt.bas.server.service;

import com.spt.bas.client.entity.BsCompanyBalanceHistory;
import com.spt.tools.jpa.service.IBaseService;
import java.util.List;

import java.math.BigDecimal;

public interface BsCompanyBalanceHistoryService extends IBaseService<BsCompanyBalanceHistory> {

    List<BsCompanyBalanceHistory>  findByCompanyId(Long companyId);

    void updateLastBalance(Long companyId, BigDecimal lastBalance);

    void updateLastBalanceStatus(Long companyId,  String changeType);
}

