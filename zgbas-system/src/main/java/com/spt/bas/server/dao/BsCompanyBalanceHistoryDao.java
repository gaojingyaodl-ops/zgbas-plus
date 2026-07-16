package com.spt.bas.server.dao;

import com.spt.bas.client.entity.BsCompanyBalanceHistory;
import com.spt.tools.jpa.dao.BaseDao;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

import javax.transaction.Transactional;
import java.math.BigDecimal;

public interface BsCompanyBalanceHistoryDao extends BaseDao<BsCompanyBalanceHistory> {

    @Query("from BsCompanyBalanceHistory a where a.companyId = ?1")
    List<BsCompanyBalanceHistory>  findByCompanyId(Long companyId);

    @Modifying
    @Transactional
    @Query("update BsCompanyBalanceHistory c set c.lastBalance =?2 where c.companyId=?1")
    void updateLastBalance(Long companyId, BigDecimal lastBalance);

    @Modifying
    @Transactional
    @Query("update BsCompanyBalanceHistory c set c.changeType =?2 where c.companyId=?1")
    void updateLastBalanceStatus( Long companyId,  String changeType);

}
