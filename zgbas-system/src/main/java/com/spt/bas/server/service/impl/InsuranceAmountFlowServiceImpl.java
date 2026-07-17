package com.spt.bas.server.service.impl;

import com.spt.bas.client.entity.FileType;
import com.spt.bas.client.entity.InsuranceAmountFlow;
import com.spt.bas.server.dao.InsuranceAmountFlowDao;
import com.spt.bas.server.service.IFileTypeService;
import com.spt.bas.server.service.IInsuranceAmountFlowService;
import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.service.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional(readOnly = true)
public class InsuranceAmountFlowServiceImpl extends BaseService<InsuranceAmountFlow> implements IInsuranceAmountFlowService {
    @Autowired
    private InsuranceAmountFlowDao insuranceAmountFlowDao;
    @Override
    public BaseDao<InsuranceAmountFlow> getBaseDao() {
        return insuranceAmountFlowDao;
    }
}
