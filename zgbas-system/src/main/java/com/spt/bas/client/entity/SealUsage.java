package com.spt.bas.client.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
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
 * 印章使用记录表(印章申请)
 *
 */
@Entity
@Table(name = "t_seal_usage")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class SealUsage extends IdEntity implements IPmEntity{

	private static final long serialVersionUID = 5744196834421309570L;
	private Long contractId;			//合同ID
	private Boolean businessFlg = false;//业务用印标识
	private String businessType;		//业务用印类型 B-采购 S-销售  F-服务
	private String contractNo;			//合同编号
	private String fileType;			//文件类型
	private String fileName;			//文件名称
	private String customerName;		//客户/提供方

	private BigDecimal totalAmount;		//合同总金额
	private String sealType;			//印章类型
	private String companyName;			//公司名称
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
	private Date sealDate;				//印章日期
	private Long approveId;				//审批ID
	private Long enterpriseId;			//企业账套ID
	private String fileId;				//附件ID

	private String remark;				//备注
	private Long applyUserId;			//申请人ID
	private String applyUserName;		//申请人名称
	private Long realApproveId;			//关联审批ID

	@JsonSerialize(using = ToStringSerializer.class)
	private Boolean finSealFlg = false;	//包含财务章标识


	//c印章申请的字段大致都在
    private Long deptId;//部门Id

	/**
	 * 盖章类型
	 */
	private String stampType;

	/**
	 * 物流附件单ID
	 */
	private Long logisticsFileId;

	/**
	 * 所属区域
	 */
	private String ownRegion;

	private String virtualType;

	public String getStampType() {
		return stampType;
	}

	public void setStampType(String stampType) {
		this.stampType = stampType;
	}

	//是否是代采中间业务盖章
	private Boolean chainDc= false;//业务用印标识

	public Boolean getChainDc() {
		return chainDc;
	}

	public void setChainDc(Boolean chainDc) {
		this.chainDc = chainDc;
	}

	public String getContractNo() {
		return contractNo;
	}
	public void setContractNo(String contractNo) {
		this.contractNo = contractNo;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getCustomerName() {
		return customerName;
	}
	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}
	public BigDecimal getTotalAmount() {
		return totalAmount;
	}
	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
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
	public Long getApproveId() {
		return approveId;
	}
	public void setApproveId(Long approveId) {
		this.approveId = approveId;
	}
	public Long getEnterpriseId() {
		return enterpriseId;
	}
	public void setEnterpriseId(Long enterpriseId) {
		this.enterpriseId = enterpriseId;
	}
	public String getFileId() {
		return fileId;
	}
	public void setFileId(String fileId) {
		this.fileId = fileId;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
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
	public String getFileType() {
		return fileType;
	}
	public void setFileType(String fileType) {
		this.fileType = fileType;
	}
	public Boolean getFinSealFlg() {
		return finSealFlg;
	}
	public void setFinSealFlg(Boolean finSealFlg) {
		this.finSealFlg = finSealFlg;
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

	public Long getRealApproveId() {
		return realApproveId;
	}

	public void setRealApproveId(Long realApproveId) {
		this.realApproveId = realApproveId;
	}

	public Boolean getBusinessFlg() {
		return businessFlg;
	}

	public void setBusinessFlg(Boolean businessFlg) {
		this.businessFlg = businessFlg;
	}

	@Override
	public void setStatus(String status) {
		// TODO Auto-generated method stub
		
	}

	public Long getDeptId() {
		return deptId;
	}

	public void setDeptId(Long deptId) {
		this.deptId = deptId;
	}

	public Long getLogisticsFileId() {
		return logisticsFileId;
	}

	public void setLogisticsFileId(Long logisticsFileId) {
		this.logisticsFileId = logisticsFileId;
	}

	public String getOwnRegion() {
		return ownRegion;
	}

	public void setOwnRegion(String ownRegion) {
		this.ownRegion = ownRegion;
	}

	public String getVirtualType() {
		return virtualType;
	}

	public void setVirtualType(String virtualType) {
		this.virtualType = virtualType;
	}
}
