package com.spt.bas.server.service;

import com.spt.bas.client.entity.BsIncreaseInfo;
import com.spt.tools.jpa.service.IBaseService;

public interface IBsIncreaseInfoService extends IBaseService<BsIncreaseInfo> {
    BsIncreaseInfo findByCompanyId(Long companyId);
}
