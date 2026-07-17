package com.spt.bas.server.service;

import com.spt.bas.client.entity.BsSupplyInfo;
import com.spt.tools.jpa.service.IBaseService;

public interface IBsSupplyInfoService extends IBaseService<BsSupplyInfo> {

    BsSupplyInfo findByWxUserId(Long wxUserId);

    BsSupplyInfo findByCompanyId(Long companyId);
}

