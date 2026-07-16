package com.spt.bas.server.dao;

import com.spt.bas.client.entity.CtrContractSettlementAmount;
import com.spt.tools.jpa.dao.BaseDao;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.List;

/**
 * @Author MoonLight
 * @Date 2023/5/10 16:32
 * @Version 1.0
 */
public interface CtrContractSettlementAmountDao extends BaseDao<CtrContractSettlementAmount> {
    CtrContractSettlementAmount findByBizId(Long bizId);

    CtrContractSettlementAmount findByBizIdAndSettlementType(Long bizId, String settlementType);
    List<CtrContractSettlementAmount> findBySettlementId(Long settlementId);
    List<CtrContractSettlementAmount> findBySettlementIdAndSettlementType(Long settlementId, String settlementType);
    CtrContractSettlementAmount findBySettlementIdAndSettlementTypeAndSettlementStatus(Long settlementId, String settlementType, String settlementStatus);

    @Query("select sum(a.settlementAmount) from CtrContractSettlementAmount a where a.settlementId =?1 and a.settlementStatus =?2")
    BigDecimal getSumSettlementAmount(Long settlementId, String settlementStatus);

    @Query("select sum(a.settlementAmount) from CtrContractSettlementAmount a where a.settlementId =?1 and a.settlementType =?2")
    BigDecimal getSumSettlementAmountV2(Long settlementId, String settlementType);

    @Transactional
    @Modifying
    void deleteBySettlementIdAndBizId(Long settlementId, Long bizId);

    @Transactional
    @Modifying
    void deleteBySettlementIdIn(List<Long> settlementIds);

    @Transactional
    @Modifying
    @Query("update CtrContractSettlementAmount set settlementStatus =?2 where settlementId in ?1")
    void updateSettlementStatus(List<Long> settlementIdList, String settlementStatus);
}
