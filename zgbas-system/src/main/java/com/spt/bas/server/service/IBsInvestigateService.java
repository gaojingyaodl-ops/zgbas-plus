package com.spt.bas.server.service;

import com.spt.bas.client.entity.BsInvestigate;
import com.spt.tools.jpa.service.IBaseService;

public interface IBsInvestigateService extends IBaseService<BsInvestigate> {

    BsInvestigate findByCompanyId(Long companyId);

}
