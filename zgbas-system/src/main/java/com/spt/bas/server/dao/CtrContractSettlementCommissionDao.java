package com.spt.bas.server.dao;

import com.spt.bas.client.entity.CtrContractSettlementCommission;
import com.spt.tools.jpa.dao.BaseDao;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.List;

/**
 * @Author MoonLight
 * @Date 2023/5/10 16:33
 * @Version 1.0
 */
public interface CtrContractSettlementCommissionDao extends BaseDao<CtrContractSettlementCommission> {

    CtrContractSettlementCommission findBySettlementAmountId(Long settlementId);

    List<CtrContractSettlementCommission> findBySettlementId(Long settlementId);

    @Transactional
    @Modifying
    void deleteBySettlementIdAndSettlementAmountId(Long settlementId, Long settlementAmountId);

    @Transactional
    @Modifying
    @Query("update CtrContractSettlementCommission set settlementStatus =?2 where settlementId in ?1")
    void updateSettlementStatus(List<Long> settlementIdList, String settlementStatus);

    @Query("from CtrContractSettlementCommission c where c.settlementId in ?1 and c.settlementStatus = '0'")
    List<CtrContractSettlementCommission> getCommissionList(List<Long> settlementIdList);

    @Query("from CtrContractSettlementCommission c where c.settlementId in ?1 and c.settlementStatus = ?2")
    List<CtrContractSettlementCommission> getCommissionList(List<Long> settlementIdList, String settlementStatus);
}
