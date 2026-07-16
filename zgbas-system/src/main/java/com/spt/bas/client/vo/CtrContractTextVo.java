package com.spt.bas.client.vo;

import com.spt.bas.client.entity.CtrContract;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class CtrContractTextVo extends CtrContract{

	/**
	 *
	 */
	private static final long serialVersionUID = 5815316857357570161L;

	private List<CtrProductVo> productList = new ArrayList<CtrProductVo>();
	private String serviceContractNo;
	private String ourCompanyName;
	private String contractTimeStr;
	private String deliveryDateStr;
	private String deliveryTimeStr;
	private String invoiceDateStr;
	private String payTimeStr;
	private	BigDecimal	payAmount;	//付款金额
	private String tottalAmountStr;

	private String receiveBank;
	private String receiveAccount;

	private String deliveryTypeName;
	private String matchUserPhone;//业务员手机号
	private String status;
	private String email;

	private String ourCompanyAddres;   //单位地址
	private String ourCompanyFax;	  //传真号码
	private String ourCompanyEmail;	  //单位电子邮箱
	private String ourCompanyPerson;  //单位法人
	private String ourCompanyContact;  //联系人
	private String ourCompanyPhone;  //联系电话
	private String deliveryTypeStr;	  //交货方式
	private String qualityStandardStr;//质量标准

	private String companyPerson;	  //公司法人
	private String companyFax;		  //公司传真

	/**
	 * 总价
	 */
	private BigDecimal totalAmount;

	/**
	 * 总价(中文)
	 */
	private String totalAmountStr;

	/**
	 * 运费承担
	 */
	private String freightBearing;

	/**
	 * 交货地点
	 */
	private String deliAddr;

	private  String wrapSpecs;

	private  String payMode;

	private   String  signAddress;

	private   String  additionalAgreement;


	private String   payRemaindTime;

	private  String payBondTimeStr;

	/**
	 * 数量合计
	 */
	private BigDecimal sumDealNumber;
	/**
	 * 不含税单价
	 */
	private BigDecimal sumTaxPriceNoTax;
	/**
	 * 不含税总价合计
	 */
	private BigDecimal sumTotalPriceNoTax;
	/**
	 * 函数单价
	 */
	private BigDecimal sumDealPrice;
	/**
	 * 含税合计
	 */
	private BigDecimal sumTotalPrice;
	
	private String ourBankName;
	private String ourBankAccount;
	
	private String bankName;
	private String bankAccount;

	/**
	 * 定金比率
	 */
	private String bondRateStr;

	/**
	 * 上游定金
	 */
	private BigDecimal payRateAmount;

	/**
	 * 货到票到标识字符串
	 */
	private String receiptArrivedStr;

	public String getPayBondTimeStr() {
		return payBondTimeStr;
	}

	public void setPayBondTimeStr(String payBondTimeStr) {
		this.payBondTimeStr = payBondTimeStr;
	}

	public String getPayRemaindTime() {
		return payRemaindTime;
	}

	public void setPayRemaindTime(String payRemaindTime) {
		this.payRemaindTime = payRemaindTime;
	}

	public String getSignAddress() {
		return signAddress;
	}

	public void setSignAddress(String signAddress) {
		this.signAddress = signAddress;
	}

	public String getAdditionalAgreement() {
		return additionalAgreement;
	}

	public void setAdditionalAgreement(String additionalAgreement) {
		this.additionalAgreement = additionalAgreement;
	}

	public String getWrapSpecs() {
		return wrapSpecs;
	}

	public void setWrapSpecs(String wrapSpecs) {
		this.wrapSpecs = wrapSpecs;
	}

	@Override
	public String getPayMode() {
		return payMode;
	}

	@Override
	public void setPayMode(String payMode) {
		this.payMode = payMode;
	}



	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getMatchUserPhone() {
		return matchUserPhone;
	}
	public void setMatchUserPhone(String matchUserPhone) {
		this.matchUserPhone = matchUserPhone;
	}
	public BigDecimal getPayAmount() {
		return payAmount;
	}
	public void setPayAmount(BigDecimal payAmount) {
		this.payAmount = payAmount;
	}
	public List<CtrProductVo> getProductList() {
		return productList;
	}
	public void setProductList(List<CtrProductVo> productList) {
		this.productList = productList;
	}
	public String getContractTimeStr() {
		return contractTimeStr;
	}
	public void setContractTimeStr(String contractTimeStr) {
		this.contractTimeStr = contractTimeStr;
	}
	public String getDeliveryDateStr() {
		return deliveryDateStr;
	}
	public void setDeliveryDateStr(String deliveryDateStr) {
		this.deliveryDateStr = deliveryDateStr;
	}
	public String getPayTimeStr() {
		return payTimeStr;
	}
	public void setPayTimeStr(String payTimeStr) {
		this.payTimeStr = payTimeStr;
	}
	public String getTottalAmountStr() {
		return tottalAmountStr;
	}
	public void setTottalAmountStr(String tottalAmountStr) {
		this.tottalAmountStr = tottalAmountStr;
	}
	public String getReceiveBank() {
		return receiveBank;
	}
	public void setReceiveBank(String receiveBank) {
		this.receiveBank = receiveBank;
	}
	public String getReceiveAccount() {
		return receiveAccount;
	}
	public void setReceiveAccount(String receiveAccount) {
		this.receiveAccount = receiveAccount;
	}
	public String getDeliveryTypeName() {
		return deliveryTypeName;
	}
	public void setDeliveryTypeName(String deliveryTypeName) {
		this.deliveryTypeName = deliveryTypeName;
	}
	public String getOurCompanyName() {
		return ourCompanyName;
	}
	public void setOurCompanyName(String ourCompanyName) {
		this.ourCompanyName = ourCompanyName;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getOurCompanyAddres() {
		return ourCompanyAddres;
	}
	public void setOurCompanyAddres(String ourCompanyAddres) {
		this.ourCompanyAddres = ourCompanyAddres;
	}
	public String getOurCompanyFax() {
		return ourCompanyFax;
	}
	public void setOurCompanyFax(String ourCompanyFax) {
		this.ourCompanyFax = ourCompanyFax;
	}
	public String getOurCompanyEmail() {
		return ourCompanyEmail;
	}
	public void setOurCompanyEmail(String ourCompanyEmail) {
		this.ourCompanyEmail = ourCompanyEmail;
	}
	public String getDeliveryTypeStr() {
		return deliveryTypeStr;
	}
	public void setDeliveryTypeStr(String deliveryTypeStr) {
		this.deliveryTypeStr = deliveryTypeStr;
	}
	public String getQualityStandardStr() {
		return qualityStandardStr;
	}
	public void setQualityStandardStr(String qualityStandardStr) {
		this.qualityStandardStr = qualityStandardStr;
	}
	public String getOurCompanyPerson() {
		return ourCompanyPerson;
	}
	public void setOurCompanyPerson(String ourCompanyPerson) {
		this.ourCompanyPerson = ourCompanyPerson;
	}

	public String getOurCompanyContact() {
		return ourCompanyContact;
	}

	public void setOurCompanyContact(String ourCompanyContact) {
		this.ourCompanyContact = ourCompanyContact;
	}

	public String getOurCompanyPhone() {
		return ourCompanyPhone;
	}

	public void setOurCompanyPhone(String ourCompanyPhone) {
		this.ourCompanyPhone = ourCompanyPhone;
	}

	public String getCompanyPerson() {
		return companyPerson;
	}
	public void setCompanyPerson(String companyPerson) {
		this.companyPerson = companyPerson;
	}
	public String getInvoiceDateStr() {
		return invoiceDateStr;
	}
	public void setInvoiceDateStr(String invoiceDateStr) {
		this.invoiceDateStr = invoiceDateStr;
	}
	public String getCompanyFax() {
		return companyFax;
	}
	public void setCompanyFax(String companyFax) {
		this.companyFax = companyFax;
	}
	public String getDeliveryTimeStr() {
		return deliveryTimeStr;
	}
	public void setDeliveryTimeStr(String deliveryTimeStr) {
		this.deliveryTimeStr = deliveryTimeStr;
	}

	public String getServiceContractNo() {
		return serviceContractNo;
	}

	public void setServiceContractNo(String serviceContractNo) {
		this.serviceContractNo = serviceContractNo;
	}

	@Override
	public BigDecimal getTotalAmount() {
		return totalAmount;
	}

	@Override
	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
	}

	public String getTotalAmountStr() {
		return totalAmountStr;
	}

	public void setTotalAmountStr(String totalAmountStr) {
		this.totalAmountStr = totalAmountStr;
	}

	public String getFreightBearing() {
		return freightBearing;
	}

	public void setFreightBearing(String freightBearing) {
		this.freightBearing = freightBearing;
	}

	public String getDeliAddr() {
		return deliAddr;
	}

	public void setDeliAddr(String deliAddr) {
		this.deliAddr = deliAddr;
	}

	public BigDecimal getSumDealNumber() {
		return sumDealNumber;
	}

	public void setSumDealNumber(BigDecimal sumDealNumber) {
		this.sumDealNumber = sumDealNumber;
	}

	public BigDecimal getSumTaxPriceNoTax() {
		return sumTaxPriceNoTax;
	}

	public void setSumTaxPriceNoTax(BigDecimal sumTaxPriceNoTax) {
		this.sumTaxPriceNoTax = sumTaxPriceNoTax;
	}

	public BigDecimal getSumTotalPriceNoTax() {
		return sumTotalPriceNoTax;
	}

	public void setSumTotalPriceNoTax(BigDecimal sumTotalPriceNoTax) {
		this.sumTotalPriceNoTax = sumTotalPriceNoTax;
	}

	public BigDecimal getSumDealPrice() {
		return sumDealPrice;
	}

	public void setSumDealPrice(BigDecimal sumDealPrice) {
		this.sumDealPrice = sumDealPrice;
	}

	public BigDecimal getSumTotalPrice() {
		return sumTotalPrice;
	}

	public void setSumTotalPrice(BigDecimal sumTotalPrice) {
		this.sumTotalPrice = sumTotalPrice;
	}

	public String getOurBankName() {
		return ourBankName;
	}

	public void setOurBankName(String ourBankName) {
		this.ourBankName = ourBankName;
	}

	public String getOurBankAccount() {
		return ourBankAccount;
	}

	public void setOurBankAccount(String ourBankAccount) {
		this.ourBankAccount = ourBankAccount;
	}

	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public String getBankAccount() {
		return bankAccount;
	}

	public void setBankAccount(String bankAccount) {
		this.bankAccount = bankAccount;
	}

	public String getBondRateStr() {
		return bondRateStr;
	}

	public void setBondRateStr(String bondRateStr) {
		this.bondRateStr = bondRateStr;
	}

	public BigDecimal getPayRateAmount() {
		return payRateAmount;
	}

	public void setPayRateAmount(BigDecimal payRateAmount) {
		this.payRateAmount = payRateAmount;
	}

	public String getReceiptArrivedStr() {
		return receiptArrivedStr;
	}

	public void setReceiptArrivedStr(String receiptArrivedStr) {
		this.receiptArrivedStr = receiptArrivedStr;
	}
}
