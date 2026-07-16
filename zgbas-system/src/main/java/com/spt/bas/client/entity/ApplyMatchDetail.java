package com.spt.bas.client.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.spt.tools.jpa.vo.IdEntity;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;

/**
 * 申请单-撮合明细表
 */
@Entity
@Table(name = "t_apply_match_detail")
public class  ApplyMatchDetail extends IdEntity{

	private static final long serialVersionUID = -2389924425862192500L;
	private	String	contractType;		//类型		B-采购，S-销售
	private	Date	arrivalTime;		//到货时间
	private	Date	payBondTime;			//付款时间
	private	Date	payFullTime;			//付全款时间
	private	BigDecimal	payBondAmount = BigDecimal.ZERO;		//付款金额
	private	String	payType;			//付款方式		现金cash、信用证credit、承兑-accept
	private	BigDecimal	payRate = BigDecimal.ZERO;		//付款比例
	private	String	payRemark;			//付款备注  （采购？）

	private	String	receiveType;		//收款方式		现金cash、信用证credit、承兑-accept
	private	Date	receiveBondTime;		//收定金时间
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
	private	Date	receiveFullTime;		//收全款时间
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
	private	Date	deliveryTime;		//交货时间
	private	BigDecimal	receiveBondAmount = BigDecimal.ZERO;	//定金
	private	BigDecimal	receiveRate = BigDecimal.ZERO;	//收款比例
	private	String	receiveRemark;		//收款备注 （销售？）
	private	String	deliveryMode;		//结算方式		款到发货-XKHH、款到发货分批-XKHHFP、货到付款-XHHK
	private	String	deliveryType;		//交货方式		自提-ZT、配送-PS
	private	BigDecimal	warehouseCost = BigDecimal.ZERO;	//仓储费
	private	BigDecimal	transportCost = BigDecimal.ZERO;	//运输费
	private	String	status;		//申请状态		状态 'N-新增，A-审批中，B-驳回，D-完成'

	private	Long	applyMatchId;		//撮合业务ID
	private	Long	enterpriseId;		//企业账套ID
	private	Long	contractId;			//合同ID
	private	String	companyName;		//对方公司名称
	private	String	contactName;		//供货商联系人
	private	String	contactPhone;		//联系电话
	private	String	contactAddr;		//联系地址
	private	String	companyBank;		//银行
	private	String	companyAccount;		//公司账号
	private String taxNumber;			//需货商 税号
	private Long companyId;          	//公司ID
	private String contractNo;			//合同编号
	private String contractAttr;		//合同属性：N-现货，F-期货

	private Long matchUserId;			//业务员ID
	private String matchUserName;		//业务员名称

	private String businessType;		//业务类型
	private String arrivalTimeExt;		//到货日期(补充)
	private String invoiceDate;			//开票日期
	private String extraTerm;			//补充条款
	private BigDecimal totalAmount;		//（合同）总价
	private String qualityStandard;		//质量标准
	private String payKind;				//付款方式
	private String payKindCode;

	private String deliveryAddr;		//交货地点
	private Integer creditDays;			//账期
	private String buySource;  // 采购来源
	private String sellSource; // 销售来源
 	private BigDecimal payRateAmount;
 	@DateTimeFormat(pattern = "yyyy-MM-dd")
 	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
 	private Date deliveryDate;
 	private BigDecimal dealPrice;
 	private BigDecimal dealAmountNotax;
 	private BigDecimal receiveRateAmount;
 	private String serviceType;
 	private BigDecimal minDealPrice;
 	private BigDecimal serviceAmount;
 	private BigDecimal premium;
 	private Long buyTemplateId;					//采购合同模板ID
	private String buyContentTemplateId;		//上传采购合同附件ID
	private Long sellTemplateId;				//销售合同模板ID
	private String sellContentTemplateId;		//上传销售合同附件ID
	private Long serviceTemplateId;				//服务合同模板ID
	private String serviceContentTemplateId;	//上传服务合同模板ID
	private String serviceOurCompanyName;
	// 我方
	private String ourCompanyName;

	/**
	 * 销售 结算方式 0：一票制 1：两票制
	 */
	private String settlementType;

	/**
	 * 不含险销售价
	 */
	private BigDecimal dealPriceNoInsurance;

	private BigDecimal actualContractAmount;

	/**
	 * 抵扣余额
	 */
	private BigDecimal  deductibleAmount;

	/**
	 *合同原始金额
	 */
	private BigDecimal originalContractAmount;

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
	 * 预算运输费
	 */
	private BigDecimal approveTransportAmount ;

	/**
	 * 预算仓储费
	 */
	private BigDecimal  approveWarehouseAmount  ;

	/**
	 * 装卸费
	 */
	private   BigDecimal stevedorage;

	/**
	 * 每吨毛利润
	 */
	private BigDecimal  grossProfit;

	/**
	 * 临时属性-逾期罚息金额
	 */
	private Long breachDays;

	/**
	 * 临时属性-逾期罚息金额
	 */
	private BigDecimal breachAmount;

	/**
	 * 临时属性-已收逾期罚息金额
	 */
	private BigDecimal receiveBreachAmount;

	/**
	 * 临时属性-采购申请ID
	 */
	private Long stockVirtualId;

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

	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
	private Date confirmDate;//确认收货日期

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
	private BigDecimal tpRate;

	/**
	 * 托盘天数
	 */
	private Integer tpDays;

	/**
	 * 审批中预估托盘利息
	 */
	private BigDecimal approveTpInterest;


	public BigDecimal getStevedorage() {
		return defaultNum(stevedorage);
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

	@Transient
	public Long getBreachDays() {
		return breachDays;
	}

	public void setBreachDays(Long breachDays) {
		this.breachDays = breachDays;
	}

	@Transient
	public BigDecimal getBreachAmount() {
		return breachAmount;
	}

	public void setBreachAmount(BigDecimal breachAmount) {
		this.breachAmount = breachAmount;
	}

	@Transient
	public BigDecimal getReceiveBreachAmount() {
		return receiveBreachAmount;
	}

	public void setReceiveBreachAmount(BigDecimal receiveBreachAmount) {
		this.receiveBreachAmount = receiveBreachAmount;
	}

	public BigDecimal getApproveTransportAmount() {
		return approveTransportAmount;
	}

	public void setApproveTransportAmount(BigDecimal approveTransportAmount) {
		this.approveTransportAmount = approveTransportAmount;
	}

	public BigDecimal getApproveWarehouseAmount() {
		return approveWarehouseAmount;
	}

	public void setApproveWarehouseAmount(BigDecimal approveWarehouseAmount) {
		this.approveWarehouseAmount = approveWarehouseAmount;
	}

	public String getApplySource() {
		return applySource;
	}

	public void setApplySource(String applySource) {
		this.applySource = applySource;
	}

	public BigDecimal getActualContractAmount() {
		return actualContractAmount;
	}

	public void setActualContractAmount(BigDecimal actualContractAmount) {
		this.actualContractAmount = actualContractAmount;
	}

	public BigDecimal getOriginalContractAmount() {
		return originalContractAmount;
	}

	public void setOriginalContractAmount(BigDecimal originalContractAmount) {
		this.originalContractAmount = originalContractAmount;
	}

	public BigDecimal getDeductibleAmount() {
		return deductibleAmount;
	}

	public void setDeductibleAmount(BigDecimal deductibleAmount) {
		this.deductibleAmount = deductibleAmount;
	}

	public String getCarrier() {
		return carrier;
	}

	public void setCarrier(String carrier) {
		this.carrier = carrier;
	}

	public String getTaxNumber() {
		return taxNumber;
	}
	public void setTaxNumber(String taxNumber) {
		this.taxNumber = taxNumber;
	}
	public String getContractType() {
		return contractType;
	}
	public void setContractType(String contractType) {
		this.contractType = contractType;
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
		return defaultNum(warehouseCost);
	}
	public void setWarehouseCost(BigDecimal warehouseCost) {
		this.warehouseCost = warehouseCost;
	}
	public BigDecimal getTransportCost() {
		return defaultNum(transportCost);
	}
	public void setTransportCost(BigDecimal transportCost) {
		this.transportCost = transportCost;
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
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public Long getCompanyId() {
		return companyId;
	}
	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
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
	public BigDecimal getPayBondAmount() {
		return payBondAmount;
	}
	public void setPayBondAmount(BigDecimal payBondAmount) {
		this.payBondAmount = payBondAmount;
	}

	public Date getPayFullTime() {
		return payFullTime;
	}
	public void setPayFullTime(Date payFullTime) {
		this.payFullTime = payFullTime;
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
		return receiveBondAmount;
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
	public String getBusinessType() {
		return businessType;
	}
	public void setBusinessType(String businessType) {
		this.businessType = businessType;
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
		return defaultNum(totalAmount);
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

	public static long getSerialVersionUID() {
		return serialVersionUID;
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

	public String getOurCompanyName() {
		return ourCompanyName;
	}

	public void setOurCompanyName(String ourCompanyName) {
		this.ourCompanyName = ourCompanyName;
	}

	private BigDecimal defaultNum(BigDecimal value) {
		return Objects.isNull(value) ? BigDecimal.ZERO : value;
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

	public Date getConfirmDate() {
		return confirmDate;
	}

	public void setConfirmDate(Date confirmDate) {
		this.confirmDate = confirmDate;
	}

	public BigDecimal getDiscountAmount() {
		return defaultNum(discountAmount);
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
		return !Objects.isNull(receiptArrivedFlg) && receiptArrivedFlg;
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
		return defaultNum(approveTpInterest);
	}

	public void setApproveTpInterest(BigDecimal approveTpInterest) {
		this.approveTpInterest = approveTpInterest;
	}

	@Transient
	public Long getStockVirtualId() {
		return stockVirtualId;
	}

	public void setStockVirtualId(Long stockVirtualId) {
		this.stockVirtualId = stockVirtualId;
	}
}
