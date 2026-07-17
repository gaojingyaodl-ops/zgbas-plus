package com.spt.bas.server.service.impl;


import com.spt.bas.client.entity.BsCompany;
import com.spt.bas.client.entity.BsCompanyUserBak;
import com.spt.bas.server.dao.BsCompanyUserBakDao;
import com.spt.bas.server.service.IBsCompanyUserBakService;

import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.service.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@Transactional(readOnly = true)
public class BsCompanyUserBakServiceImpl extends BaseService<BsCompanyUserBak> implements IBsCompanyUserBakService {
    @Autowired
    private BsCompanyUserBakDao companyUserBakDao;

    @Override
    public BsCompanyUserBak findByMatchUserIdAndFollowDate(Long companyId,Long matchUserId) {
        return companyUserBakDao.findByMatchUserIdAndFollowDate(companyId,matchUserId);
    }

    @Override
    public List<BsCompany> getCompanyForDate(Long matchUserId) {

        return companyUserBakDao.getCompanyForDate(matchUserId);
    }

    @Override
    public BsCompanyUserBak findByCompanyId(Long companyId) {

        return companyUserBakDao.findByCompanyId(companyId);
    }

    @Override
    public BaseDao<BsCompanyUserBak> getBaseDao() {
        return companyUserBakDao;
    }
}
