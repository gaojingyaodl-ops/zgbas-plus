package com.spt.bas.report.client.entity;
/**
 * 自营考核统计实体
 * @author gaojingyao
 */
import java.math.BigDecimal;
import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;

public class RptCtrContractAsseMentReport {
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
	private Date buyDate;				//采购日期
	private String productName;			//商品名称
	private String productCd;			//商品代码
	private String brandNumber;			//牌号
	private Long factoryId;				//厂商ID
	private String factoryName;			//厂商名称
	private BigDecimal sellNumber=BigDecimal.ZERO;		//销售数量
	private Long buyCompanyId;			//采购企业ID
	private String buyCompanyName;		//采购企业名称
	private BigDecimal buyPrice;		//采购单价
	private BigDecimal buyAmount;		//采购额
	private BigDecimal transAmount=BigDecimal.ZERO;		//运费
	private Long buyUserId;				//采购业务员ID
	private String buyUserName;			//采购业务员
	private BigDecimal sellPrice;		//销售单价
	private BigDecimal sellAmount;		//销售额
	private Long sellCompanyId;			//销售企业ID
	private String sellCompanyName;		//销售企业名称
	private Long sellUserId;			//销售业务员ID
	private String sellUserName;		//销售业务员
	private Long sellDeptId;			//部门ID
	private BigDecimal sellMatchProfit=BigDecimal.ZERO;	//销售业务员毛利
	private BigDecimal buyMatchProfit=BigDecimal.ZERO;	//采购业务员毛利
	private String businessNo;			//合同编号
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
	private Date payTime;				//付款日期
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
	private Date payFullTime;			//收款日期
	private BigDecimal buyTotalNumber;  //采购合同数量
	private Long enterpriseId;
	public Date getBuyDate() {
		return buyDate;
	}
	public void setBuyDate(Date buyDate) {
		this.buyDate = buyDate;
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
	public BigDecimal getSellNumber() {
		return sellNumber;
	}
	public void setSellNumber(BigDecimal sellNumber) {
		this.sellNumber = sellNumber;
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
	public BigDecimal getBuyAmount() {
		return buyAmount;
	}
	public void setBuyAmount(BigDecimal buyAmount) {
		this.buyAmount = buyAmount;
	}
	public BigDecimal getTransAmount() {
		return transAmount;
	}
	public void setTransAmount(BigDecimal transAmount) {
		this.transAmount = transAmount;
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
	public BigDecimal getSellPrice() {
		return sellPrice;
	}
	public void setSellPrice(BigDecimal sellPrice) {
		this.sellPrice = sellPrice;
	}
	public BigDecimal getSellAmount() {
		return sellAmount;
	}
	public void setSellAmount(BigDecimal sellAmount) {
		this.sellAmount = sellAmount;
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
	public Long getSellUserId() {
		return sellUserId;
	}
	public void setSellUserId(Long sellUserId) {
		this.sellUserId = sellUserId;
	}
	public String getSellUserName() {
		return sellUserName;
	}
	public void setSellUserName(String sellUserName) {
		this.sellUserName = sellUserName;
	}
	public Long getSellDeptId() {
		return sellDeptId;
	}
	public void setSellDeptId(Long sellDeptId) {
		this.sellDeptId = sellDeptId;
	}
	public BigDecimal getSellMatchProfit() {
		return sellMatchProfit;
	}
	public void setSellMatchProfit(BigDecimal sellMatchProfit) {
		this.sellMatchProfit = sellMatchProfit;
	}
	public BigDecimal getBuyMatchProfit() {
		return buyMatchProfit;
	}
	public void setBuyMatchProfit(BigDecimal buyMatchProfit) {
		this.buyMatchProfit = buyMatchProfit;
	}
	
	public Date getPayFullTime() {
		return payFullTime;
	}
	public void setPayFullTime(Date payFullTime) {
		this.payFullTime = payFullTime;
	}
	public BigDecimal getBuyPrice() {
		return buyPrice;
	}
	public void setBuyPrice(BigDecimal buyPrice) {
		this.buyPrice = buyPrice;
	}
	public String getBusinessNo() {
		return businessNo;
	}
	public void setBusinessNo(String businessNo) {
		this.businessNo = businessNo;
	}
	public Date getPayTime() {
		return payTime;
	}
	public void setPayTime(Date payTime) {
		this.payTime = payTime;
	}
	public BigDecimal getBuyTotalNumber() {
		return buyTotalNumber;
	}
	public void setBuyTotalNumber(BigDecimal buyTotalNumber) {
		this.buyTotalNumber = buyTotalNumber;
	}
	public Long getEnterpriseId() {
		return enterpriseId;
	}
	public void setEnterpriseId(Long enterpriseId) {
		this.enterpriseId = enterpriseId;
	}
	
	
}
