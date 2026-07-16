package com.spt.bas.server.dao;

import com.spt.bas.client.entity.BudgetSettlement;
import com.spt.tools.jpa.dao.BaseDao;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;

/**
 * 预算结算单
 * @author shengong
 */
public interface BudgetSettlementDao extends BaseDao<BudgetSettlement> {

    /**
     * 查询是否是逾期中合同
     * @param contractId
     * @return
     */
    @Query("from BudgetSettlement c where c.sellContractId = ?1 and c.budgetStatus = '1'")
    BudgetSettlement findBySellContractIdWithBudgetStatusIs1(Long contractId);

    /**
     * 所有未完成预算结算单
     * @return
     */
    @Query("from BudgetSettlement c where c.budgetFinishStatus <> '1'")
    List<BudgetSettlement> findUnFinishSettlementListV3();

    /**
     * 根据合同编号查询未完成预算结算单
     * @return
     */
    @Query("from BudgetSettlement c where c.budgetFinishStatus <> '1' and c.sellContractNo =?1")
    List<BudgetSettlement> findUnFinishSettlementListV3ByContractNo(String contractNo);

    BudgetSettlement findBySellContractIdAndBudgetFinishStatus(Long contractId, String finishStatus);

    /**
     * 所有今日到期未完成预算结算单
     * @return
     */
    @Query("from BudgetSettlement c where c.budgetFinishStatus <> '1' and function('to_days',c.payFullTime) = function('to_days',?1) and c.settlementType is not null")
    List<BudgetSettlement> findUnFinishSettlementListV3Today(Date date);


    BudgetSettlement findBySellContractId(Long sellContractId);

}
