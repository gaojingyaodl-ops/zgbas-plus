package com.spt.bas.client.entity;

import com.spt.tools.jpa.vo.IdEntity;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * 合同结算金额表
 *
 * @Author MoonLight
 * @Date 2023/5/10 16:05
 * @Version 1.0
 */
@Entity
@Table(name = "t_ctr_contract_settlement_amount")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class CtrContractSettlementAmount extends IdEntity {
    private static final long serialVersionUID = -1;

    /**
     * 合同ID
     */
    private Long contractId;

    /**
     * 合同编号
     */
    private String contractNo;

    /**
     * 关联结算单ID
     */
    private Long settlementId;

    /**
     * 业务ID
     */
    private Long bizId;

    /**
     * 结算金额
     */
    private BigDecimal settlementAmount;

    /**
     * 结算状态
     * 0-未结算
     * 1-已结算
     */
    private String settlementStatus;

    /**
     * 金额类型
     * 0-收货款
     * 1-收逾期罚息
     * 2-返还逾期罚息
     */
    private String settlementType;

    public Long getContractId() {
        return contractId;
    }

    public void setContractId(Long contractId) {
        this.contractId = contractId;
    }

    public String getContractNo() {
        return contractNo;
    }

    public void setContractNo(String contractNo) {
        this.contractNo = contractNo;
    }

    public BigDecimal getSettlementAmount() {
        return Objects.isNull(settlementAmount) ? BigDecimal.ZERO : settlementAmount;
    }

    public void setSettlementAmount(BigDecimal settlementAmount) {
        this.settlementAmount = settlementAmount;
    }

    public String getSettlementStatus() {
        return settlementStatus;
    }

    public void setSettlementStatus(String settlementStatus) {
        this.settlementStatus = settlementStatus;
    }

    public String getSettlementType() {
        return settlementType;
    }

    public void setSettlementType(String settlementType) {
        this.settlementType = settlementType;
    }

    public Long getSettlementId() {
        return settlementId;
    }

    public void setSettlementId(Long settlementId) {
        this.settlementId = settlementId;
    }

    public Long getBizId() {
        return bizId;
    }

    public void setBizId(Long bizId) {
        this.bizId = bizId;
    }
}
