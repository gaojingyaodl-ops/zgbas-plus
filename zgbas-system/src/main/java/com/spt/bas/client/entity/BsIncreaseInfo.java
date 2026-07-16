package com.spt.bas.client.entity;

import com.spt.tools.jpa.vo.IdEntity;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * <p>
 * 风控员填写额增信信息
 * </p>
 *
 * @Author: shengong
 * @Date: Created in 2021-01-27 09:43
 */
@Entity
@Table(name = "t_bs_increase_info")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@DynamicUpdate
@DynamicInsert
public class BsIncreaseInfo extends IdEntity {
    private static final long serialVersionUID = -7030801460189004952L;
    private Long companyId;

    /**
     * 网塑人保额度
     */
    private String npInsuranceCredit;

    /**
     * 网塑历史交易笔数
     */
    private String npHistoryTradeNum;

    /**
     * 平均逾期天数
     */
    private String overdueTradeDay;

    /**
     * 其他增信措施的附件ID
     */
    private String otherFinancialIds;

    /**
     * 其他增信措施的内容
     */
    private String otherFinancial;

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public String getNpInsuranceCredit() {
        return npInsuranceCredit;
    }

    public void setNpInsuranceCredit(String npInsuranceCredit) {
        this.npInsuranceCredit = npInsuranceCredit;
    }

    public String getNpHistoryTradeNum() {
        return npHistoryTradeNum;
    }

    public void setNpHistoryTradeNum(String npHistoryTradeNum) {
        this.npHistoryTradeNum = npHistoryTradeNum;
    }

    public String getOverdueTradeDay() {
        return overdueTradeDay;
    }

    public void setOverdueTradeDay(String overdueTradeDay) {
        this.overdueTradeDay = overdueTradeDay;
    }

    public String getOtherFinancialIds() {
        return otherFinancialIds;
    }

    public void setOtherFinancialIds(String otherFinancialIds) {
        this.otherFinancialIds = otherFinancialIds;
    }

    public String getOtherFinancial() {
        return otherFinancial;
    }

    public void setOtherFinancial(String otherFinancial) {
        this.otherFinancial = otherFinancial;
    }
}
