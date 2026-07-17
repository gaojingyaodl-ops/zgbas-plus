package com.spt.bas.server.service.impl;

import com.spt.bas.client.entity.BsCompanyBalanceHistory;
import com.spt.bas.server.dao.BsCompanyBalanceHistoryDao;
import com.spt.bas.server.service.BsCompanyBalanceHistoryService;
import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.service.BaseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
@Slf4j
public class BsCompanyBalanceHistoryServiceImpl extends BaseService<BsCompanyBalanceHistory> implements BsCompanyBalanceHistoryService {

    @Autowired
    private BsCompanyBalanceHistoryDao bsCompanyBalanceHistoryDao;

    @Override
    public BaseDao<BsCompanyBalanceHistory> getBaseDao() {
        return bsCompanyBalanceHistoryDao;
    }

    @Override
    public List<BsCompanyBalanceHistory> findByCompanyId(Long companyId) {
        return bsCompanyBalanceHistoryDao.findByCompanyId(companyId);
    }

    @Override
    public void updateLastBalance(Long companyId, BigDecimal lastBalance) {
        bsCompanyBalanceHistoryDao.updateLastBalance(companyId,lastBalance);
    }

    @Override
    public void updateLastBalanceStatus(Long companyId, String changeType) {
        bsCompanyBalanceHistoryDao.updateLastBalanceStatus(companyId,changeType);
    }
}

