package com.spt.bas.client.entity;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.spt.tools.jpa.vo.IdEntity;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;


@Entity
@Table(name = "t_bs_company_fee_config")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class BsCompanyFeeConfig extends IdEntity {

    /**
     * 企业id
     */
    private Long companyId;

    /**
     * 顾客等级
     */
    private String companyGrade;

    /**
     * 是否绑定cfca
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Boolean bingDingFlg = false;

    /**
     * 手续费比率
     */
    private BigDecimal handlingFeeRatio;


    /**
     * 流程号
     * @return
     */
    private String approveNo;

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

    public String getCompanyGrade() {
        return companyGrade;
    }

    public void setCompanyGrade(String companyGrade) {
        this.companyGrade = companyGrade;
    }

    public Boolean getBingDingFlg() {
        return bingDingFlg;
    }

    public void setBingDingFlg(Boolean bingDingFlg) {
        this.bingDingFlg = bingDingFlg;
    }

    public BigDecimal getHandlingFeeRatio() {
        return handlingFeeRatio;
    }

    public void setHandlingFeeRatio(BigDecimal handlingFeeRatio) {
        this.handlingFeeRatio = handlingFeeRatio;
    }
}
