package com.spt.bas.server.service;

import com.spt.bas.client.entity.ApiExternalHis;
import com.spt.tools.jpa.service.IBaseService;

public interface IApiExternalHisService extends IBaseService<ApiExternalHis> {

    void addApiExternalHis(ApiExternalHis externalHis);
}
