package com.spt.bas.client.entity;

import com.spt.tools.jpa.vo.IdEntity;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;

/**
 * 合同结算提成表
 *
 * @Author MoonLight
 * @Date 2023/5/10 16:27
 * @Version 1.0
 */
@Entity
@Table(name = "t_ctr_contract_settlement_commission")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class CtrContractSettlementCommission extends IdEntity {
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
     * 结算单ID
     */
    private Long settlementId;

    /**
     * 结算单金额表ID
     */
    private Long settlementAmountId;

    /**
     * 税后差价收入
     */
    private BigDecimal afterTaxSpreadAmount;

    /**
     * 采购业务员ID
     */
    private Long buyMatchUserId;

    /**
     * 采购业务员
     */
    private String buyMatchUserName;

    /**
     * 销售业务员ID
     */
    private Long sellMatchUserId;

    /**
     * 销售业务员
     */
    private String sellMatchUserName;

    /**
     * 采购业务员上级主管
     */
    private Long buyHeadUserId;

    /**
     * 销售业务员上级主管
     */
    private Long sellHeadUserId;

    /**
     * 销售团队负责人分成
     */
    private BigDecimal sellHeadCommissionAmount;

    /**
     * 采购团队负责人分成
     */
    private BigDecimal buyHeadCommissionAmount;

    /**
     * 销售人员分成
     */
    private BigDecimal sellMatchAmount;

    /**
     * 采购人员分成
     */
    private BigDecimal buyMatchAmount;

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

    /**
     * 采购提成比例
     */
    private BigDecimal buyCommission = BigDecimal.ZERO;

    /**
     * 销售提成比例
     */
    private BigDecimal sellCommission = BigDecimal.ZERO;

    /**
     * 采购负责人提成比例
     */
    private BigDecimal buyHeadCommission = BigDecimal.ZERO;

    /**
     * 销售负责人提成比例
     */
    private BigDecimal sellHeadCommission = BigDecimal.ZERO;

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

    public Long getSettlementId() {
        return settlementId;
    }

    public void setSettlementId(Long settlementId) {
        this.settlementId = settlementId;
    }

    public BigDecimal getAfterTaxSpreadAmount() {
        return defaultNum(afterTaxSpreadAmount);
    }

    public void setAfterTaxSpreadAmount(BigDecimal afterTaxSpreadAmount) {
        this.afterTaxSpreadAmount = afterTaxSpreadAmount;
    }

    public Long getBuyMatchUserId() {
        return buyMatchUserId;
    }

    public void setBuyMatchUserId(Long buyMatchUserId) {
        this.buyMatchUserId = buyMatchUserId;
    }

    public String getBuyMatchUserName() {
        return buyMatchUserName;
    }

    public void setBuyMatchUserName(String buyMatchUserName) {
        this.buyMatchUserName = buyMatchUserName;
    }

    public Long getSellMatchUserId() {
        return sellMatchUserId;
    }

    public void setSellMatchUserId(Long sellMatchUserId) {
        this.sellMatchUserId = sellMatchUserId;
    }

    public String getSellMatchUserName() {
        return sellMatchUserName;
    }

    public void setSellMatchUserName(String sellMatchUserName) {
        this.sellMatchUserName = sellMatchUserName;
    }

    public Long getBuyHeadUserId() {
        return buyHeadUserId;
    }

    public void setBuyHeadUserId(Long buyHeadUserId) {
        this.buyHeadUserId = buyHeadUserId;
    }

    public Long getSellHeadUserId() {
        return sellHeadUserId;
    }

    public void setSellHeadUserId(Long sellHeadUserId) {
        this.sellHeadUserId = sellHeadUserId;
    }

    public BigDecimal getSellHeadCommissionAmount() {
        return defaultNum(sellHeadCommissionAmount);
    }

    public void setSellHeadCommissionAmount(BigDecimal sellHeadCommissionAmount) {
        this.sellHeadCommissionAmount = sellHeadCommissionAmount;
    }


    public BigDecimal getBuyHeadCommissionAmount() {
        return defaultNum(buyHeadCommissionAmount);
    }

    public void setBuyHeadCommissionAmount(BigDecimal buyHeadCommissionAmount) {
        this.buyHeadCommissionAmount = buyHeadCommissionAmount;
    }

    public BigDecimal getSellMatchAmount() {
        return defaultNum(sellMatchAmount);
    }

    public void setSellMatchAmount(BigDecimal sellMatchAmount) {
        this.sellMatchAmount = sellMatchAmount;
    }

    public BigDecimal getBuyMatchAmount() {
        return defaultNum(buyMatchAmount);
    }

    public void setBuyMatchAmount(BigDecimal buyMatchAmount) {
        this.buyMatchAmount = buyMatchAmount;
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

    public Long getSettlementAmountId() {
        return settlementAmountId;
    }

    public void setSettlementAmountId(Long settlementAmountId) {
        this.settlementAmountId = settlementAmountId;
    }

    private BigDecimal defaultNum(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    public BigDecimal getBuyCommission() {
        return defaultNum(buyCommission);
    }

    public void setBuyCommission(BigDecimal buyCommission) {
        this.buyCommission = buyCommission;
    }

    public BigDecimal getSellCommission() {
        return defaultNum(sellCommission);
    }

    public void setSellCommission(BigDecimal sellCommission) {
        this.sellCommission = sellCommission;
    }

    public BigDecimal getBuyHeadCommission() {
        return defaultNum(buyHeadCommission);
    }

    public void setBuyHeadCommission(BigDecimal buyHeadCommission) {
        this.buyHeadCommission = buyHeadCommission;
    }

    public BigDecimal getSellHeadCommission() {
        return defaultNum(sellHeadCommission);
    }

    public void setSellHeadCommission(BigDecimal sellHeadCommission) {
        this.sellHeadCommission = sellHeadCommission;
    }
}
