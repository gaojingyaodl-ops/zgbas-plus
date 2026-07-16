package com.spt.bas.client.entity;

import com.spt.tools.core.annotation.LogEntityName;
import com.spt.tools.core.annotation.LogField;
import com.spt.tools.jpa.vo.IdEntity;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;

/**
 * 资金方
 */
@Entity
@LogEntityName("资金方")
@Table(name = "t_bs_funder")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class BsFunder extends IdEntity {

    private static final long serialVersionUID = -1L;

    /**
     * 用户ID
     */
    @LogField("用户ID")
    private Long userId;

    /**
     * 企业套账ID
     */
    @LogField("企业套账ID")
    private Long enterpriseId;

    /**
     * 用户名称
     */
    @LogField("用户名称")
    private String userName;

    /**
     * 企业名称
     */
    @LogField("企业名称")
    private String companyNames;

    /**
     * 备注
     */
    @LogField("备注")
    private String remark;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getEnterpriseId() {
        return enterpriseId;
    }

    public void setEnterpriseId(Long enterpriseId) {
        this.enterpriseId = enterpriseId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getCompanyNames() {
        return companyNames;
    }

    public void setCompanyNames(String companyNames) {
        this.companyNames = companyNames;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public BsFunder() {
    }

    public BsFunder(Long userId, Long enterpriseId, String userName, String companyNames, String remark) {
        this.userId = userId;
        this.enterpriseId = enterpriseId;
        this.userName = userName;
        this.companyNames = companyNames;
        this.remark = remark;
    }

    @Override
    public String toString() {
        return "BsFunder{" +
                "userId=" + userId +
                ", enterpriseId=" + enterpriseId +
                ", userName='" + userName + '\'' +
                ", companyNames='" + companyNames + '\'' +
                ", remark='" + remark + '\'' +
                '}';
    }
}
