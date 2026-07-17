package com.spt.bas.server.service;

import com.spt.bas.client.entity.ApplyCompanyCredit;
import com.spt.bas.client.entity.ApplyCompanyInfo;
import com.spt.tools.jpa.service.IBaseService;

public interface IApplyCompanyCreditService extends IBaseService<ApplyCompanyCredit> {
    ApplyCompanyCredit findByCompanyIdAndType(String type, Long companyId, String creditType);
}
