package com.spt.bas.server.dao;

import com.spt.bas.client.entity.BudgetSettlementTotal;
import com.spt.tools.jpa.dao.BaseDao;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;


public interface BudgetSettlementTotalDao extends BaseDao<BudgetSettlementTotal> {

    @Modifying
    @Transactional
    void deleteByBudgetSettlementId(String budgetSettlementId);
}

