package com.spt.bas.client.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.spt.bas.client.entity.ApplyProductDetail;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class ApplyMatchDetailVo {
    private Long id;
    /**
     * 类型		B-采购，S-销售
     */
    private String contractType;
    /**
     * 产品类型		现货N、期货F
     */
    private String productType;

    /**
     * 到货时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date arrivalTime;

    /**
     * 付款时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date payBondTime;

    /**
     * 付全款时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date payFullTime;

    /**
     * 付款金额
     */
    private BigDecimal payBondAmount;

    /**
     * 付款方式		现金cash、信用证credit、承兑-accept
     */
    private String payType;

    /**
     * 付款比例
     */
    private BigDecimal payRate;

    /**
     * 付款备注
     */
    private String payRemark;

    /**
     * 收款方式		现金cash、信用证credit、承兑-accept
     */
    private String receiveType;

    /**
     * 收定金时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date receiveBondTime;

    /**
     * 收全款时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date receiveFullTime;

    /**
     * 交货时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date deliveryTime;

    /**
     * 收款金额
     */
    private BigDecimal receiveBondAmount;

    /**
     * 收款比例
     */
    private BigDecimal receiveRate;

    /**
     * 收款备注
     */
    private String receiveRemark;

    /**
     * 交货方式		款到发货-XKHH、款到发货分批-XKHHFP、货到付款-XHHK
     */
    private String deliveryMode;

    /**
     * 配送方式		自提-ZT、配送-PS
     */
    private String deliveryType;

    /**
     * 仓储费
     */
    private BigDecimal warehouseCost;

    /**
     * 仓库ID
     */
    private Long warehouseId;

    /**
     * 仓库
     */
    private String warehouse;

    /**
     * 仓库电话
     */
    private String warehousePhone;

    /**
     * 仓库配送地址
     */
    private String warehouseAddr;

    /**
     * 仓库名
     */
    private String warehouseName;

    /**
     * 运输费
     */
    private BigDecimal transportCost;

    /**
     * 申请状态		状态 'N-新增，A-审批中，B-驳回，D-完成'
     */
    private String status;

    /**
     * 撮合业务ID
     */
    private Long applyMatchId;

    /**
     * 企业账套ID
     */
    private Long enterpriseId;

    /**
     * 合同ID
     */
    private Long contractId;

    /**
     * 对方公司名称
     */
    private String companyName;

    /**
     * 供货商联系人
     */
    private String contactName;

    /**
     * 联系电话
     */
    private String contactPhone;

    /**
     * 联系地址
     */
    private String contactAddr;

    /**
     * 银行
     */
    private String companyBank;

    /**
     * 公司账号
     */
    private String companyAccount;

    /**
     * 需货商 税号
     */
    private String taxNumber;

    /**
     * 公司ID
     */
    private Long companyId;

    /**
     * 随机码
     */
    private String randomNumber;

    /**
     * 合同编号
     */
    private String contractNo;

    /**
     * 合同属性：N-现货，F-期货
     */
    private String contractAttr;

    /**
     * 业务员ID
     */
    private Long matchUserId;

    /**
     * 业务员名称
     */
    private String matchUserName;

    /**
     * 到货日期(补充)
     */
    private String arrivalTimeExt;

    /**
     * 开票日期
     */
    private String invoiceDate;

    /**
     * 补充条款
     */
    private String extraTerm;

    /**
     * 合同总价
     */
    private BigDecimal totalAmount;

    /**
     * 质量标准
     */
    private String qualityStandard;

    /**
     * 付款方式
     */
    private String payKind;
    private String payKindCode;

    /**
     * 交货地点
     */
    private String deliveryAddr;

    /**
     * 账期
     */
    private Integer creditDays;

    private String buySource;
    private String sellSource;
    private BigDecimal payRateAmount;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date deliveryDate;
    private BigDecimal dealPrice;
    //页面不含税单价
    private BigDecimal dealAmountNotax;
    //不含税单价
    private BigDecimal dealPriceNotax;
    private BigDecimal receiveRateAmount;
    private String serviceType;
    private BigDecimal minDealPrice;
    private BigDecimal serviceAmount;
    private BigDecimal premium;
    private String extraterm;
    /**
     * 采购合同模板ID
     */
    private Long buyTemplateId;

    /**
     * 上传采购合同附件ID
     */
    private String buyContentTemplateId;

    /**
     * 销售合同模板ID
     */
    private Long sellTemplateId;

    /**
     * 上传销售合同附件ID
     */
    private String sellContentTemplateId;

    /**
     * 服务合同模板ID
     */
    private Long serviceTemplateId;
    /**
     * 上传服务合同模板ID
     */
    private String serviceContentTemplateId;
    private String serviceOurCompanyName;

    private String ourCompanyName;

    /**
     * 销售 结算方式 0：一票制 1：两票制
     */
    private String settlementType;

    /**
     * 不含险销售价
     */
    private BigDecimal dealPriceNoInsurance;

    private List<ApplyProductDetail> lstInsert;
    private List<ApplyProductDetail> lstUpdate;
    private List<ApplyProductDetail> lstDelete;


    /**
     * 承运商
     */
    private  String  carrier;

    /**
     * 来源
     *
     */
    private String applySource;

    /**
     * 销售代采方
     */
    private String sellOurCompanyName;


    /**
     * 链条采购单价
     */
    private BigDecimal buyDealPrice;

    /**
     * 链条销售单价
     */
    private BigDecimal sellDealPrice;

    /**
     * 业务类型
     */
    private String businessType;

    /**
     * 裝卸費
     */
    private   BigDecimal stevedorage;

    /**
     * 每吨毛利润
     */
    private BigDecimal  grossProfit;

    /**
     * 省
     */
    private String provinceName;

    /**
     * 市
     */
    private String cityName;

    /**
     * 区
     */
    private String areaCode;

    /**
     * 贴现费用
     */
    private BigDecimal discountAmount;

    /**
     * 贴现利率
     */
    private BigDecimal discountRate;

    /**
     * 是否货到票到
     */
    private Boolean receiptArrivedFlg = false;

    /**
     * 托盘利率
     */
    private BigDecimal tpRate=BigDecimal.ZERO;

    /**
     * 托盘天数
     */
    private Integer tpDays;

    /**
     * 审批中预估托盘利息
     */
    private BigDecimal approveTpInterest;

    public BigDecimal getStevedorage() {
        return stevedorage;
    }

    public void setStevedorage(BigDecimal stevedorage) {
        this.stevedorage = stevedorage;
    }

    public BigDecimal getGrossProfit() {
        return grossProfit;
    }

    public void setGrossProfit(BigDecimal grossProfit) {
        this.grossProfit = grossProfit;
    }

    public BigDecimal getBuyDealPrice() {
        return buyDealPrice;
    }

    public void setBuyDealPrice(BigDecimal buyDealPrice) {
        this.buyDealPrice = buyDealPrice;
    }

    public BigDecimal getSellDealPrice() {
        return sellDealPrice;
    }

    public void setSellDealPrice(BigDecimal sellDealPrice) {
        this.sellDealPrice = sellDealPrice;
    }

    public String getBusinessType() {
        return businessType;
    }

    public void setBusinessType(String businessType) {
        this.businessType = businessType;
    }

    public String getApplySource() {
        return applySource;
    }

    public void setApplySource(String applySource) {
        this.applySource = applySource;
    }

    public String getCarrier() {
        return carrier;
    }

    public void setCarrier(String carrier) {
        this.carrier = carrier;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<ApplyProductDetail> getLstInsert() {
        return lstInsert;
    }

    public void setLstInsert(List<ApplyProductDetail> lstInsert) {
        this.lstInsert = lstInsert;
    }

    public List<ApplyProductDetail> getLstUpdate() {
        return lstUpdate;
    }

    public void setLstUpdate(List<ApplyProductDetail> lstUpdate) {
        this.lstUpdate = lstUpdate;
    }

    public List<ApplyProductDetail> getLstDelete() {
        return lstDelete;
    }

    public void setLstDelete(List<ApplyProductDetail> lstDelete) {
        this.lstDelete = lstDelete;
    }

    @SuppressWarnings("unchecked")
    public void setBatchSub(List<?> lstInsert, List<?> lstUpdate, List<?> lstDelete) {
        setLstInsert((List<ApplyProductDetail>) lstInsert);
        setLstUpdate((List<ApplyProductDetail>) lstUpdate);
        setLstDelete((List<ApplyProductDetail>) lstDelete);
    }

    public String getContractType() {
        return contractType;
    }

    public void setContractType(String contractType) {
        this.contractType = contractType;
    }

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    public Date getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(Date arrivalTime) {
        this.arrivalTime = arrivalTime;
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

    public String getPayRemark() {
        return payRemark;
    }

    public void setPayRemark(String payRemark) {
        this.payRemark = payRemark;
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

    public String getReceiveRemark() {
        return receiveRemark;
    }

    public void setReceiveRemark(String receiveRemark) {
        this.receiveRemark = receiveRemark;
    }

    public String getDeliveryMode() {
        return deliveryMode;
    }

    public void setDeliveryMode(String deliveryMode) {
        this.deliveryMode = deliveryMode;
    }

    public String getDeliveryType() {
        return deliveryType;
    }

    public void setDeliveryType(String deliveryType) {
        this.deliveryType = deliveryType;
    }

    public BigDecimal getWarehouseCost() {
        return warehouseCost;
    }

    public void setWarehouseCost(BigDecimal warehouseCost) {
        this.warehouseCost = warehouseCost;
    }

    public Long getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(Long warehouseId) {
        this.warehouseId = warehouseId;
    }

    public String getWarehouse() {
        return warehouse;
    }

    public void setWarehouse(String warehouse) {
        this.warehouse = warehouse;
    }

    public String getWarehousePhone() {
        return warehousePhone;
    }

    public void setWarehousePhone(String warehousePhone) {
        this.warehousePhone = warehousePhone;
    }

    public String getWarehouseAddr() {
        return warehouseAddr;
    }

    public void setWarehouseAddr(String warehouseAddr) {
        this.warehouseAddr = warehouseAddr;
    }

    public BigDecimal getTransportCost() {
        return transportCost;
    }

    public void setTransportCost(BigDecimal transportCost) {
        this.transportCost = transportCost;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getApplyMatchId() {
        return applyMatchId;
    }

    public void setApplyMatchId(Long applyMatchId) {
        this.applyMatchId = applyMatchId;
    }

    public Long getEnterpriseId() {
        return enterpriseId;
    }

    public void setEnterpriseId(Long enterpriseId) {
        this.enterpriseId = enterpriseId;
    }

    public Long getContractId() {
        return contractId;
    }

    public void setContractId(Long contractId) {
        this.contractId = contractId;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public String getContactAddr() {
        return contactAddr;
    }

    public void setContactAddr(String contactAddr) {
        this.contactAddr = contactAddr;
    }

    public String getCompanyBank() {
        return companyBank;
    }

    public void setCompanyBank(String companyBank) {
        this.companyBank = companyBank;
    }

    public String getCompanyAccount() {
        return companyAccount;
    }

    public void setCompanyAccount(String companyAccount) {
        this.companyAccount = companyAccount;
    }

    public String getTaxNumber() {
        return taxNumber;
    }

    public void setTaxNumber(String taxNumber) {
        this.taxNumber = taxNumber;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public String getRandomNumber() {
        return randomNumber;
    }

    public void setRandomNumber(String randomNumber) {
        this.randomNumber = randomNumber;
    }

    public String getContractNo() {
        return contractNo;
    }

    public void setContractNo(String contractNo) {
        this.contractNo = contractNo;
    }

    public String getContractAttr() {
        return contractAttr;
    }

    public void setContractAttr(String contractAttr) {
        this.contractAttr = contractAttr;
    }

    public Long getMatchUserId() {
        return matchUserId;
    }

    public void setMatchUserId(Long matchUserId) {
        this.matchUserId = matchUserId;
    }

    public String getMatchUserName() {
        return matchUserName;
    }

    public void setMatchUserName(String matchUserName) {
        this.matchUserName = matchUserName;
    }

    public Date getPayBondTime() {
        return payBondTime;
    }

    public void setPayBondTime(Date payBondTime) {
        this.payBondTime = payBondTime;
    }

    public Date getPayFullTime() {
        return payFullTime;
    }

    public void setPayFullTime(Date payFullTime) {
        this.payFullTime = payFullTime;
    }

    public BigDecimal getPayBondAmount() {
        return Objects.isNull(payBondAmount) ? BigDecimal.ZERO: payBondAmount;
    }

    public void setPayBondAmount(BigDecimal payBondAmount) {
        this.payBondAmount = payBondAmount;
    }

    public Date getReceiveBondTime() {
        return receiveBondTime;
    }

    public void setReceiveBondTime(Date receiveBondTime) {
        this.receiveBondTime = receiveBondTime;
    }

    public Date getReceiveFullTime() {
        return receiveFullTime;
    }

    public void setReceiveFullTime(Date receiveFullTime) {
        this.receiveFullTime = receiveFullTime;
    }

    public BigDecimal getReceiveBondAmount() {
        return Objects.isNull(receiveBondAmount) ? BigDecimal.ZERO : receiveBondAmount;
    }

    public void setReceiveBondAmount(BigDecimal receiveBondAmount) {
        this.receiveBondAmount = receiveBondAmount;
    }

    public Date getDeliveryTime() {
        return deliveryTime;
    }

    public void setDeliveryTime(Date deliveryTime) {
        this.deliveryTime = deliveryTime;
    }

    public String getArrivalTimeExt() {
        return arrivalTimeExt;
    }

    public void setArrivalTimeExt(String arrivalTimeExt) {
        this.arrivalTimeExt = arrivalTimeExt;
    }

    public String getInvoiceDate() {
        return invoiceDate;
    }

    public void setInvoiceDate(String invoiceDate) {
        this.invoiceDate = invoiceDate;
    }

    public String getExtraTerm() {
        return extraTerm;
    }

    public void setExtraTerm(String extraTerm) {
        this.extraTerm = extraTerm;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getQualityStandard() {
        return qualityStandard;
    }

    public void setQualityStandard(String qualityStandard) {
        this.qualityStandard = qualityStandard;
    }

    public String getPayKind() {
        return payKind;
    }

    public void setPayKind(String payKind) {
        this.payKind = payKind;
    }

    public String getDeliveryAddr() {
        return deliveryAddr;
    }

    public void setDeliveryAddr(String deliveryAddr) {
        this.deliveryAddr = deliveryAddr;
    }

    public String getPayKindCode() {
        return payKindCode;
    }

    public void setPayKindCode(String payKindCode) {
        this.payKindCode = payKindCode;
    }

    public Integer getCreditDays() {
        return creditDays;
    }

    public void setCreditDays(Integer creditDays) {
        this.creditDays = creditDays;
    }

    public String getBuySource() {
        return buySource;
    }

    public void setBuySource(String buySource) {
        this.buySource = buySource;
    }

    public String getSellSource() {
        return sellSource;
    }

    public void setSellSource(String sellSource) {
        this.sellSource = sellSource;
    }

    public BigDecimal getPayRateAmount() {
        return payRateAmount;
    }

    public void setPayRateAmount(BigDecimal payRateAmount) {
        this.payRateAmount = payRateAmount;
    }

    public Date getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(Date deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public BigDecimal getDealPrice() {
        return dealPrice;
    }

    public void setDealPrice(BigDecimal dealPrice) {
        this.dealPrice = dealPrice;
    }

    public BigDecimal getDealAmountNotax() {
        return dealAmountNotax;
    }

    public void setDealAmountNotax(BigDecimal dealAmountNotax) {
        this.dealAmountNotax = dealAmountNotax;
    }

    public BigDecimal getReceiveRateAmount() {
        return receiveRateAmount;
    }

    public void setReceiveRateAmount(BigDecimal receiveRateAmount) {
        this.receiveRateAmount = receiveRateAmount;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public BigDecimal getMinDealPrice() {
        return minDealPrice;
    }

    public void setMinDealPrice(BigDecimal minDealPrice) {
        this.minDealPrice = minDealPrice;
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

    public String getExtraterm() {
        return extraterm;
    }

    public void setExtraterm(String extraterm) {
        this.extraterm = extraterm;
    }

    public Long getBuyTemplateId() {
        return buyTemplateId;
    }

    public void setBuyTemplateId(Long buyTemplateId) {
        this.buyTemplateId = buyTemplateId;
    }

    public String getBuyContentTemplateId() {
        return buyContentTemplateId;
    }

    public void setBuyContentTemplateId(String buyContentTemplateId) {
        this.buyContentTemplateId = buyContentTemplateId;
    }

    public Long getSellTemplateId() {
        return sellTemplateId;
    }

    public void setSellTemplateId(Long sellTemplateId) {
        this.sellTemplateId = sellTemplateId;
    }

    public String getSellContentTemplateId() {
        return sellContentTemplateId;
    }

    public void setSellContentTemplateId(String sellContentTemplateId) {
        this.sellContentTemplateId = sellContentTemplateId;
    }

    public Long getServiceTemplateId() {
        return serviceTemplateId;
    }

    public void setServiceTemplateId(Long serviceTemplateId) {
        this.serviceTemplateId = serviceTemplateId;
    }

    public String getServiceContentTemplateId() {
        return serviceContentTemplateId;
    }

    public void setServiceContentTemplateId(String serviceContentTemplateId) {
        this.serviceContentTemplateId = serviceContentTemplateId;
    }

    public String getServiceOurCompanyName() {
        return serviceOurCompanyName;
    }

    public void setServiceOurCompanyName(String serviceOurCompanyName) {
        this.serviceOurCompanyName = serviceOurCompanyName;
    }

    public String getWarehouseName() {
        return warehouseName;
    }

    public void setWarehouseName(String warehouseName) {
        this.warehouseName = warehouseName;
    }

    public String getSettlementType() {
        return settlementType;
    }

    public void setSettlementType(String settlementType) {
        this.settlementType = settlementType;
    }

    public BigDecimal getDealPriceNoInsurance() {
        return dealPriceNoInsurance;
    }

    public void setDealPriceNoInsurance(BigDecimal dealPriceNoInsurance) {
        this.dealPriceNoInsurance = dealPriceNoInsurance;
    }

    public BigDecimal getDealPriceNotax() {
        return dealPriceNotax;
    }

    public void setDealPriceNotax(BigDecimal dealPriceNotax) {
        this.dealPriceNotax = dealPriceNotax;
    }

    public String getOurCompanyName() {
        return ourCompanyName;
    }

    public void setOurCompanyName(String ourCompanyName) {
        this.ourCompanyName = ourCompanyName;
    }

    public String getSellOurCompanyName() {
        return sellOurCompanyName;
    }

    public void setSellOurCompanyName(String sellOurCompanyName) {
        this.sellOurCompanyName = sellOurCompanyName;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getAreaCode() {
        return areaCode;
    }

    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }

    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
    }

    public BigDecimal getDiscountRate() {
        return discountRate;
    }

    public void setDiscountRate(BigDecimal discountRate) {
        this.discountRate = discountRate;
    }

    public Boolean getReceiptArrivedFlg() {
        return receiptArrivedFlg;
    }

    public void setReceiptArrivedFlg(Boolean receiptArrivedFlg) {
        this.receiptArrivedFlg = receiptArrivedFlg;
    }

    public BigDecimal getTpRate() {
        return tpRate;
    }

    public void setTpRate(BigDecimal tpRate) {
        this.tpRate = tpRate;
    }

    public Integer getTpDays() {
        return tpDays;
    }

    public void setTpDays(Integer tpDays) {
        this.tpDays = tpDays;
    }

    public BigDecimal getApproveTpInterest() {
        return approveTpInterest;
    }

    public void setApproveTpInterest(BigDecimal approveTpInterest) {
        this.approveTpInterest = approveTpInterest;
    }
}
