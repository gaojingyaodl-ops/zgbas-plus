package com.spt.bas.client.vo;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.CtrProduct;

public class ContractExcelVo {

	private String buyCompanyName;//买方
	private Long buyCompanyId;
	private Long buyUserId;
	private String buyUserName;
	private String buyCompanyType;
	private String sellCompanyName;//我方抬头
	private Long sellCompanyId;
	private Long sellUserId;
	private String sellUserName;
	private String sellMobile;
	private String contractNo;
	private String fileId;
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
	private Date tradeDate;//成交日期
	private String productName;//品名
	private String productCd;
	private String brandNumber;//牌号
	private String factoryName;//厂商
	private BigDecimal dealNumber;//数量
	private BigDecimal dealPrice;//销售单价
	private BigDecimal totalPrice;//总价
	private Integer paymentDays;//账期天数
	private BigDecimal payedAmount;//已还款金额
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
	private Date deliveryDate;//交货时间
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
	private Date payDate;//付款时间
	private String deliveryPlace;//交货地
	private BigDecimal lateFee;//逾期收款金额
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
	private Date payedDate; 
	private String sellerContractor;//业务经理
	private String sellerMobile;
	//private Integer delayDays;//逾期天数
	private String remark;
	private String deliveryType = BasConstants.DICT_TYPE_BUYDELIVERY_Z;		//交货方式
	private String deliveryTypeName;
	//private String delayStr;//是否逾期
	private String departmentName;
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
	private Date allDeliveryDate;//全部出货日期
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
	private Date billDate;//开票日期
	private String status;//合同状态
	
	private Boolean creditFlg = false;  //赊销标识
	private String payMode;				//付款方式
	private String packageSpec;			//包装规格
	private Boolean basFlg = false;
	private String businessType;		//业务类型
	
	private List<CtrProduct> ctrProductList;

	public String getContractNo() {
		return contractNo;
	}
	public void setContractNo(String contractNo) {
		this.contractNo = contractNo;
	}
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	public String getFactoryName() {
		return factoryName;
	}
	public void setFactoryName(String factoryName) {
		this.factoryName = factoryName;
	}
	public Integer getPaymentDays() {
		return paymentDays;
	}
	public void setPaymentDays(Integer paymentDays) {
		this.paymentDays = paymentDays;
	}
	public Date getDeliveryDate() {
		return deliveryDate;
	}
	public void setDeliveryDate(Date deliveryDate) {
		this.deliveryDate = deliveryDate;
	}
	public Date getPayDate() {
		return payDate;
	}
	public void setPayDate(Date payDate) {
		this.payDate = payDate;
	}
	public String getDeliveryPlace() {
		return deliveryPlace;
	}
	public void setDeliveryPlace(String deliveryPlace) {
		this.deliveryPlace = deliveryPlace;
	}
	public Date getPayedDate() {
		return payedDate;
	}
	public void setPayedDate(Date payedDate) {
		this.payedDate = payedDate;
	}
	public String getSellerContractor() {
		return sellerContractor;
	}
	public void setSellerContractor(String sellerContractor) {
		this.sellerContractor = sellerContractor;
	}
	public String getSellerMobile() {
		return sellerMobile;
	}
	public void setSellerMobile(String sellerMobile) {
		this.sellerMobile = sellerMobile;
	}
	public BigDecimal getDealPrice() {
		return dealPrice;
	}
	public void setDealPrice(BigDecimal dealPrice) {
		this.dealPrice = dealPrice;
	}
	public BigDecimal getTotalPrice() {
		return totalPrice;
	}
	public void setTotalPrice(BigDecimal totalPrice) {
		this.totalPrice = totalPrice;
	}
	public BigDecimal getPayedAmount() {
		return payedAmount;
	}
	public void setPayedAmount(BigDecimal payedAmount) {
		this.payedAmount = payedAmount;
	}
	public BigDecimal getLateFee() {
		return lateFee;
	}
	public void setLateFee(BigDecimal lateFee) {
		this.lateFee = lateFee;
	}
	/*public Integer getDelayDays() {
		return delayDays;
	}
	public void setDelayDays(Integer delayDays) {
		this.delayDays = delayDays;
	}*/
	public String getBuyCompanyName() {
		return buyCompanyName;
	}
	public void setBuyCompanyName(String buyCompanyName) {
		this.buyCompanyName = buyCompanyName;
	}
	public Long getBuyCompanyId() {
		return buyCompanyId;
	}
	public void setBuyCompanyId(Long buyCompanyId) {
		this.buyCompanyId = buyCompanyId;
	}
	public String getSellCompanyName() {
		return sellCompanyName;
	}
	public void setSellCompanyName(String sellCompanyName) {
		this.sellCompanyName = sellCompanyName;
	}
	public Long getSellCompanyId() {
		return sellCompanyId;
	}
	public void setSellCompanyId(Long sellCompanyId) {
		this.sellCompanyId = sellCompanyId;
	}
	public String getBrandNumber() {
		return brandNumber;
	}
	public void setBrandNumber(String brandNumber) {
		this.brandNumber = brandNumber;
	}
	public Long getBuyUserId() {
		return buyUserId;
	}
	public void setBuyUserId(Long buyUserId) {
		this.buyUserId = buyUserId;
	}
	public Long getSellUserId() {
		return sellUserId;
	}
	public void setSellUserId(Long sellUserId) {
		this.sellUserId = sellUserId;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public String getProductCd() {
		return productCd;
	}
	public void setProductCd(String productCd) {
		this.productCd = productCd;
	}
	public BigDecimal getDealNumber() {
		return dealNumber;
	}
	public void setDealNumber(BigDecimal dealNumber) {
		this.dealNumber = dealNumber;
	}
	public String getBuyUserName() {
		return buyUserName;
	}
	public void setBuyUserName(String buyUserName) {
		this.buyUserName = buyUserName;
	}
	public String getBuyCompanyType() {
		return buyCompanyType;
	}
	public void setBuyCompanyType(String buyCompanyType) {
		this.buyCompanyType = buyCompanyType;
	}
	public String getDeliveryType() {
		return deliveryType;
	}
	public void setDeliveryType(String deliveryType) {
		this.deliveryType = deliveryType;
	}
	public String getDeliveryTypeName() {
		return deliveryTypeName;
	}
	public void setDeliveryTypeName(String deliveryTypeName) {
		this.deliveryTypeName = deliveryTypeName;
	}
	public String getSellUserName() {
		return sellUserName;
	}
	public void setSellUserName(String sellUserName) {
		this.sellUserName = sellUserName;
	}
	public String getSellMobile() {
		return sellMobile;
	}
	public void setSellMobile(String sellMobile) {
		this.sellMobile = sellMobile;
	}
	/*public String getDelayStr() {
		return delayStr;
	}
	public void setDelayStr(String delayStr) {
		this.delayStr = delayStr;
	}*/
	public String getDepartmentName() {
		return departmentName;
	}
	public void setDepartmentName(String departmentName) {
		this.departmentName = departmentName;
	}
	public Date getAllDeliveryDate() {
		return allDeliveryDate;
	}
	public void setAllDeliveryDate(Date allDeliveryDate) {
		this.allDeliveryDate = allDeliveryDate;
	}
	public Date getBillDate() {
		return billDate;
	}
	public void setBillDate(Date billDate) {
		this.billDate = billDate;
	}
	public Date getTradeDate() {
		return tradeDate;
	}
	public void setTradeDate(Date tradeDate) {
		this.tradeDate = tradeDate;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public List<CtrProduct> getCtrProductList() {
		return ctrProductList;
	}
	public void setCtrProductList(List<CtrProduct> ctrProductList) {
		this.ctrProductList = ctrProductList;
	}
	public String getFileId() {
		return fileId;
	}
	public void setFileId(String fileId) {
		this.fileId = fileId;
	}
	public Boolean getCreditFlg() {
		return creditFlg;
	}
	public void setCreditFlg(Boolean creditFlg) {
		this.creditFlg = creditFlg;
	}
	public String getPayMode() {
		return payMode;
	}
	public void setPayMode(String payMode) {
		this.payMode = payMode;
	}
	public String getPackageSpec() {
		return packageSpec;
	}
	public void setPackageSpec(String packageSpec) {
		this.packageSpec = packageSpec;
	}
	public Boolean getBasFlg() {
		return basFlg;
	}
	public void setBasFlg(Boolean basFlg) {
		this.basFlg = basFlg;
	}
	public String getBusinessType() {
		return businessType;
	}
	public void setBusinessType(String businessType) {
		this.businessType = businessType;
	}
	
}
