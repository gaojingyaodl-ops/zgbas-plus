package com.spt.bas.client.entity;

import com.spt.pm.inter.IPmEntity;
import com.spt.tools.jpa.vo.IdEntity;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 争议
 */
public class ApplyDispute extends IdEntity implements IPmEntity {

    private Long approveContentId;

    private Long approveId;

    private String status;

    private String remark;

    /**
     * 发起争议时销售合同状态
     */
    private String curBuyContractStatus;

    /**
     * 发起争议时采购合同状态
     */
    private String curSellContractStatus;

    private String fileId;


    // 标的物信息 ======start
    // 我方
    private String ourCompanyName;

    // 品种
    private String productName;

    // 牌号
    private String brandNumber;

    // 数量
    private BigDecimal dealNumber;

    // 厂商
    private String factoryName;

    // 包装规格
    private String wrapSpecs;

    // 质量标准
    private String qualityStandard;
    // 标的物信息 ======end

    // 采购=======================================================start
    private Long buyContractId;

    private String buyContractNo;

    // 采购来源
    private String buySource;

    // 供方
    private String buyCompanyName;

    // 业务员
    private String buyMatchUserName;

    // 结算方式
    private String deliveryModeB;

    // 支付方式
    private String payType;

    // 定金比例
    private BigDecimal payRate;

    // 定金
    private BigDecimal payRateAmount;

    // 付全款日期
    private Date payFullTime;

    // 交货方式
    private String buyDeliveryType;

    // 交货日期
    private Date buyDeliveryDate;

    // 交货日期补充字段
    private String bArrivalTimeExt;

    // 交货地点
    private String bdeliveryAddr;

    // 详细地址
    private String contactAddr;

    // 仓储费
    private BigDecimal bwarehouseCost;

    // 运输费
    private BigDecimal btransportCost;

    // 含税单价
    private BigDecimal bdealPrice;

    // 不含税单价
    private BigDecimal bdealAmountNotax;

    // 总价
    private BigDecimal btotalAmount;

    // 补充条款
    private String extraTerm;

    // 备注
    private String payRemark;

    // 采购合同
    private String buyContentTemplateId;

    // 采购=======================================================end


    // 销售=======================================================start
    private Long sellContractId;

    private String sellContractNo;

    // 需方
    private String sellCompanyName;

    // 业务员
    private String sellMatchUserName;

    // 结算方式
    private String deliveryModeS;

    // 支付方式
    private String receiveType;

    // 定金比例
    private BigDecimal receiveRate;

    // 定金
    private BigDecimal receiveRateAmount;

    // 付全款日期
    private Date receiveFullTime;

    // 交货方式
    private String sellDeliveryType;

    // 交货日期
    private Date sellDeliveryDate;

    // 交货期日 补充字段
    private String sArrivalTimeExt;

    // 交货地点
    private String deliveryAddr;

    // 详细地点
    private String sContactAddr;

    // 仓储费(元)
    private BigDecimal swarehouseCost;

    // 运输费(元)
    private BigDecimal stransportCost;

    // 加价
    private BigDecimal premium;

    // 销售价
    private BigDecimal sdealPrice;

    // 销售总价
    private BigDecimal stotalAmount;

    // 销售 补充协议
    private String sExtraTerm;

    // 备注
    private String receiveRemark;

    // 销售合同地址
    private String sellContentTemplateId;

    // 回款周期
    private Integer creditDays;

    private String settlementType;

    // 销售==========================================================end

    // 服务合同
    private Boolean serviceFlg;

    // 服务合同我方抬头
    private String serviceOurCompanyName;

    // 服务合同号
    private String serviceContractNo;

    // 资金服务费
    private BigDecimal serviceAmount;

    // 争议原因
    private String disputeReason;

    // 争议处理
    private String disputeHandle;

    //部门Id
    private Long deptId;

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

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Long getBuyContractId() {
        return buyContractId;
    }

    public void setBuyContractId(Long buyContractId) {
        this.buyContractId = buyContractId;
    }

    public Long getSellContractId() {
        return sellContractId;
    }

    public void setSellContractId(Long sellContractId) {
        this.sellContractId = sellContractId;
    }

    public String getBuyContractNo() {
        return buyContractNo;
    }

    public void setBuyContractNo(String buyContractNo) {
        this.buyContractNo = buyContractNo;
    }

    public String getSellContractNo() {
        return sellContractNo;
    }

    public void setSellContractNo(String sellContractNo) {
        this.sellContractNo = sellContractNo;
    }

    public String getCurBuyContractStatus() {
        return curBuyContractStatus;
    }

    public void setCurBuyContractStatus(String curBuyContractStatus) {
        this.curBuyContractStatus = curBuyContractStatus;
    }

    public String getCurSellContractStatus() {
        return curSellContractStatus;
    }

    public void setCurSellContractStatus(String curSellContractStatus) {
        this.curSellContractStatus = curSellContractStatus;
    }

    public String getFileId() {
        return fileId;
    }

    @Override
    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public String getOurCompanyName() {
        return ourCompanyName;
    }

    public void setOurCompanyName(String ourCompanyName) {
        this.ourCompanyName = ourCompanyName;
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

    public BigDecimal getDealNumber() {
        return dealNumber;
    }

    public void setDealNumber(BigDecimal dealNumber) {
        this.dealNumber = dealNumber;
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

    public String getBuySource() {
        return buySource;
    }

    public void setBuySource(String buySource) {
        this.buySource = buySource;
    }

    public String getBuyCompanyName() {
        return buyCompanyName;
    }

    public void setBuyCompanyName(String buyCompanyName) {
        this.buyCompanyName = buyCompanyName;
    }

    public String getBuyMatchUserName() {
        return buyMatchUserName;
    }

    public void setBuyMatchUserName(String buyMatchUserName) {
        this.buyMatchUserName = buyMatchUserName;
    }

    public String getDeliveryModeB() {
        return deliveryModeB;
    }

    public void setDeliveryModeB(String deliveryModeB) {
        this.deliveryModeB = deliveryModeB;
    }

    public String getPayType() {
        return payType;
    }

    public void setPayType(String payType) {
        this.payType = payType;
    }

    public BigDecimal getPayRate() {
        return payRate;
    }

    public void setPayRate(BigDecimal payRate) {
        this.payRate = payRate;
    }

    public BigDecimal getPayRateAmount() {
        return payRateAmount;
    }

    public void setPayRateAmount(BigDecimal payRateAmount) {
        this.payRateAmount = payRateAmount;
    }

    public Date getPayFullTime() {
        return payFullTime;
    }

    public void setPayFullTime(Date payFullTime) {
        this.payFullTime = payFullTime;
    }

    public String getBuyDeliveryType() {
        return buyDeliveryType;
    }

    public void setBuyDeliveryType(String buyDeliveryType) {
        this.buyDeliveryType = buyDeliveryType;
    }

    public Date getBuyDeliveryDate() {
        return buyDeliveryDate;
    }

    public void setBuyDeliveryDate(Date buyDeliveryDate) {
        this.buyDeliveryDate = buyDeliveryDate;
    }

    public String getbArrivalTimeExt() {
        return bArrivalTimeExt;
    }

    public void setbArrivalTimeExt(String bArrivalTimeExt) {
        this.bArrivalTimeExt = bArrivalTimeExt;
    }

    public String getBdeliveryAddr() {
        return bdeliveryAddr;
    }

    public void setBdeliveryAddr(String bdeliveryAddr) {
        this.bdeliveryAddr = bdeliveryAddr;
    }

    public String getContactAddr() {
        return contactAddr;
    }

    public void setContactAddr(String contactAddr) {
        this.contactAddr = contactAddr;
    }

    public BigDecimal getBwarehouseCost() {
        return bwarehouseCost;
    }

    public void setBwarehouseCost(BigDecimal bwarehouseCost) {
        this.bwarehouseCost = bwarehouseCost;
    }

    public BigDecimal getBtransportCost() {
        return btransportCost;
    }

    public void setBtransportCost(BigDecimal btransportCost) {
        this.btransportCost = btransportCost;
    }

    public BigDecimal getBdealPrice() {
        return bdealPrice;
    }

    public void setBdealPrice(BigDecimal bdealPrice) {
        this.bdealPrice = bdealPrice;
    }

    public BigDecimal getBdealAmountNotax() {
        return bdealAmountNotax;
    }

    public void setBdealAmountNotax(BigDecimal bdealAmountNotax) {
        this.bdealAmountNotax = bdealAmountNotax;
    }

    public BigDecimal getBtotalAmount() {
        return btotalAmount;
    }

    public void setBtotalAmount(BigDecimal btotalAmount) {
        this.btotalAmount = btotalAmount;
    }

    public String getExtraTerm() {
        return extraTerm;
    }

    public void setExtraTerm(String extraTerm) {
        this.extraTerm = extraTerm;
    }

    public String getPayRemark() {
        return payRemark;
    }

    public void setPayRemark(String payRemark) {
        this.payRemark = payRemark;
    }

    public String getBuyContentTemplateId() {
        return buyContentTemplateId;
    }

    public void setBuyContentTemplateId(String buyContentTemplateId) {
        this.buyContentTemplateId = buyContentTemplateId;
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

    public String getDeliveryModeS() {
        return deliveryModeS;
    }

    public void setDeliveryModeS(String deliveryModeS) {
        this.deliveryModeS = deliveryModeS;
    }

    public String getReceiveType() {
        return receiveType;
    }

    public void setReceiveType(String receiveType) {
        this.receiveType = receiveType;
    }

    public BigDecimal getReceiveRate() {
        return receiveRate;
    }

    public void setReceiveRate(BigDecimal receiveRate) {
        this.receiveRate = receiveRate;
    }

    public BigDecimal getReceiveRateAmount() {
        return receiveRateAmount;
    }

    public void setReceiveRateAmount(BigDecimal receiveRateAmount) {
        this.receiveRateAmount = receiveRateAmount;
    }

    public Date getReceiveFullTime() {
        return receiveFullTime;
    }

    public void setReceiveFullTime(Date receiveFullTime) {
        this.receiveFullTime = receiveFullTime;
    }

    public String getSellDeliveryType() {
        return sellDeliveryType;
    }

    public void setSellDeliveryType(String sellDeliveryType) {
        this.sellDeliveryType = sellDeliveryType;
    }

    public Date getSellDeliveryDate() {
        return sellDeliveryDate;
    }

    public void setSellDeliveryDate(Date sellDeliveryDate) {
        this.sellDeliveryDate = sellDeliveryDate;
    }


    public String getsArrivalTimeExt() {
        return sArrivalTimeExt;
    }

    public void setsArrivalTimeExt(String sArrivalTimeExt) {
        this.sArrivalTimeExt = sArrivalTimeExt;
    }

    public String getDeliveryAddr() {
        return deliveryAddr;
    }

    public void setDeliveryAddr(String deliveryAddr) {
        this.deliveryAddr = deliveryAddr;
    }

    public String getsContactAddr() {
        return sContactAddr;
    }

    public void setsContactAddr(String sContactAddr) {
        this.sContactAddr = sContactAddr;
    }

    public BigDecimal getSwarehouseCost() {
        return swarehouseCost;
    }

    public void setSwarehouseCost(BigDecimal swarehouseCost) {
        this.swarehouseCost = swarehouseCost;
    }

    public BigDecimal getStransportCost() {
        return stransportCost;
    }

    public void setStransportCost(BigDecimal stransportCost) {
        this.stransportCost = stransportCost;
    }

    public BigDecimal getPremium() {
        return premium;
    }

    public void setPremium(BigDecimal premium) {
        this.premium = premium;
    }

    public BigDecimal getSdealPrice() {
        return sdealPrice;
    }

    public void setSdealPrice(BigDecimal sdealPrice) {
        this.sdealPrice = sdealPrice;
    }

    public BigDecimal getStotalAmount() {
        return stotalAmount;
    }

    public void setStotalAmount(BigDecimal stotalAmount) {
        this.stotalAmount = stotalAmount;
    }

    public String getsExtraTerm() {
        return sExtraTerm;
    }

    public void setsExtraTerm(String sExtraTerm) {
        this.sExtraTerm = sExtraTerm;
    }

    public String getReceiveRemark() {
        return receiveRemark;
    }

    public void setReceiveRemark(String receiveRemark) {
        this.receiveRemark = receiveRemark;
    }

    public String getSellContentTemplateId() {
        return sellContentTemplateId;
    }

    public void setSellContentTemplateId(String sellContentTemplateId) {
        this.sellContentTemplateId = sellContentTemplateId;
    }

    public Integer getCreditDays() {
        return creditDays;
    }

    public void setCreditDays(Integer creditDays) {
        this.creditDays = creditDays;
    }

    public String getDisputeReason() {
        return disputeReason;
    }

    public void setDisputeReason(String disputeReason) {
        this.disputeReason = disputeReason;
    }

    public String getDisputeHandle() {
        return disputeHandle;
    }

    public void setDisputeHandle(String disputeHandle) {
        this.disputeHandle = disputeHandle;
    }

    public Boolean getServiceFlg() {
        return serviceFlg;
    }

    public void setServiceFlg(Boolean serviceFlg) {
        this.serviceFlg = serviceFlg;
    }

    public String getServiceOurCompanyName() {
        return serviceOurCompanyName;
    }

    public void setServiceOurCompanyName(String serviceOurCompanyName) {
        this.serviceOurCompanyName = serviceOurCompanyName;
    }

    public String getServiceContractNo() {
        return serviceContractNo;
    }

    public void setServiceContractNo(String serviceContractNo) {
        this.serviceContractNo = serviceContractNo;
    }

    public BigDecimal getServiceAmount() {
        return serviceAmount;
    }

    public void setServiceAmount(BigDecimal serviceAmount) {
        this.serviceAmount = serviceAmount;
    }

    public String getSettlementType() {
        return settlementType;
    }

    public void setSettlementType(String settlementType) {
        this.settlementType = settlementType;
    }

    public Long getApproveContentId() {
        return approveContentId;
    }

    public void setApproveContentId(Long approveContentId) {
        this.approveContentId = approveContentId;
    }

    public Long getDeptId() {
        return deptId;
    }

    public void setDeptId(Long deptId) {
        this.deptId = deptId;
    }
}
