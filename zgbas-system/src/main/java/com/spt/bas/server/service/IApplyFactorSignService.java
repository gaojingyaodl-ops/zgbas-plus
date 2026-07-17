package com.spt.bas.server.service;

import com.spt.bas.client.entity.ApplyFactorSign;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.jpa.service.IBaseService;

public interface IApplyFactorSignService extends IBaseService<ApplyFactorSign> {

    void applyFactorSign(ApplyFactorSign factorSign) throws ApplicationException;
}
