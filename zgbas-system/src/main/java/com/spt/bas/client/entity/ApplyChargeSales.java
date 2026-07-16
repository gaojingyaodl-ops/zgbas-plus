package com.spt.bas.client.entity;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.spt.pm.inter.IPmEntity;
import com.spt.tools.jpa.vo.IdEntity;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;

/**
 * 代采赊销
 */
@Entity
@Table(name = "t_apply_charge_sales")
public class ApplyChargeSales extends IdEntity implements IPmEntity {
//    /**
//     * 申请单-撮合业务表
//     */
//    private static final long serialVersionUID = -2635875951438327617L;

    /**
     * 审批id
     */
    private Long approveId;

    /**
     * 企业账套ID
     */
    private Long enterpriseId;

    /**
     * 审批编号
     */
    private String approveNo;

    /**
     * 毛利润
     */
    private BigDecimal grossProfit;

    /**
     * 差价
     */
    private BigDecimal differPrice;

    /**
     * 审批状态		N-新增，A-审批中，B-驳回，D-完成
     */
    private String status;

    /**
     * 附件id
     */
    private String fileId;

    /**
     * 备注
     */
    private String remark;

    /**
     * 我方公司名称
     */
    private String ourCompanyName;

    /**
     * 配送地址
     */
    private String shippingAddr;

    /**
     * 买方总价
     */
    private BigDecimal buyAmount;

    /**
     * 买方总价
     */
    private BigDecimal sellAmount;

    /**
     * 业务类型
     */
    private String businessType;

    /**
     * 合同属性
     */
    private String contractAttr;

    /**
     * 质量标准   Y-原厂标准 G-过渡料 F-副牌料
     */
    private String qualityStandard;

    /**
     * 采购-对方企业ID
     */
    private Long buyCompanyId;

    /**
     * 销售-对方企业ID
     */
    private Long sellCompanyId;

    /**
     * 内部交易销售合同号
     */
    private String sellContractNo;

    /**
     * 内部交易采购方业务员
     */
    private Long buyUserId;

    /**
     * 内部交易采购方业务员
     */
    private String buyUserName;

    /**
     * 采购来源 B:自营采购 G:供应商
     */
    private String buySource;

    /**
     * 货名
     */
    private String productName;

    /**
     * 货名Cd
     */
    private String productCd;

    /**
     * 牌号
     */
    private String brandNumber;

    /**
     * 厂商ID
     */
    private Long factoryId;

    /**
     * 厂商名称
     */
    private String factoryName;

    /**
     * 数量
     */
    private BigDecimal dealNumber;

    /**
     * 合同库存id
     */
    private Long stockContractId;

    /**
     * 利润率
     */
    private BigDecimal grossProfitRate;

    /**
     * 包装规格
     */
    private String wrapSpecs;

    /**
     * 采购佣金
     */
    private BigDecimal buyCommission;

    /**
     * 销售佣金
     */
    private BigDecimal sellCommission;

    /**
     * 公司净利
     */
    private BigDecimal companyCommission;

    /**
     * 营销留存
     销售提成(元) */
    private BigDecimal marketingRetention;

    /**
     * 是否需要风控审批
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Boolean riskApproveFlg;//

    /**
     * 审批放款状态
     */
    private String approvalLoanStatus;


    public Long getApproveId() {
        return approveId;
    }

    public Long getEnterpriseId() {
        return enterpriseId;
    }

    @Override
    public void setEnterpriseId(Long enterpriseId) {
        this.enterpriseId = enterpriseId;
    }

    public String getApproveNo() {
        return approveNo;
    }

    public void setApproveNo(String approveNo) {
        this.approveNo = approveNo;
    }

    public BigDecimal getGrossProfit() {
        return grossProfit;
    }

    public void setGrossProfit(BigDecimal grossProfit) {
        this.grossProfit = grossProfit;
    }

    public BigDecimal getDifferPrice() {
        return differPrice;
    }

    public void setDifferPrice(BigDecimal differPrice) {
        this.differPrice = differPrice;
    }

    public String getStatus() {
        return status;
    }

    public String getFileId() {
        return fileId;
    }

    @Override
    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getOurCompanyName() {
        return ourCompanyName;
    }

    public void setOurCompanyName(String ourCompanyName) {
        this.ourCompanyName = ourCompanyName;
    }

    public String getShippingAddr() {
        return shippingAddr;
    }

    public void setShippingAddr(String shippingAddr) {
        this.shippingAddr = shippingAddr;
    }

    public BigDecimal getBuyAmount() {
        return buyAmount;
    }

    public void setBuyAmount(BigDecimal buyAmount) {
        this.buyAmount = buyAmount;
    }

    public BigDecimal getSellAmount() {
        return sellAmount;
    }

    public void setSellAmount(BigDecimal sellAmount) {
        this.sellAmount = sellAmount;
    }

    public String getBusinessType() {
        return businessType;
    }

    public void setBusinessType(String businessType) {
        this.businessType = businessType;
    }

    public String getContractAttr() {
        return contractAttr;
    }

    public void setContractAttr(String contractAttr) {
        this.contractAttr = contractAttr;
    }

    public String getQualityStandard() {
        return qualityStandard;
    }

    public void setQualityStandard(String qualityStandard) {
        this.qualityStandard = qualityStandard;
    }

    public Long getBuyCompanyId() {
        return buyCompanyId;
    }

    public void setBuyCompanyId(Long buyCompanyId) {
        this.buyCompanyId = buyCompanyId;
    }

    public Long getSellCompanyId() {
        return sellCompanyId;
    }

    public void setSellCompanyId(Long sellCompanyId) {
        this.sellCompanyId = sellCompanyId;
    }

    public String getSellContractNo() {
        return sellContractNo;
    }

    public void setSellContractNo(String sellContractNo) {
        this.sellContractNo = sellContractNo;
    }

    public Long getBuyUserId() {
        return buyUserId;
    }

    public void setBuyUserId(Long buyUserId) {
        this.buyUserId = buyUserId;
    }

    public String getBuyUserName() {
        return buyUserName;
    }

    public void setBuyUserName(String buyUserName) {
        this.buyUserName = buyUserName;
    }

    public String getBuySource() {
        return buySource;
    }

    public void setBuySource(String buySource) {
        this.buySource = buySource;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductCd() {
        return productCd;
    }

    public void setProductCd(String productCd) {
        this.productCd = productCd;
    }

    public String getBrandNumber() {
        return brandNumber;
    }

    public void setBrandNumber(String brandNumber) {
        this.brandNumber = brandNumber;
    }

    public Long getFactoryId() {
        return factoryId;
    }

    public void setFactoryId(Long factoryId) {
        this.factoryId = factoryId;
    }

    public String getFactoryName() {
        return factoryName;
    }

    public void setFactoryName(String factoryName) {
        this.factoryName = factoryName;
    }

    public BigDecimal getDealNumber() {
        return dealNumber;
    }

    public void setDealNumber(BigDecimal dealNumber) {
        this.dealNumber = dealNumber;
    }

    public Long getStockContractId() {
        return stockContractId;
    }

    public void setStockContractId(Long stockContractId) {
        this.stockContractId = stockContractId;
    }

    public BigDecimal getGrossProfitRate() {
        return grossProfitRate;
    }

    public void setGrossProfitRate(BigDecimal grossProfitRate) {
        this.grossProfitRate = grossProfitRate;
    }

    public String getWrapSpecs() {
        return wrapSpecs;
    }

    public void setWrapSpecs(String wrapSpecs) {
        this.wrapSpecs = wrapSpecs;
    }

    public BigDecimal getBuyCommission() {
        return buyCommission;
    }

    public void setBuyCommission(BigDecimal buyCommission) {
        this.buyCommission = buyCommission;
    }

    public BigDecimal getSellCommission() {
        return sellCommission;
    }

    public void setSellCommission(BigDecimal sellCommission) {
        this.sellCommission = sellCommission;
    }

    public BigDecimal getCompanyCommission() {
        return companyCommission;
    }

    public void setCompanyCommission(BigDecimal companyCommission) {
        this.companyCommission = companyCommission;
    }

    public BigDecimal getMarketingRetention() {
        return marketingRetention;
    }

    public void setMarketingRetention(BigDecimal marketingRetention) {
        this.marketingRetention = marketingRetention;
    }

    public Boolean getRiskApproveFlg() {
        return riskApproveFlg;
    }

    public void setRiskApproveFlg(Boolean riskApproveFlg) {
        this.riskApproveFlg = riskApproveFlg;
    }

    public String getApprovalLoanStatus() {
        return approvalLoanStatus;
    }

    public void setApprovalLoanStatus(String approvalLoanStatus) {
        this.approvalLoanStatus = approvalLoanStatus;
    }


    @Override
    public void setApproveId(Long approveId) {
        this.approveId = approveId;
    }

    @Override
    public void setStatus(String status) {
      this.status = status;
    }
}
