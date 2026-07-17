package com.spt.bas.server.service;

import com.spt.bas.client.entity.BsCompanyQuota;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.core.json.JsonUtil;
import com.spt.tools.jpa.service.IBaseService;
import org.springframework.web.bind.annotation.RequestBody;

public interface IBsCompanyQuotaService extends IBaseService<BsCompanyQuota> {
    void startFlow(String bizEntityJson, Long companyId) throws ApplicationException;

    BsCompanyQuota getLatestApply(Long companyId);
}
