package com.spt.bas.server.service;


import com.spt.bas.client.entity.BusinessRestrictRelieve;
import com.spt.tools.jpa.service.IBaseService;

public interface IBusinessRestrictRelieveService extends IBaseService<BusinessRestrictRelieve> {
    void updateUsableCount(Long id, Integer usableCount);
    BusinessRestrictRelieve findByCompanyIdAndAndUserId(Long companyId, Long userId);
    
    void resetUsableCount();
}
