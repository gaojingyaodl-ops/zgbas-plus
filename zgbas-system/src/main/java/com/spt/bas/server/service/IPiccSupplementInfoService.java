package com.spt.bas.server.service;



import com.spt.bas.client.entity.PiccSupplementInfo;
import com.spt.tools.jpa.service.IBaseService;

public interface IPiccSupplementInfoService extends IBaseService<PiccSupplementInfo> {

    PiccSupplementInfo findByCompanyId(Long companyId);
    
}
