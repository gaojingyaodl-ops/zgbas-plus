package com.spt.bas.client.entity;

import com.spt.tools.jpa.vo.IdEntity;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;


@Entity
@Table(name = "t_bs_company_balance_history")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class BsCompanyBalanceHistory extends IdEntity {

    /**
     * 客户id
     */
    private Long companyId;

    /**
     * 变更类型
     */
    private String changeType;

    /**
     * 金额(正/负)
     */
    private BigDecimal balance;

    /**
     * 期末余额
     */
    private BigDecimal lastBalance;

    /**
     * 流程号
     * @return
     */
    private String approveNo;

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public String getApproveNo() {
        return approveNo;
    }

    public void setApproveNo(String approveNo) {
        this.approveNo = approveNo;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public String getChangeType() {
        return changeType;
    }

    public void setChangeType(String changeType) {
        this.changeType = changeType;
    }

    public BigDecimal getLastBalance() {
        return lastBalance;
    }

    public void setLastBalance(BigDecimal lastBalance) {
        this.lastBalance = lastBalance;
    }
}
