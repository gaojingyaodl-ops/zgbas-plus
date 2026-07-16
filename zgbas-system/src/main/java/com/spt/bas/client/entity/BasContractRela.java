package com.spt.bas.client.entity;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.spt.pm.inter.IPmEntity;
import com.spt.tools.jpa.vo.IdEntity;

/**
 * 合同关联
 */
@Entity
@Table(name = "t_bas_contract_rela")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class BasContractRela extends IdEntity implements IPmEntity {

	private static final long serialVersionUID = -2685465518394319986L;
	private String buyContractId;// 买方合同Id
//	private String buyCompanyName;// 买方企业名称
//	private Long buyCompanyId;// 买方企业id
	private String sellContractId;// 卖方合同Id
//	private String sellCompanyName;// 卖方企业名称
//	private Long sellCompanyId;// 卖方企业id
	private BigDecimal grossProfit; // 毛利润
	private String ourCompanyName; // 我方企业名称

	private String productName; // 商品名称
	private String productCode; // 商品代码
	private BigDecimal buyNumber; // 采购数量
	private BigDecimal sellNumber; // 销售数量
	private String numberUnit; // 数量单位
//	private BigDecimal buyPrice; // 买方单价
	private BigDecimal buyAmount; // 买方总价
//	private BigDecimal sellPrice; // 买方单价
	private BigDecimal sellAmount; // 买方总价
	private String status; // 审批状态
	private String fileId; // 附件id,多个附件Id
	private Long approveId; // 审批id
	private String remark;
	@JsonSerialize(using = ToStringSerializer.class)
	private Boolean closeFlg;//是否闭口业务
	
	private Boolean exposureFlg=false;//是否是敞口业务
	
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
	private Date contractTime; // 合同时间
	
	private String brandNumber;

	private Long enterpriseId; // 企业账套Id
	
	public BigDecimal getGrossProfit() {
		return grossProfit;
	}
	public void setGrossProfit(BigDecimal grossProfit) {
		this.grossProfit = grossProfit;
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
	public String getProductCode() {
		return productCode;
	}
	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}
	public String getNumberUnit() {
		return numberUnit;
	}
	public void setNumberUnit(String numberUnit) {
		this.numberUnit = numberUnit;
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
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getFileId() {
		return fileId;
	}
	public void setFileId(String fileId) {
		this.fileId = fileId;
	}
	public Long getApproveId() {
		return approveId;
	}
	public void setApproveId(Long approveId) {
		this.approveId = approveId;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public String getBuyContractId() {
		return buyContractId;
	}
	public void setBuyContractId(String buyContractId) {
		this.buyContractId = buyContractId;
	}
	public String getSellContractId() {
		return sellContractId;
	}
	public void setSellContractId(String sellContractId) {
		this.sellContractId = sellContractId;
	}
	public Date getContractTime() {
		return contractTime;
	}
	public void setContractTime(Date contractTime) {
		this.contractTime = contractTime;
	}
	public Boolean getCloseFlg() {
		return closeFlg;
	}
	public void setCloseFlg(Boolean closeFlg) {
		this.closeFlg = closeFlg;
	}
	public BigDecimal getBuyNumber() {
		return buyNumber;
	}
	public void setBuyNumber(BigDecimal buyNumber) {
		this.buyNumber = buyNumber;
	}
	public BigDecimal getSellNumber() {
		return sellNumber;
	}
	public void setSellNumber(BigDecimal sellNumber) {
		this.sellNumber = sellNumber;
	}
	public Boolean getExposureFlg() {
		return exposureFlg;
	}
	public void setExposureFlg(Boolean exposureFlg) {
		this.exposureFlg = exposureFlg;
	}
	public String getBrandNumber() {
		return brandNumber;
	}
	public void setBrandNumber(String brandNumber) {
		this.brandNumber = brandNumber;
	}
	public Long getEnterpriseId() {
		return enterpriseId;
	}
	public void setEnterpriseId(Long enterpriseId) {
		this.enterpriseId = enterpriseId;
	}

}
