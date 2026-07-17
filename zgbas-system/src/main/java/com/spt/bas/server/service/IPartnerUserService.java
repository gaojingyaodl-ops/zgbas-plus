package com.spt.bas.server.service;

import com.spt.bas.client.entity.LogisticsCompanyConfig;
import com.spt.bas.client.entity.PartnerUser;
import com.spt.tools.jpa.service.IBaseService;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

public interface IPartnerUserService extends IBaseService<PartnerUser> {

    public List<PartnerUser> getByCompanyId(Long companyId);

    public PartnerUser getByUserId(Long userId);
}
