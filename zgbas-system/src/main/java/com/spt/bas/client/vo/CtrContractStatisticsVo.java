package com.spt.bas.client.vo;

import java.math.BigDecimal;
import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;
/**
 * 进销统计表
 * @author zhouzihang
 *
 */
public class CtrContractStatisticsVo {
	private Long contractRelaId;// 采购销售关系ID
	private String contractNo;// 采购/销售合同编号
	private Date buyDate;// 采购日期
	private String contractAttr;// 合同属性：N-现货，F-期货
	private String productName;// 商品名称
	private String brandNumber;// 牌号
	private String factoryName;// 厂商
	private String companyName;// 对方企业
	private String warehouseAddr;// 仓库地址
	private BigDecimal dealNumber;// 数量
	private BigDecimal dealPrice;// 单价
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
	private Date payTime;// 付款日期
	private BigDecimal salesNumber;// 已销数量
	private BigDecimal remainNumber;// 剩余数量
	private Long inWarehouseHours;// 在库时长(当前日期-采购日期)
	private BigDecimal overplusPrice;// 剩余货值
	private Long matchUserId;// 业务员ID
	private String matchUserName;// 业务员名称
	private Long leaderId;// 小组负责人ID
	private Long deptId;// 部门ID
	private String deptName;// 部门名称
	private BigDecimal totalPrice;// 销售金额
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
	private Date deliveryDateFrom;// 交货日期开始
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
	private Date deliveryDateTo;// 交货日期结束(到货时间)
	private String deliveryMode;// 交货方式
	private String deliveryType;// 提货方式
	private BigDecimal transportAmount;// 运输费
	private Boolean billFlg;// 发票状态
	public Long getContractRelaId() {
		return contractRelaId;
	}
	public void setContractRelaId(Long contractRelaId) {
		this.contractRelaId = contractRelaId;
	}
	public String getContractNo() {
		return contractNo;
	}
	public void setContractNo(String contractNo) {
		this.contractNo = contractNo;
	}
	public Date getBuyDate() {
		return buyDate;
	}
	public void setBuyDate(Date buyDate) {
		this.buyDate = buyDate;
	}
	public String getContractAttr() {
		return contractAttr;
	}
	public void setContractAttr(String contractAttr) {
		this.contractAttr = contractAttr;
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
	public String getCompanyName() {
		return companyName;
	}
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}
	public String getWarehouseAddr() {
		return warehouseAddr;
	}
	public void setWarehouseAddr(String warehouseAddr) {
		this.warehouseAddr = warehouseAddr;
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
	public Date getPayTime() {
		return payTime;
	}
	public void setPayTime(Date payTime) {
		this.payTime = payTime;
	}
	public BigDecimal getSalesNumber() {
		return salesNumber;
	}
	public void setSalesNumber(BigDecimal salesNumber) {
		this.salesNumber = salesNumber;
	}
	public BigDecimal getRemainNumber() {
		return remainNumber;
	}
	public void setRemainNumber(BigDecimal remainNumber) {
		this.remainNumber = remainNumber;
	}
	public Long getInWarehouseHours() {
		return inWarehouseHours;
	}
	public void setInWarehouseHours(Long inWarehouseHours) {
		this.inWarehouseHours = inWarehouseHours;
	}
	public BigDecimal getOverplusPrice() {
		return overplusPrice;
	}
	public void setOverplusPrice(BigDecimal overplusPrice) {
		this.overplusPrice = overplusPrice;
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
	public Long getLeaderId() {
		return leaderId;
	}
	public void setLeaderId(Long leaderId) {
		this.leaderId = leaderId;
	}
	public Long getDeptId() {
		return deptId;
	}
	public void setDeptId(Long deptId) {
		this.deptId = deptId;
	}
	public String getDeptName() {
		return deptName;
	}
	public void setDeptName(String deptName) {
		this.deptName = deptName;
	}
	public BigDecimal getTotalPrice() {
		return totalPrice;
	}
	public void setTotalPrice(BigDecimal totalPrice) {
		this.totalPrice = totalPrice;
	}
	public Date getDeliveryDateFrom() {
		return deliveryDateFrom;
	}
	public void setDeliveryDateFrom(Date deliveryDateFrom) {
		this.deliveryDateFrom = deliveryDateFrom;
	}
	public Date getDeliveryDateTo() {
		return deliveryDateTo;
	}
	public void setDeliveryDateTo(Date deliveryDateTo) {
		this.deliveryDateTo = deliveryDateTo;
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
	public BigDecimal getTransportAmount() {
		return transportAmount;
	}
	public void setTransportAmount(BigDecimal transportAmount) {
		this.transportAmount = transportAmount;
	}
	public Boolean getBillFlg() {
		return billFlg;
	}
	public void setBillFlg(Boolean billFlg) {
		this.billFlg = billFlg;
	}
	
}
