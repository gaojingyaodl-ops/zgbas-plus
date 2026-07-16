package com.spt.bas.client.entity;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.spt.pm.inter.IPmEntity;
import com.spt.tools.jpa.vo.IdEntity;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.format.annotation.DateTimeFormat;


import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;


/**
 * 代采赊销印章使用记录表(印章申请)
 *
 */
@Entity
@Table(name = "t_dcsx_seal_usage")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class SealUsageDCSX extends IdEntity implements IPmEntity{
	private static final long serialVersionUID = 5744196834421319570L;

	/**
	 * 合同id
	 */
	private Long contractId;

	private String businessType;

	private String contractNo;

	private String fileType;

	private String fileName;

	private String sealType;

	private String companyName;

	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
	private Date sealDate;

	private Date createdDate;

	private Date updatedDate;

	private Long approveId;

	private Long enterpriseId;

	private String fileId;

	private Long applyUserId;

	private String applyUserName;

	private String status;

	/**
	 * 品种
	 */
	private String productBrand;

	/**
	 *牌号
	 */
	private  String productNum;

	/**
	 * 合同数量（吨位数）
	 */
	private BigDecimal totalNumber;

	/**
	 * 厂商ID
	 */
	private Long factoryId;

	/**
	 * 厂商名称
	 */
	private String factoryName;

	/**
	 * 包装规格
	 */
	private String wrapSpecs;

	/**
	 * 质量标准
	 */
	private String qualityStandard; //质量标准  Y-原厂标准，G-过渡料，F-副牌料

	/**
	 * 我方企业名称
	 */
	private String ourCompanyName;

	/**
	 * 采购单价
	 */
	private BigDecimal bondAmount;

	/**
	 *合同总价
	 */
	private BigDecimal totalAmount;

	/**
	 *回款周期
	 */
	private Long creditDays;

	/**
	 *付款日期
	 */
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
	private Date payBondTime;
	/**
	 *补充条款
	 */
	private String extraTerm;

	/**
	 * 备注
	 */
	private String remark;

	private BigDecimal dealPrice;

	/**
	 * 关联审批id
	 */
	private Long realApproveId;

	/**
	 * 采购合同单价
	 */
	private BigDecimal buyPrice;

	/**
	 * 交货方式
	 */
	private String deliveryType;

	/**
	 * 交货日期
	 */
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
	private Date deliveryDate;

	/**
	 * 下游交货日期
	 */
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
	private Date sellDeliveryDate;
	
	/**
	 * 交货地点
	 */
	private String deliveryAddr;

	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
	private Date lastPayDate;

	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
	private Date payFullTime;

	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
	private Date buyPayFullTime;

	public Boolean getBusinessFlg() {
		return BusinessFlg;
	}

	public void setBusinessFlg(Boolean businessFlg) {
		BusinessFlg = businessFlg;
	}

	private Boolean BusinessFlg;

	private String ourBankName;

	private String ourBankAccount;

	/**
	 * 部门Id
	 * @param approveId
	 */
	private Long deptId;

	@Override
	public void setApproveId(Long approveId) {
		this.approveId = approveId;
	}

	@Override
	public void setStatus(String status) {
		this.status = status;
	}

	public Long getContractId() {
		return contractId;
	}

	public void setContractId(Long contractId) {
		this.contractId = contractId;
	}

	public String getBusinessType() {
		return businessType;
	}

	public void setBusinessType(String businessType) {
		this.businessType = businessType;
	}

	public String getContractNo() {
		return contractNo;
	}

	public void setContractNo(String contractNo) {
		this.contractNo = contractNo;
	}

	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getSealType() {
		return sealType;
	}

	public void setSealType(String sealType) {
		this.sealType = sealType;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public Date getSealDate() {
		return sealDate;
	}

	public void setSealDate(Date sealDate) {
		this.sealDate = sealDate;
	}

	@Override
	public Date getCreatedDate() {
		return createdDate;
	}

	@Override
	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	@Override
	public Date getUpdatedDate() {
		return updatedDate;
	}

	@Override
	public void setUpdatedDate(Date updatedDate) {
		this.updatedDate = updatedDate;
	}

	public Long getApproveId() {
		return approveId;
	}

	public Long getEnterpriseId() {
		return enterpriseId;
	}

	@Override
	public void setEnterpriseId(Long enterpriseId) {
		this.enterpriseId = enterpriseId;
	}

	public String getFileId() {
		return fileId;
	}

	@Override
	public void setFileId(String fileId) {
		this.fileId = fileId;
	}

	public Long getApplyUserId() {
		return applyUserId;
	}

	public void setApplyUserId(Long applyUserId) {
		this.applyUserId = applyUserId;
	}

	public String getApplyUserName() {
		return applyUserName;
	}

	public void setApplyUserName(String applyUserName) {
		this.applyUserName = applyUserName;
	}

	public String getStatus() {
		return status;
	}

	public String getProductBrand() {
		return productBrand;
	}

	public void setProductBrand(String productBrand) {
		this.productBrand = productBrand;
	}

	public String getProductNum() {
		return productNum;
	}

	public void setProductNum(String productNum) {
		this.productNum = productNum;
	}

	public BigDecimal getTotalNumber() {
		return totalNumber;
	}

	public void setTotalNumber(BigDecimal totalNumber) {
		this.totalNumber = totalNumber;
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

	public String getOurCompanyName() {
		return ourCompanyName;
	}

	public void setOurCompanyName(String ourCompanyName) {
		this.ourCompanyName = ourCompanyName;
	}

	public BigDecimal getBondAmount() {
		return bondAmount;
	}

	public void setBondAmount(BigDecimal bondAmount) {
		this.bondAmount = bondAmount;
	}

	public BigDecimal getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
	}

	public Long getCreditDays() {
		return creditDays;
	}

	public void setCreditDays(Long creditDays) {
		this.creditDays = creditDays;
	}

	public Date getPayBondTime() {
		return payBondTime;
	}

	public void setPayBondTime(Date payBondTime) {
		this.payBondTime = payBondTime;
	}

	public String getExtraTerm() {
		return extraTerm;
	}

	public void setExtraTerm(String extraTerm) {
		this.extraTerm = extraTerm;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public BigDecimal getDealPrice() {
		return dealPrice;
	}

	public void setDealPrice(BigDecimal dealPrice) {
		this.dealPrice = dealPrice;
	}

	public Date getPayFullTime() {
		return payFullTime;
	}

	public void setPayFullTime(Date payFullTime) {
		this.payFullTime = payFullTime;
	}

	public Date getBuyPayFullTime() {
		return buyPayFullTime;
	}

	public void setBuyPayFullTime(Date buyPayFullTime) {
		this.buyPayFullTime = buyPayFullTime;
	}

	public Long getRealApproveId() {
		return realApproveId;
	}

	public void setRealApproveId(Long realApproveId) {
		this.realApproveId = realApproveId;
	}

	public BigDecimal getBuyPrice() {
		return buyPrice;
	}

	public void setBuyPrice(BigDecimal buyPrice) {
		this.buyPrice = buyPrice;
	}

	public Date getLastPayDate() {
		return lastPayDate;
	}

	public void setLastPayDate(Date lastPayDate) {
		this.lastPayDate = lastPayDate;
	}

	public String getDeliveryType() {
		return deliveryType;
	}

	public void setDeliveryType(String deliveryType) {
		this.deliveryType = deliveryType;
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

	public String getDeliveryAddr() {
		return deliveryAddr;
	}

	public void setDeliveryAddr(String deliveryAddr) {
		this.deliveryAddr = deliveryAddr;
	}

	public Long getDeptId() {
		return deptId;
	}

	public void setDeptId(Long deptId) {
		this.deptId = deptId;
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
}
