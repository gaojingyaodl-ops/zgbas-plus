package com.spt.bas.server.service.impl;

import com.spt.bas.client.entity.EvaluateUserManage;
import com.spt.bas.server.dao.EvaluateUserManageDao;
import com.spt.bas.server.service.IEvaluateUserManageService;
import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.service.BaseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class EvaluateUserManageServiceImpl extends BaseService<EvaluateUserManage> implements IEvaluateUserManageService {
    @Autowired
    private EvaluateUserManageDao evaluateUserManageDao;
    @Override
    public BaseDao<EvaluateUserManage> getBaseDao() {
        return evaluateUserManageDao;
    }
}
