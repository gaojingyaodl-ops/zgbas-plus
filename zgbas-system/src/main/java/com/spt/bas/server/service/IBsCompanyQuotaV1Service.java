package com.spt.bas.server.service;

import com.spt.bas.client.entity.BsCompanyQuotaV1;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.jpa.service.IBaseService;

public interface IBsCompanyQuotaV1Service extends IBaseService<BsCompanyQuotaV1> {
    void startFlow(String bizEntityJson, Long companyId) throws ApplicationException;

}
