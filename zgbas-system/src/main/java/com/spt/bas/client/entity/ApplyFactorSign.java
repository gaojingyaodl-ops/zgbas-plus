package com.spt.bas.client.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.spt.pm.inter.IPmEntity;
import com.spt.tools.jpa.vo.IdEntity;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;

/**
 * 签署保理
 */
@Entity
@Table(name = "t_apply_factor_sign")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class ApplyFactorSign extends IdEntity implements IPmEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 合同ID
     */
    private Long contractId;

    /**
     * 合同编号
     */
    private String contractNo;

    /**
     * 货名
     */
    private String productNames;

    /**
     * 合同日期
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date contractTime;

    /**
     * 客户ID
     */
    private Long companyId;

    /**
     * 客户名称
     */
    private String companyName;

    /**
     * 我方
     */
    private String ourCompanyName;

    /**
     * 数量
     */
    private BigDecimal dealNumber;

    /**
     * 销售单价
     */
    private BigDecimal dealPrice;

    /**
     * 约定付全款日期
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date payFullDate;

    /**
     * 交货日期
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date deliveryDate;

    /**
     * 销售合同金额
     */
    private BigDecimal totalAmount;

    /**
     * 附件ID
     */
    private String fileId;

    /**
     * 审批单ID
     */
    private Long approveId;

    /**
     * 状态
     */
    private String status;

    /**
     * 企业账套ID
     */
    private Long enterpriseId;

    private Long applyUserId;

    public Long getContractId() {
        return contractId;
    }

    public void setContractId(Long contractId) {
        this.contractId = contractId;
    }

    public String getContractNo() {
        return StringUtils.isBlank(contractNo) ? "" : contractNo;
    }

    public void setContractNo(String contractNo) {
        this.contractNo = contractNo;
    }

    public String getProductNames() {
        return StringUtils.isBlank(productNames) ? "" : productNames;
    }

    public void setProductNames(String productNames) {
        this.productNames = productNames;
    }

    public Date getContractTime() {
        return contractTime;
    }

    public void setContractTime(Date contractTime) {
        this.contractTime = contractTime;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public String getCompanyName() {
        return StringUtils.isBlank(companyName) ? "" : companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getOurCompanyName() {
        return StringUtils.isBlank(ourCompanyName) ? "" : ourCompanyName;
    }

    public void setOurCompanyName(String ourCompanyName) {
        this.ourCompanyName = ourCompanyName;
    }

    public BigDecimal getDealNumber() {
        return dealNumber;
    }

    public void setDealNumber(BigDecimal dealNumber) {
        this.dealNumber = dealNumber;
    }

    public BigDecimal getDealPrice() {
        return dealPrice;
    }

    public void setDealPrice(BigDecimal dealPrice) {
        this.dealPrice = dealPrice;
    }

    public Date getPayFullDate() {
        return payFullDate;
    }

    public void setPayFullDate(Date payFullDate) {
        this.payFullDate = payFullDate;
    }

    public Date getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(Date deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public BigDecimal getTotalAmount() {
        return Objects.isNull(totalAmount) ? BigDecimal.ZERO : totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getFileId() {
        return fileId;
    }

    @Override
    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public Long getApproveId() {
        return approveId;
    }

    @Override
    public void setApproveId(Long approveId) {
        this.approveId = approveId;
    }

    public String getStatus() {
        return status;
    }

    @Override
    public void setStatus(String status) {
        this.status = status;
    }

    public Long getEnterpriseId() {
        return enterpriseId;
    }

    @Override
    public void setEnterpriseId(Long enterpriseId) {
        this.enterpriseId = enterpriseId;
    }

    public Long getApplyUserId() {
        return applyUserId;
    }

    public void setApplyUserId(Long applyUserId) {
        this.applyUserId = applyUserId;
    }
}
