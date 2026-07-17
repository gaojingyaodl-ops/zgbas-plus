package com.spt.bas.server.service;

import com.spt.bas.client.entity.BudgetSettlementTotal;
import com.spt.tools.core.bean.PageSearchVo;
import com.spt.tools.jpa.service.IBaseService;
import org.springframework.data.domain.Page;


public interface IBudgetSettlementTotalService extends IBaseService<BudgetSettlementTotal> {

    Page<BudgetSettlementTotal> findSettlementPage(PageSearchVo searchVo);

    BudgetSettlementTotal sumPageSettlement(PageSearchVo searchVo);

    void createSettleTotal(String summaryDate);
}

