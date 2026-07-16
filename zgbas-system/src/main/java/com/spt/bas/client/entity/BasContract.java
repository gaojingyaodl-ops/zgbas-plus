package com.spt.bas.client.entity;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.spt.pm.inter.IPmEntity;
import com.spt.tools.jpa.vo.IdEntity;

@Entity
@Table(name = "t_bas_contract")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class BasContract extends IdEntity implements IPmEntity {
	/**
	 * 合同信息
	 */
	private static final long serialVersionUID = -2685465518394319986L;
	private String contractType; // 合同类型; 'B-采购，S-销售',
	private String businessNo; // 业务编号
	private String contractNo; // 合同编号
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
	private Date contractTime; // 合同时间
	private Long oppCompanyId; // 对方企业id
	private String oppCompanyName; // 对方企业名称
	private String ourCompanyName; // 我方企业名称
	private String productName; // 商品名称
	private String productCode; // 商品代码
	private BigDecimal dealPrice; // 单价
	private BigDecimal dealNumber; // 数量
	private String numberUnit; // 数量单位
	private BigDecimal dealAmount; // 合同总价
	private BigDecimal taxAmount; // 进项税
	private BigDecimal dealAmountNotax; // 不含税价
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
	private Date deliveryDateFrom; // 交货日期开始
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
	private Date deliveryDateTo; // 交货日期结束
	private String warehouse; // 仓库
	private String status; // 审批状态
	private String fileId; // 附件id,多个附件Id
	private Long linkContractId; // 关联合同id
	private Long approveId; // 审批id
	private String remark;

	// 采购：N-新增，S-已签约，F1-已付款，G1-已收货，V1-已收票
	// 销售：N-新增，S-已签约，F2-已收款，G2-已发货，V2-已开票
	private String contractStatus = "N";// 合同状态 
	
	@JsonSerialize(using = ToStringSerializer.class)
	private Boolean fondFlg = false;//已收款/已付款
	@JsonSerialize(using = ToStringSerializer.class)
	private Boolean payFlg = false;//已发货/已收货
	@JsonSerialize(using = ToStringSerializer.class)
	private Boolean billFlg = false;//已收票/已开票
	@JsonSerialize(using = ToStringSerializer.class)
	private Boolean buysellFlg = false;//已销售/已采购

	private BigDecimal bondRate;// 定金比率
	private BigDecimal payBond;// 已付定金
	private BigDecimal payRemain;// 已付尾款
	private String deliveryMode;// 交货方式
	
	private Long contractRelaId;//合同关联表Id
	@JsonSerialize(using = ToStringSerializer.class)
	private Boolean closeFlg;//是否闭口业务
	
	private String createUserName;
	private Long createUserId;
	
	private String matchUserName;
	private Long matchUserId;
	
	private String brandNumber;
	private BigDecimal remainNumber; 
	private Long contractTextId;
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
	private Date payTime;

	private Long enterpriseId; // 企业账套Id
	
	public String getContractType() {
		return contractType;
	}

	public void setContractType(String contractType) {
		this.contractType = contractType;
	}

	public String getContractNo() {
		return contractNo;
	}

	public void setContractNo(String contractNo) {
		this.contractNo = contractNo;
	}

	public Date getContractTime() {
		return contractTime;
	}

	public void setContractTime(Date contractTime) {
		this.contractTime = contractTime;
	}

	public Long getOppCompanyId() {
		return oppCompanyId;
	}

	public void setOppCompanyId(Long oppCompanyId) {
		this.oppCompanyId = oppCompanyId;
	}

	public String getOppCompanyName() {
		return oppCompanyName;
	}

	public void setOppCompanyName(String oppCompanyName) {
		this.oppCompanyName = oppCompanyName;
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

	public BigDecimal getDealPrice() {
		return dealPrice;
	}

	public void setDealPrice(BigDecimal dealPrice) {
		this.dealPrice = dealPrice;
	}

	public BigDecimal getDealNumber() {
		return dealNumber;
	}

	public void setDealNumber(BigDecimal dealNumber) {
		this.dealNumber = dealNumber;
	}

	public String getNumberUnit() {
		return numberUnit;
	}

	public void setNumberUnit(String numberUnit) {
		this.numberUnit = numberUnit;
	}

	public BigDecimal getDealAmount() {
		return dealAmount;
	}

	public void setDealAmount(BigDecimal dealAmount) {
		this.dealAmount = dealAmount;
	}

	public BigDecimal getTaxAmount() {
		return taxAmount;
	}

	public void setTaxAmount(BigDecimal taxAmount) {
		this.taxAmount = taxAmount;
	}

	public BigDecimal getDealAmountNotax() {
		return dealAmountNotax;
	}

	public void setDealAmountNotax(BigDecimal dealAmountNotax) {
		this.dealAmountNotax = dealAmountNotax;
	}

	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
	public Date getDeliveryDateFrom() {
		return deliveryDateFrom;
	}

	public void setDeliveryDateFrom(Date deliveryDateFrom) {
		this.deliveryDateFrom = deliveryDateFrom;
	}

	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
	public Date getDeliveryDateTo() {
		return deliveryDateTo;
	}

	public void setDeliveryDateTo(Date deliveryDateTo) {
		this.deliveryDateTo = deliveryDateTo;
	}

	public String getWarehouse() {
		return warehouse;
	}

	public void setWarehouse(String warehouse) {
		this.warehouse = warehouse;
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

	public Long getLinkContractId() {
		return linkContractId;
	}

	public void setLinkContractId(Long linkContractId) {
		this.linkContractId = linkContractId;
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

	public String getBusinessNo() {
		return businessNo;
	}

	public void setBusinessNo(String businessNo) {
		this.businessNo = businessNo;
	}

	public BigDecimal getBondRate() {
		return bondRate;
	}

	public void setBondRate(BigDecimal bondRate) {
		this.bondRate = bondRate;
	}

	public BigDecimal getPayRemain() {
		return payRemain;
	}

	public void setPayRemain(BigDecimal payRemain) {
		this.payRemain = payRemain;
	}

	public String getDeliveryMode() {
		return deliveryMode;
	}

	public void setDeliveryMode(String deliveryMode) {
		this.deliveryMode = deliveryMode;
	}

	public String getContractStatus() {
		return contractStatus;
	}

	public void setContractStatus(String contractStatus) {
		this.contractStatus = contractStatus;
	}

	public BigDecimal getPayBond() {
		return payBond;
	}

	public void setPayBond(BigDecimal payBond) {
		this.payBond = payBond;
	}

	public Long getContractRelaId() {
		return contractRelaId;
	}

	public void setContractRelaId(Long contractRelaId) {
		this.contractRelaId = contractRelaId;
	}

	public Boolean getCloseFlg() {
		return closeFlg;
	}

	public void setCloseFlg(Boolean closeFlg) {
		this.closeFlg = closeFlg;
	}

	public Boolean getFondFlg() {
		return fondFlg;
	}

	public void setFondFlg(Boolean fondFlg) {
		this.fondFlg = fondFlg;
	}

	public Boolean getPayFlg() {
		return payFlg;
	}

	public void setPayFlg(Boolean payFlg) {
		this.payFlg = payFlg;
	}

	public Boolean getBillFlg() {
		return billFlg;
	}

	public void setBillFlg(Boolean billFlg) {
		this.billFlg = billFlg;
	}
	@Transient
	public String getCreateUserName() {
		return createUserName;
	}

	public void setCreateUserName(String createUserName) {
		this.createUserName = createUserName;
	}
	
	@Transient
	public Long getCreateUserId() {
		return createUserId;
	}

	public void setCreateUserId(Long createUserId) {
		this.createUserId = createUserId;
	}

	public String getMatchUserName() {
		return matchUserName;
	}

	public void setMatchUserName(String matchUserName) {
		this.matchUserName = matchUserName;
	}

	public Long getMatchUserId() {
		return matchUserId;
	}

	public void setMatchUserId(Long matchUserId) {
		this.matchUserId = matchUserId;
	}

	public Boolean getBuysellFlg() {
		return buysellFlg;
	}

	public void setBuysellFlg(Boolean buysellFlg) {
		this.buysellFlg = buysellFlg;
	}

	public String getBrandNumber() {
		return brandNumber;
	}

	public void setBrandNumber(String brandNumber) {
		this.brandNumber = brandNumber;
	}

	public BigDecimal getRemainNumber() {
		return remainNumber;
	}

	public void setRemainNumber(BigDecimal remainNumber) {
		this.remainNumber = remainNumber;
	}

	public Long getContractTextId() {
		return contractTextId;
	}

	public void setContractTextId(Long contractTextId) {
		this.contractTextId = contractTextId;
	}

	public Date getPayTime() {
		return payTime;
	}

	public void setPayTime(Date payTime) {
		this.payTime = payTime;
	}

	public Long getEnterpriseId() {
		return enterpriseId;
	}

	public void setEnterpriseId(Long enterpriseId) {
		this.enterpriseId = enterpriseId;
	}
}
