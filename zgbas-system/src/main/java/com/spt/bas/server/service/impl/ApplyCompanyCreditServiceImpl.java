package com.spt.bas.server.service.impl;

import com.spt.bas.client.entity.ApplyCompanyCredit;
import com.spt.bas.client.entity.ApplyCompanyInfo;
import com.spt.bas.server.dao.ApplyCompanyCreditDao;
import com.spt.bas.server.service.IApplyCompanyCreditService;
import com.spt.bas.server.service.IApplyCompanyInfoService;
import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.service.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional(readOnly = true)
public class ApplyCompanyCreditServiceImpl extends BaseService<ApplyCompanyCredit> implements IApplyCompanyCreditService {
    @Autowired
    private ApplyCompanyCreditDao applyCompanyCreditDao;
    @Override
    public BaseDao<ApplyCompanyCredit> getBaseDao() {
        return applyCompanyCreditDao;
    }

    @Override
    public ApplyCompanyCredit findByCompanyIdAndType(String type, Long companyId, String creditType) {
        return applyCompanyCreditDao.findByCompanyIdAndType(type,companyId,creditType);
    }
}
