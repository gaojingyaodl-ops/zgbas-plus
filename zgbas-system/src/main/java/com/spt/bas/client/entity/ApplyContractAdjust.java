package com.spt.bas.client.entity;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.spt.pm.inter.IPmEntity;
import com.spt.tools.jpa.vo.IdEntity;

/**
 * 合同调整申请
 */
@Entity
@Table(name = "t_apply_contract_adjust")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class ApplyContractAdjust extends IdEntity implements IPmEntity {

    private static final long serialVersionUID = -7539037669748687108L;
    /**
     * 审批id
     */
    private Long approveId;
    /**
     * 审批编号
     */
    private String approveNo;

    /**
     * 审批状态
     */
    private String status;
    /**
     * 调整原因(备注)
     */
    private String remark;

    /**
     * 总数量
     */
    private BigDecimal totalNumber;

    /**
     * 总数量（修改后）
     */
    private BigDecimal totalNumberB;

    private String ourCompanyName;
    /**
     * 我方(修改后)
     */
    private String ourCompanyNameB;

    /**
     * 修改类型
     */
    private String changeType;

    /**
     * 品种
     */
    private String productName;

    /**
     * 牌号
     */
    private String brandNumber;

    /**
     * 厂商
     */
    private String factoryName;

    /**
     * 包装规格
     */
    private String wrapSpecs;

    /**
     * 质量标准
     */
    private String qualityStandard;


    // 采购合同信息=============start
    /**
     * 采购合同编号
     */
    private String buyContractNo;

    /**
     * 上游公司id
     */
    private Long buyCompanyId;

    /**
     * 上游公司
     */
    private String buyCompanyName;

    /**
     * 上游公司id(修改后)
     */
    private Long buyCompanyIdB;

    /**
     * 上游公司（修改后）
     */
    private String buyCompanyNameB;

    /**
     * 上游业务员
     */
    private String buyMatchUserName;

    /**
     * 采购合同单价
     */
    private BigDecimal buyUnitPrice;

    /**
     * 采购合同单价（修改后）
     */
    private BigDecimal buyUnitPriceB;

    private BigDecimal noTaxPrice;

    /**
     * 采购合同总价
     */
    private BigDecimal buyTotalAmount;

    /**
     * 采购合同总价（修改后）
     */
    private BigDecimal buyTotalAmountB;

    /**
     * 采购仓储费
     */
    private BigDecimal buyWarehouseAmount;

    /**
     * 采购仓储费(修改后)
     */
    private BigDecimal buyWarehouseAmountB;

    /**
     * 采购运输费
     */
    private BigDecimal buyTransformAmount;

    /**
     * 采购运输费（修改后）
     */
    private BigDecimal buyTransformAmountB;

    /**
     * 采购合同附件（修改后）
     */
    private String buyContractFileId;

    // 采购合同信息=============end

    // 销售合同信息=============start

    /**
     * 销售合同编号
     */
    private String sellContractNo;

    /**
     * 约定付全款日期
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date buyPayFullTime;

    /**
     * 约定付全款日期(修改后)
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date buyPayFullTimeB;
    
    /**
     * 约定收全款日期
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date payFullTime;

    /**
     * 约定收全款日期(修改后)
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date payFullTimeB;

    /**
     * 下游公司
     */
    private Long sellCompanyId;

    /**
     * 下游公司
     */
    private String sellCompanyName;

    /**
     * 下游公司(修改后)
     */
    private Long sellCompanyIdB;

    /**
     * 下游公司（修改后）
     */
    private String sellCompanyNameB;

    /**
     * 下游业务员
     */
    private String sellMatchUserName;

    /**
     * 回款周期
     */
    private Integer creditDays;

    /**
     * 加价
     */
    private BigDecimal premium;

    /**
     * 加价（修改后）
     */
    private BigDecimal premiumB;

    /**
     * 下游仓储费
     */
    private BigDecimal sellWarehouseAmount;

    /**
     * 下游仓储费(修改后)
     */
    private BigDecimal sellWarehouseAmountB;

    /**
     * 下游运输费
     */
    private BigDecimal sellTransformAmount;

    /**
     * 下游运输费（修改后）
     */
    private BigDecimal sellTransformAmountB;

    /**
     * 当前损耗
     */
    private BigDecimal lossAmount;

    /**
     * 损耗(修改后)
     */
    private BigDecimal lossAmountB;

    /**
     * 销售合同附件（修改后）
     */
    private String sellContractFileId;

    /**
     * 销售合同单价
     */
    private BigDecimal sellUnitPrice;

    /**
     * 销售合同单价(修改后)
     */
    private BigDecimal sellUnitPriceB;

    /**
     * 销售合同金额
     */
    private BigDecimal sellTotalAmount;

    /**
     * 销售合同金额(修改后)
     */
    private BigDecimal sellTotalAmountB;
    // 销售合同信息=============end

    /**
     * 决算类型 null:代采 0:一票制 1:两票制
     */
    private String settlementType;

    /**
     * 服务合同金额
     */
    private BigDecimal serviceAmount;

    /**
     * 服务合同金额（修改后）
     */
    private BigDecimal serviceAmountB;
    /**
     * 收货日期
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date buyDeliveryDate;

    /**
     * 收货日期(修改后)
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date buyDeliveryDateB;
    
    /**
     * 收货日期
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date sellDeliveryDate;

    /**
     * 收货日期(修改后)
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date sellDeliveryDateB;

    /**
     * 上游公司使用抬头
     */
    private String buyCompanyTitle;

    /**
     * 上游公司使用抬头(修改后)
     */
    private String buyCompanyTitleB;

    /**
     * 下游公司使用抬头
     */
    private String sellCompanyTitle;

    /**
     * 下游公司使用抬头(修改后)
     */
    private String sellCompanyTitleB;

    /**
     * 销售服务费费率
     */
    private BigDecimal serverRate;

    /**
     * 附件id
     */
    private String aboutContractFileId;

    /**
     * 部门Id
     * @return
     */
    private Long deptId;

    /**
     * 销售合同id
     */
    private Long sellContractId;
    /**
     * 采购合同id
     */
    private Long buyContractId;

    public BigDecimal getServerRate() {
        return serverRate;
    }

    public void setServerRate(BigDecimal serverRate) {
        this.serverRate = serverRate;
    }

    public Long getApproveId() {
        return approveId;
    }

    @Override
    public void setApproveId(Long approveId) {
        this.approveId = approveId;
    }

    public String getApproveNo() {
        return approveNo;
    }

    public void setApproveNo(String approveNo) {
        this.approveNo = approveNo;
    }

    public String getStatus() {
        return status;
    }

    @Override
    public void setStatus(String status) {
        this.status = status;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public BigDecimal getTotalNumber() {
        return totalNumber;
    }

    public void setTotalNumber(BigDecimal totalNumber) {
        this.totalNumber = totalNumber;
    }

    public String getBuyContractNo() {
        return buyContractNo;
    }

    public void setBuyContractNo(String buyContractNo) {
        this.buyContractNo = buyContractNo;
    }

    public Long getBuyCompanyId() {
        return buyCompanyId;
    }

    public void setBuyCompanyId(Long buyCompanyId) {
        this.buyCompanyId = buyCompanyId;
    }

    public String getBuyCompanyName() {
        return buyCompanyName;
    }

    public void setBuyCompanyName(String buyCompanyName) {
        this.buyCompanyName = buyCompanyName;
    }

    public Long getBuyCompanyIdB() {
        return buyCompanyIdB;
    }

    public void setBuyCompanyIdB(Long buyCompanyIdB) {
        this.buyCompanyIdB = buyCompanyIdB;
    }

    public String getBuyCompanyNameB() {
        return buyCompanyNameB;
    }

    public void setBuyCompanyNameB(String buyCompanyNameB) {
        this.buyCompanyNameB = buyCompanyNameB;
    }

    public String getBuyMatchUserName() {
        return buyMatchUserName;
    }

    public void setBuyMatchUserName(String buyMatchUserName) {
        this.buyMatchUserName = buyMatchUserName;
    }

    public BigDecimal getBuyUnitPrice() {
        return buyUnitPrice;
    }

    public void setBuyUnitPrice(BigDecimal buyUnitPrice) {
        this.buyUnitPrice = buyUnitPrice;
    }

    public BigDecimal getBuyUnitPriceB() {
        return buyUnitPriceB;
    }

    public void setBuyUnitPriceB(BigDecimal buyUnitPriceB) {
        this.buyUnitPriceB = buyUnitPriceB;
    }

    public BigDecimal getNoTaxPrice() {
        return noTaxPrice;
    }

    public void setNoTaxPrice(BigDecimal noTaxPrice) {
        this.noTaxPrice = noTaxPrice;
    }

    public BigDecimal getBuyTotalAmount() {
        return buyTotalAmount;
    }

    public void setBuyTotalAmount(BigDecimal buyTotalAmount) {
        this.buyTotalAmount = buyTotalAmount;
    }

    public BigDecimal getBuyTotalAmountB() {
        return buyTotalAmountB;
    }

    public void setBuyTotalAmountB(BigDecimal buyTotalAmountB) {
        this.buyTotalAmountB = buyTotalAmountB;
    }

    public BigDecimal getBuyWarehouseAmount() {
        return buyWarehouseAmount;
    }

    public void setBuyWarehouseAmount(BigDecimal buyWarehouseAmount) {
        this.buyWarehouseAmount = buyWarehouseAmount;
    }

    public BigDecimal getBuyWarehouseAmountB() {
        return buyWarehouseAmountB;
    }

    public void setBuyWarehouseAmountB(BigDecimal buyWarehouseAmountB) {
        this.buyWarehouseAmountB = buyWarehouseAmountB;
    }

    public BigDecimal getBuyTransformAmount() {
        return buyTransformAmount;
    }

    public void setBuyTransformAmount(BigDecimal buyTransformAmount) {
        this.buyTransformAmount = buyTransformAmount;
    }

    public BigDecimal getBuyTransformAmountB() {
        return buyTransformAmountB;
    }

    public void setBuyTransformAmountB(BigDecimal buyTransformAmountB) {
        this.buyTransformAmountB = buyTransformAmountB;
    }

    public String getBuyContractFileId() {
        return buyContractFileId;
    }

    public void setBuyContractFileId(String buyContractFileId) {
        this.buyContractFileId = buyContractFileId;
    }

    public String getSellContractNo() {
        return sellContractNo;
    }

    public void setSellContractNo(String sellContractNo) {
        this.sellContractNo = sellContractNo;
    }

    public Date getPayFullTime() {
        return payFullTime;
    }

    public void setPayFullTime(Date payFullTime) {
        this.payFullTime = payFullTime;
    }

    public Date getPayFullTimeB() {
        return payFullTimeB;
    }

    public void setPayFullTimeB(Date payFullTimeB) {
        this.payFullTimeB = payFullTimeB;
    }

    public Long getSellCompanyId() {
        return sellCompanyId;
    }

    public void setSellCompanyId(Long sellCompanyId) {
        this.sellCompanyId = sellCompanyId;
    }

    public String getSellCompanyName() {
        return sellCompanyName;
    }

    public void setSellCompanyName(String sellCompanyName) {
        this.sellCompanyName = sellCompanyName;
    }

    public String getSellMatchUserName() {
        return sellMatchUserName;
    }

    public void setSellMatchUserName(String sellMatchUserName) {
        this.sellMatchUserName = sellMatchUserName;
    }

    public Integer getCreditDays() {
        return creditDays;
    }

    public void setCreditDays(Integer creditDays) {
        this.creditDays = creditDays;
    }

    public BigDecimal getPremium() {
        return premium;
    }

    public void setPremium(BigDecimal premium) {
        this.premium = premium;
    }

    public BigDecimal getPremiumB() {
        return premiumB;
    }

    public void setPremiumB(BigDecimal premiumB) {
        this.premiumB = premiumB;
    }

    public BigDecimal getSellWarehouseAmount() {
        return sellWarehouseAmount;
    }

    public void setSellWarehouseAmount(BigDecimal sellWarehouseAmount) {
        this.sellWarehouseAmount = sellWarehouseAmount;
    }

    public BigDecimal getSellWarehouseAmountB() {
        return sellWarehouseAmountB;
    }

    public void setSellWarehouseAmountB(BigDecimal sellWarehouseAmountB) {
        this.sellWarehouseAmountB = sellWarehouseAmountB;
    }

    public BigDecimal getSellTransformAmount() {
        return sellTransformAmount;
    }

    public void setSellTransformAmount(BigDecimal sellTransformAmount) {
        this.sellTransformAmount = sellTransformAmount;
    }

    public BigDecimal getSellTransformAmountB() {
        return sellTransformAmountB;
    }

    public void setSellTransformAmountB(BigDecimal sellTransformAmountB) {
        this.sellTransformAmountB = sellTransformAmountB;
    }

    public BigDecimal getLossAmount() {
        return lossAmount;
    }

    public void setLossAmount(BigDecimal lossAmount) {
        this.lossAmount = lossAmount;
    }

    public BigDecimal getLossAmountB() {
        return lossAmountB;
    }

    public void setLossAmountB(BigDecimal lossAmountB) {
        this.lossAmountB = lossAmountB;
    }

    public String getSellContractFileId() {
        return sellContractFileId;
    }

    public void setSellContractFileId(String sellContractFileId) {
        this.sellContractFileId = sellContractFileId;
    }

    public BigDecimal getSellUnitPrice() {
        return sellUnitPrice;
    }

    public void setSellUnitPrice(BigDecimal sellUnitPrice) {
        this.sellUnitPrice = sellUnitPrice;
    }

    public BigDecimal getSellTotalAmount() {
        return sellTotalAmount;
    }

    public void setSellTotalAmount(BigDecimal sellTotalAmount) {
        this.sellTotalAmount = sellTotalAmount;
    }

    public String getOurCompanyName() {
        return ourCompanyName;
    }

    public void setOurCompanyName(String ourCompanyName) {
        this.ourCompanyName = ourCompanyName;
    }

    public Long getSellCompanyIdB() {
        return sellCompanyIdB;
    }

    public void setSellCompanyIdB(Long sellCompanyIdB) {
        this.sellCompanyIdB = sellCompanyIdB;
    }

    public String getSellCompanyNameB() {
        return sellCompanyNameB;
    }

    public void setSellCompanyNameB(String sellCompanyNameB) {
        this.sellCompanyNameB = sellCompanyNameB;
    }

    public BigDecimal getSellTotalAmountB() {
        return sellTotalAmountB;
    }

    public void setSellTotalAmountB(BigDecimal sellTotalAmountB) {
        this.sellTotalAmountB = sellTotalAmountB;
    }

    public String getSettlementType() {
        return settlementType;
    }

    public void setSettlementType(String settlementType) {
        this.settlementType = settlementType;
    }

    public BigDecimal getServiceAmount() {
        return serviceAmount;
    }

    public void setServiceAmount(BigDecimal serviceAmount) {
        this.serviceAmount = serviceAmount;
    }

    public BigDecimal getServiceAmountB() {
        return serviceAmountB;
    }

    public void setServiceAmountB(BigDecimal serviceAmountB) {
        this.serviceAmountB = serviceAmountB;
    }

    public BigDecimal getSellUnitPriceB() {
        return sellUnitPriceB;
    }

    public void setSellUnitPriceB(BigDecimal sellUnitPriceB) {
        this.sellUnitPriceB = sellUnitPriceB;
    }

    public Date getSellDeliveryDate() {
        return sellDeliveryDate;
    }

    public void setSellDeliveryDate(Date sellDeliveryDate) {
        this.sellDeliveryDate = sellDeliveryDate;
    }

    public Date getSellDeliveryDateB() {
        return sellDeliveryDateB;
    }

    public void setSellDeliveryDateB(Date sellDeliveryDateB) {
        this.sellDeliveryDateB = sellDeliveryDateB;
    }

    public String getChangeType() {
        return changeType;
    }

    public void setChangeType(String changeType) {
        this.changeType = changeType;
    }

    public BigDecimal getTotalNumberB() {
        return totalNumberB;
    }

    public void setTotalNumberB(BigDecimal totalNumberB) {
        this.totalNumberB = totalNumberB;
    }

    public String getOurCompanyNameB() {
        return ourCompanyNameB;
    }

    public void setOurCompanyNameB(String ourCompanyNameB) {
        this.ourCompanyNameB = ourCompanyNameB;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getBrandNumber() {
        return brandNumber;
    }

    public void setBrandNumber(String brandNumber) {
        this.brandNumber = brandNumber;
    }

    public String getFactoryName() {
        return factoryName;
    }

    public void setFactoryName(String factoryName) {
        this.factoryName = factoryName;
    }

    public String getWrapSpecs() {
        return wrapSpecs;
    }

    public void setWrapSpecs(String wrapSpecs) {
        this.wrapSpecs = wrapSpecs;
    }

    public String getQualityStandard() {
        return qualityStandard;
    }

    public void setQualityStandard(String qualityStandard) {
        this.qualityStandard = qualityStandard;
    }

    public String getBuyCompanyTitle() {
        return buyCompanyTitle;
    }

    public void setBuyCompanyTitle(String buyCompanyTitle) {
        this.buyCompanyTitle = buyCompanyTitle;
    }

    public String getBuyCompanyTitleB() {
        return buyCompanyTitleB;
    }

    public void setBuyCompanyTitleB(String buyCompanyTitleB) {
        this.buyCompanyTitleB = buyCompanyTitleB;
    }

    public String getSellCompanyTitle() {
        return sellCompanyTitle;
    }

    public void setSellCompanyTitle(String sellCompanyTitle) {
        this.sellCompanyTitle = sellCompanyTitle;
    }

    public String getSellCompanyTitleB() {
        return sellCompanyTitleB;
    }

    public void setSellCompanyTitleB(String sellCompanyTitleB) {
        this.sellCompanyTitleB = sellCompanyTitleB;
    }

    public String getAboutContractFileId() {
        return aboutContractFileId;
    }

    public void setAboutContractFileId(String aboutContractFileId) {
        this.aboutContractFileId = aboutContractFileId;
    }

    public Long getDeptId() {
        return deptId;
    }

    public void setDeptId(Long deptId) {
        this.deptId = deptId;
    }

    public Date getBuyPayFullTime() {
        return buyPayFullTime;
    }

    public void setBuyPayFullTime(Date buyPayFullTime) {
        this.buyPayFullTime = buyPayFullTime;
    }

    public Date getBuyPayFullTimeB() {
        return buyPayFullTimeB;
    }

    public void setBuyPayFullTimeB(Date buyPayFullTimeB) {
        this.buyPayFullTimeB = buyPayFullTimeB;
    }

    public Date getBuyDeliveryDate() {
        return buyDeliveryDate;
    }

    public void setBuyDeliveryDate(Date buyDeliveryDate) {
        this.buyDeliveryDate = buyDeliveryDate;
    }

    public Date getBuyDeliveryDateB() {
        return buyDeliveryDateB;
    }

    public void setBuyDeliveryDateB(Date buyDeliveryDateB) {
        this.buyDeliveryDateB = buyDeliveryDateB;
    }

    public Long getSellContractId() {
        return sellContractId;
    }

    public void setSellContractId(Long sellContractId) {
        this.sellContractId = sellContractId;
    }

    public Long getBuyContractId() {
        return buyContractId;
    }

    public void setBuyContractId(Long buyContractId) {
        this.buyContractId = buyContractId;
    }
}
