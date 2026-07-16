package com.spt.bas.client.entity;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.spt.tools.jpa.vo.IdEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 风控利润统计汇总表
 *
 * @author MoonLight
 */
@Entity
@DynamicInsert
@DynamicUpdate
@Table(name = "t_ctr_contract_profit ")
public class CtrContractProfit extends IdEntity {

    /**
     * 预算编号
     */
    private String approveNo;

    private String businessType;

    /**
     * 预算id
     */
    private Long approveId;

    /**
     * 品名
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
    /**
     * 合同数量
     */
    private BigDecimal totalNumber;
    /**
     * 我方抬头
     */
    private String ourCompanyName;
    /**
     * 签订日
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date contractTime;
    /**
     * 业务员
     */
    private String matchUserName;
    /**
     * 采购合同状态
     */
    private String buyContractStatus;
    /**
     * 销售合同状态
     */
    private String sellContractStatus;
    /**
     * 数据级别
     * 数字越小级别越高，根据审批单号分组，默认取优先级最高的。
     * (0-主要）
     */
    private Long level = 0L;
    /**
     * 采购合同编号
     */
    private String buyContractNo;
    /**
     * 供应商
     */
    private String buyCompanyName;
    /**
     * 采购交货方式
     */
    private String buyDeliveryType;
    /**
     * 采购交货地址
     */
    private String buyDeliveryAddr;
    /**
     * 采购单价
     */
    private BigDecimal buyPrice;
    /**
     * 采购合同金额
     */
    private BigDecimal buyTotalAmount;
    /**
     * 应付余额
     */
    private BigDecimal balancePayable;
    /**
     * 付款金额
     */
    private BigDecimal payAmount;
    /**
     * 付全款日期
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date payFullDate;
    /**
     * 收票金额
     */
    private BigDecimal receiptBillAmount;
    /**
     * 收票日期
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date receiptBillDate;
    /**
     * 入库时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date deliveryInDate;
    /**
     * 入库数量
     */
    private BigDecimal deliveryInNumber = BigDecimal.ZERO;
    /**
     * 采购定金比例
     */
    private BigDecimal buyBondRate;
    /**
     * 采购定金金额
     */
    private BigDecimal buyBondAmount;
    /**
     * 销售合同编号
     */
    private String sellContractNo;
    /**
     * 采购商
     */
    private String sellCompanyName;
    /**
     * 销售交货方式
     */
    private String sellDeliveryType;
    /**
     * 销售交货地址
     */
    private String sellDeliveryAddr;
    /**
     * 销售合同金额
     */
    private BigDecimal sellTotalAmount;
    /**
     * 销售单价
     */
    private BigDecimal sellPrice;
    /**
     * 收款金额
     */
    private BigDecimal receiveAmount = BigDecimal.ZERO;
    /**
     * 约定付全款日期
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date appointPayFullTime;
    /**
     * 销售收全款日期
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date sellPayFullDate;
    /**
     * 回款周期
     */
    private Long creditDays;
    /**
     * 开票金额
     */
    private BigDecimal invoiceBillAmount = BigDecimal.ZERO;
    /**
     * 开票日期
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date invoiceBillDate;
    /**
     * 出库时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date deliveryOutDate;
    /**
     * 出库数量
     */
    private BigDecimal deliveryOutNumber;
    /**
     * 确认收货时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date confirmDate;
    /**
     * 应收余额
     */
    private BigDecimal balanceReceivable;
    /**
     * 销售定金比例
     */
    private BigDecimal sellBondRate;
    /**
     * 销售定金金额
     */
    private BigDecimal sellBondAmount;

    /**
     * 业务员id
     */
    private Long matchUserId;

    /**
     * 已收逾期罚息
     */
    private BigDecimal receiveBreachAmount = BigDecimal.ZERO;

    /**
     * 逾期罚息
     */
    private BigDecimal breachAmount = BigDecimal.ZERO;

    /**
     * 逾期天数
     */
    private Long breachDays = 0L;

    /**
     * 确认收货数量
     */
    private BigDecimal confirmReceiveNumber = BigDecimal.ZERO;
    /**
     * 是否已通过盖章审核
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Boolean sealFlg = false;

    /**
     * 合同类型
     * 1-代采
     * 2-赊销
     * 5-代采赊销
     */
    private String profitType;

    /**
     * 发票状态
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Boolean billFlg = false;



    /**
     * 采购来源 B:自营采购 G:供应商
     */
    private String buySource;


    /**
     * 支付方式
     */
    private String payMode;


    /**
     * 结算方式 （背靠背白条业务专用） 0：赊销（一票制）  1：赊销（两票制）
     */
    private String settlementType;/**


    /**
     * 交货方式
     */
    private String deliveryMode;

    /**
     * 交货日期
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date deliveryDate;

    /**
     * 銷售交货日期
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date sellDeliveryDate;

    /**
     * 采购详细地址
     */
    private String buyContactAddr;


    /**
     * 销售详细地址
     */
    private String sellContactAddr;

    private	BigDecimal	transportAmount = BigDecimal.ZERO;	//采购运输费
    private	BigDecimal	warehouseAmount = BigDecimal.ZERO;	//采购仓储费

    private	BigDecimal	sellTransportAmount = BigDecimal.ZERO;	//銷售运输费
    private	BigDecimal	sellWarehouseAmount = BigDecimal.ZERO;	//銷售仓储费

    /**
     * 不含税单价
     */
    private BigDecimal dealAmountNoTax;

    /**
     * 补充条款
     */
    private String sellExtraTerm;

    /**
     * 备注
     */
    private String sellRemark;

    /**
     * 补充条款
     */
    private String BuyExtraTerm;

    /**
     * 备注
     */
    private String buyRemark;

    /**
     * 资金服务费
     */
    private BigDecimal serviceAmount;
    /**
     * 加价
     */
    private BigDecimal premium;

    /**
     * 上传采购合同附件ID
     */
    private String buyContentFileId;

    /**
     * 上传销售合同附件ID
     */
    private String sellContentFileId;

    /**
     * 服务合同ID
     */
    private String serviceContractId;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date realPayDate;// 实际付款日期

    /**
     * 销售结算方式
     */
    private String sellSettlementType;

    /**
     * 附加交货时间
     */
    private String attachDeliveryTime;

    public String getAttachDeliveryTime() {
        return attachDeliveryTime;
    }

    public void setAttachDeliveryTime(String attachDeliveryTime) {
        this.attachDeliveryTime = attachDeliveryTime;
    }

    public String getServiceContractId() {
        return serviceContractId;
    }

    public void setServiceContractId(String serviceContractId) {
        this.serviceContractId = serviceContractId;
    }

    public String getSellSettlementType() {
        return sellSettlementType;
    }

    public void setSellSettlementType(String sellSettlementType) {
        this.sellSettlementType = sellSettlementType;
    }

    public Date getRealPayDate() {
        return realPayDate;
    }

    public void setRealPayDate(Date realPayDate) {
        this.realPayDate = realPayDate;
    }

    public String getBuySource() {
        return buySource;
    }

    public void setBuySource(String buySource) {
        this.buySource = buySource;
    }

    public String getPayMode() {
        return payMode;
    }

    public void setPayMode(String payMode) {
        this.payMode = payMode;
    }

    public String getSettlementType() {
        return settlementType;
    }

    public void setSettlementType(String settlementType) {
        this.settlementType = settlementType;
    }

    public String getDeliveryMode() {
        return deliveryMode;
    }

    public void setDeliveryMode(String deliveryMode) {
        this.deliveryMode = deliveryMode;
    }

    public Date getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(Date deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public Date getSellDeliveryDate() {
        return sellDeliveryDate;
    }

    public void setSellDeliveryDate(Date sellDeliveryDate) {
        this.sellDeliveryDate = sellDeliveryDate;
    }


    public String getSellContactAddr() {
        return sellContactAddr;
    }

    public void setSellContactAddr(String sellContactAddr) {
        this.sellContactAddr = sellContactAddr;
    }

    public BigDecimal getTransportAmount() {
        return transportAmount;
    }

    public void setTransportAmount(BigDecimal transportAmount) {
        this.transportAmount = transportAmount;
    }

    public BigDecimal getWarehouseAmount() {
        return warehouseAmount;
    }

    public void setWarehouseAmount(BigDecimal warehouseAmount) {
        this.warehouseAmount = warehouseAmount;
    }

    public BigDecimal getSellTransportAmount() {
        return sellTransportAmount;
    }

    public void setSellTransportAmount(BigDecimal sellTransportAmount) {
        this.sellTransportAmount = sellTransportAmount;
    }

    public BigDecimal getSellWarehouseAmount() {
        return sellWarehouseAmount;
    }

    public void setSellWarehouseAmount(BigDecimal sellWarehouseAmount) {
        this.sellWarehouseAmount = sellWarehouseAmount;
    }

    public BigDecimal getDealAmountNoTax() {
        return dealAmountNoTax;
    }

    public void setDealAmountNoTax(BigDecimal dealAmountNoTax) {
        this.dealAmountNoTax = dealAmountNoTax;
    }

    public String getSellExtraTerm() {
        return sellExtraTerm;
    }

    public void setSellExtraTerm(String sellExtraTerm) {
        this.sellExtraTerm = sellExtraTerm;
    }



    public String getBuyExtraTerm() {
        return BuyExtraTerm;
    }

    public void setBuyExtraTerm(String buyExtraTerm) {
        BuyExtraTerm = buyExtraTerm;
    }

    public String getBuyContactAddr() {
        return buyContactAddr;
    }

    public void setBuyContactAddr(String buyContactAddr) {
        this.buyContactAddr = buyContactAddr;
    }

    public String getSellRemark() {
        return sellRemark;
    }

    public void setSellRemark(String sellRemark) {
        this.sellRemark = sellRemark;
    }

    public String getBuyRemark() {
        return buyRemark;
    }

    public void setBuyRemark(String buyRemark) {
        this.buyRemark = buyRemark;
    }

    public String getBuyContentFileId() {
        return buyContentFileId;
    }

    public void setBuyContentFileId(String buyContentFileId) {
        this.buyContentFileId = buyContentFileId;
    }

    public String getSellContentFileId() {
        return sellContentFileId;
    }

    public void setSellContentFileId(String sellContentFileId) {
        this.sellContentFileId = sellContentFileId;
    }

    public BigDecimal getServiceAmount() {
        return serviceAmount;
    }

    public void setServiceAmount(BigDecimal serviceAmount) {
        this.serviceAmount = serviceAmount;
    }

    public BigDecimal getPremium() {
        return premium;
    }

    public void setPremium(BigDecimal premium) {
        this.premium = premium;
    }

    public String getApproveNo() {
        return approveNo;
    }

    public void setApproveNo(String approveNo) {
        this.approveNo = approveNo;
    }

    public String getBusinessType() {
        return businessType;
    }

    public void setBusinessType(String businessType) {
        this.businessType = businessType;
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

    public BigDecimal getTotalNumber() {
        return totalNumber;
    }

    public void setTotalNumber(BigDecimal totalNumber) {
        this.totalNumber = totalNumber;
    }

    public String getOurCompanyName() {
        return ourCompanyName;
    }

    public void setOurCompanyName(String ourCompanyName) {
        this.ourCompanyName = ourCompanyName;
    }

    public Date getContractTime() {
        return contractTime;
    }

    public void setContractTime(Date contractTime) {
        this.contractTime = contractTime;
    }

    public String getMatchUserName() {
        return matchUserName;
    }

    public void setMatchUserName(String matchUserName) {
        this.matchUserName = matchUserName;
    }

    public Long getLevel() {
        return level;
    }

    public void setLevel(Long level) {
        this.level = level;
    }

    public String getBuyContractNo() {
        return buyContractNo;
    }

    public void setBuyContractNo(String buyContractNo) {
        this.buyContractNo = buyContractNo;
    }

    public String getBuyCompanyName() {
        return buyCompanyName;
    }

    public void setBuyCompanyName(String buyCompanyName) {
        this.buyCompanyName = buyCompanyName;
    }

    public String getBuyDeliveryType() {
        return buyDeliveryType;
    }

    public void setBuyDeliveryType(String buyDeliveryType) {
        this.buyDeliveryType = buyDeliveryType;
    }

    public String getBuyDeliveryAddr() {
        return buyDeliveryAddr;
    }

    public void setBuyDeliveryAddr(String buyDeliveryAddr) {
        this.buyDeliveryAddr = buyDeliveryAddr;
    }

    public BigDecimal getBuyPrice() {
        return buyPrice;
    }

    public void setBuyPrice(BigDecimal buyPrice) {
        this.buyPrice = buyPrice;
    }

    public BigDecimal getBuyTotalAmount() {
        return buyTotalAmount;
    }

    public void setBuyTotalAmount(BigDecimal buyTotalAmount) {
        this.buyTotalAmount = buyTotalAmount;
    }

    public BigDecimal getBalancePayable() {
        return balancePayable;
    }

    public void setBalancePayable(BigDecimal balancePayable) {
        this.balancePayable = balancePayable;
    }

    public BigDecimal getPayAmount() {
        return payAmount;
    }

    public void setPayAmount(BigDecimal payAmount) {
        this.payAmount = payAmount;
    }

    public Date getPayFullDate() {
        return payFullDate;
    }

    public void setPayFullDate(Date payFullDate) {
        this.payFullDate = payFullDate;
    }

    public BigDecimal getReceiptBillAmount() {
        return receiptBillAmount;
    }

    public void setReceiptBillAmount(BigDecimal receiptBillAmount) {
        this.receiptBillAmount = receiptBillAmount;
    }

    public Date getReceiptBillDate() {
        return receiptBillDate;
    }

    public void setReceiptBillDate(Date receiptBillDate) {
        this.receiptBillDate = receiptBillDate;
    }

    public Date getDeliveryInDate() {
        return deliveryInDate;
    }

    public void setDeliveryInDate(Date deliveryInDate) {
        this.deliveryInDate = deliveryInDate;
    }

    public BigDecimal getDeliveryInNumber() {
        return deliveryInNumber;
    }

    public void setDeliveryInNumber(BigDecimal deliveryInNumber) {
        this.deliveryInNumber = deliveryInNumber;
    }

    public BigDecimal getBuyBondRate() {
        return buyBondRate;
    }

    public void setBuyBondRate(BigDecimal buyBondRate) {
        this.buyBondRate = buyBondRate;
    }

    public BigDecimal getBuyBondAmount() {
        return buyBondAmount;
    }

    public void setBuyBondAmount(BigDecimal buyBondAmount) {
        this.buyBondAmount = buyBondAmount;
    }

    public String getSellContractNo() {
        return sellContractNo;
    }

    public void setSellContractNo(String sellContractNo) {
        this.sellContractNo = sellContractNo;
    }

    public String getSellCompanyName() {
        return sellCompanyName;
    }

    public void setSellCompanyName(String sellCompanyName) {
        this.sellCompanyName = sellCompanyName;
    }

    public String getSellDeliveryType() {
        return sellDeliveryType;
    }

    public void setSellDeliveryType(String sellDeliveryType) {
        this.sellDeliveryType = sellDeliveryType;
    }

    public String getSellDeliveryAddr() {
        return sellDeliveryAddr;
    }

    public void setSellDeliveryAddr(String sellDeliveryAddr) {
        this.sellDeliveryAddr = sellDeliveryAddr;
    }

    public BigDecimal getSellTotalAmount() {
        return sellTotalAmount;
    }

    public void setSellTotalAmount(BigDecimal sellTotalAmount) {
        this.sellTotalAmount = sellTotalAmount;
    }

    public BigDecimal getSellPrice() {
        return sellPrice;
    }

    public void setSellPrice(BigDecimal sellPrice) {
        this.sellPrice = sellPrice;
    }

    public BigDecimal getReceiveAmount() {
        return receiveAmount;
    }

    public void setReceiveAmount(BigDecimal receiveAmount) {
        this.receiveAmount = receiveAmount;
    }

    public Long getCreditDays() {
        return creditDays;
    }

    public void setCreditDays(Long creditDays) {
        this.creditDays = creditDays;
    }

    public BigDecimal getInvoiceBillAmount() {
        return invoiceBillAmount;
    }

    public void setInvoiceBillAmount(BigDecimal invoiceBillAmount) {
        this.invoiceBillAmount = invoiceBillAmount;
    }

    public Date getInvoiceBillDate() {
        return invoiceBillDate;
    }

    public void setInvoiceBillDate(Date invoiceBillDate) {
        this.invoiceBillDate = invoiceBillDate;
    }

    public Date getDeliveryOutDate() {
        return deliveryOutDate;
    }

    public void setDeliveryOutDate(Date deliveryOutDate) {
        this.deliveryOutDate = deliveryOutDate;
    }

    public BigDecimal getDeliveryOutNumber() {
        return deliveryOutNumber;
    }

    public void setDeliveryOutNumber(BigDecimal deliveryOutNumber) {
        this.deliveryOutNumber = deliveryOutNumber;
    }

    public Date getConfirmDate() {
        return confirmDate;
    }

    public void setConfirmDate(Date confirmDate) {
        this.confirmDate = confirmDate;
    }

    public BigDecimal getBalanceReceivable() {
        return balanceReceivable;
    }

    public void setBalanceReceivable(BigDecimal balanceReceivable) {
        this.balanceReceivable = balanceReceivable;
    }

    public BigDecimal getSellBondRate() {
        return sellBondRate;
    }

    public void setSellBondRate(BigDecimal sellBondRate) {
        this.sellBondRate = sellBondRate;
    }

    public BigDecimal getSellBondAmount() {
        return sellBondAmount;
    }

    public void setSellBondAmount(BigDecimal sellBondAmount) {
        this.sellBondAmount = sellBondAmount;
    }

    public Long getMatchUserId() {
        return matchUserId;
    }

    public void setMatchUserId(Long matchUserId) {
        this.matchUserId = matchUserId;
    }

    public BigDecimal getReceiveBreachAmount() {
        return receiveBreachAmount;
    }

    public void setReceiveBreachAmount(BigDecimal receiveBreachAmount) {
        this.receiveBreachAmount = receiveBreachAmount;
    }

    public BigDecimal getBreachAmount() {
        return breachAmount;
    }

    public void setBreachAmount(BigDecimal breachAmount) {
        this.breachAmount = breachAmount;
    }

    public Long getBreachDays() {
        return breachDays;
    }

    public void setBreachDays(Long breachDays) {
        this.breachDays = breachDays;
    }

    public BigDecimal getConfirmReceiveNumber() {
        return confirmReceiveNumber;
    }

    public void setConfirmReceiveNumber(BigDecimal confirmReceiveNumber) {
        this.confirmReceiveNumber = confirmReceiveNumber;
    }

    public Boolean getSealFlg() {
        return sealFlg;
    }

    public void setSealFlg(Boolean sealFlg) {
        this.sealFlg = sealFlg;
    }


    public Date getSellPayFullDate() {
        return sellPayFullDate;
    }

    public void setSellPayFullDate(Date sellPayFullDate) {
        this.sellPayFullDate = sellPayFullDate;
    }

    public Boolean getBillFlg() {
        return billFlg;
    }

    public void setBillFlg(Boolean billFlg) {
        this.billFlg = billFlg;
    }

    public Date getAppointPayFullTime() {
        return appointPayFullTime;
    }

    public void setAppointPayFullTime(Date appointPayFullTime) {
        this.appointPayFullTime = appointPayFullTime;
    }

    public String getBuyContractStatus() {
        return buyContractStatus;
    }

    public void setBuyContractStatus(String buyContractStatus) {
        this.buyContractStatus = buyContractStatus;
    }

    public String getSellContractStatus() {
        return sellContractStatus;
    }

    public void setSellContractStatus(String sellContractStatus) {
        this.sellContractStatus = sellContractStatus;
    }

    public String getProfitType() {
        return profitType;
    }

    public void setProfitType(String profitType) {
        this.profitType = profitType;
    }

    public Long getApproveId() {
        return approveId;
    }

    public void setApproveId(Long approveId) {
        this.approveId = approveId;
    }
}