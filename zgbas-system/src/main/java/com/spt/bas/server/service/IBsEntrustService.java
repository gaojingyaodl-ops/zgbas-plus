package com.spt.bas.server.service;

import com.spt.bas.client.entity.BsEntrust;
import com.spt.tools.jpa.service.IBaseService;

import java.util.List;

public interface IBsEntrustService extends IBaseService<BsEntrust> {
    BsEntrust findByWxUserId(Long wxUserId);

    List<BsEntrust> findByCompanyId(Long companyId);

}
