package com.spt.bas.client.entity;

import com.spt.tools.jpa.vo.IdEntity;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.math.BigDecimal;

@Entity
@Table(name = "work_target")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class WorkTarget extends IdEntity {

    /**
     * 所属地区cd
     */
    private String branchCd;

    /**
     * 所属地区名称
     */
    private String branchName;

    /**
     * 目标总价
     */
    private BigDecimal targetTotalAmount;

    /**
     * 业务类型
     */
    private String businessType;

    /**
     * 指标类型
     */
    private String targetType;

    /**
     * 指标类型（转过之后的数据字典）
     */
    private String targetTypeStr;

    /**
     * 创建人
     */
    private String createBy;

    /**
     * 更新人
     */
    private String updateBy;

    /**
     * 指标月份
     */
    private String targetMonth;

    public String getBranchCd() {
        return branchCd;
    }

    public void setBranchCd(String branchCd) {
        this.branchCd = branchCd;
    }

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    public BigDecimal getTargetTotalAmount() {
        return targetTotalAmount;
    }

    public void setTargetTotalAmount(BigDecimal targetTotalAmount) {
        this.targetTotalAmount = targetTotalAmount;
    }

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public String getUpdateBy() {
        return updateBy;
    }

    public void setUpdateBy(String updateBy) {
        this.updateBy = updateBy;
    }

    public String getTargetMonth() {
        return targetMonth;
    }

    public void setTargetMonth(String targetMonth) {
        this.targetMonth = targetMonth;
    }

    public String getBusinessType() {
        return businessType;
    }

    public void setBusinessType(String businessType) {
        this.businessType = businessType;
    }

    public String getTargetType() {
        return targetType;
    }

    public void setTargetType(String targetType) {
        this.targetType = targetType;
    }

    @Transient
    public String getTargetTypeStr() {
        return targetTypeStr;
    }

    public void setTargetTypeStr(String targetTypeStr) {
        this.targetTypeStr = targetTypeStr;
    }
}
