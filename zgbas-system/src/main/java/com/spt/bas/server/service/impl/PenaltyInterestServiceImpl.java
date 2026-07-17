package com.spt.bas.server.service.impl;


import com.spt.bas.client.entity.PenaltyInterest;
import com.spt.bas.server.dao.PenaltyInterestDao;
import com.spt.bas.server.service.IPenaltyInterestService;

import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.service.BaseService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


/**
 * @Author: wm
 * @create 2022/06/08 10:22
 * @version: 1.0
 * @description:
 */
@Component
public class PenaltyInterestServiceImpl extends BaseService<PenaltyInterest> implements IPenaltyInterestService {
    @Autowired
    private PenaltyInterestDao penaltyInterestDao;
    @Override
    public BaseDao<PenaltyInterest> getBaseDao() {
        return penaltyInterestDao;
    }
    @Override
    public Class<PenaltyInterest> getEntityClazz() {
        return PenaltyInterest.class;
    }


    @Override
    public void updateInterStatus(String interestStatus, Long bizId) {
        penaltyInterestDao.updateInterStatus(interestStatus,bizId);
    }

    @Override
    public List<String> findContractNoByCompanyId(String companyId) {
        return penaltyInterestDao.findContractNoByCompanyId(companyId);
    }
}
