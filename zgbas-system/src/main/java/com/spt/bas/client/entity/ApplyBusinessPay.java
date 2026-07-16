package com.spt.bas.client.entity;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.spt.pm.entity.PmApprove;
import com.spt.tools.core.bean.PageSearchVo;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.spt.pm.inter.IPmEntity;
import com.spt.tools.jpa.vo.IdEntity;
@Entity
@Table(name = "t_apply_business_pay")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class ApplyBusinessPay extends  IdEntity  implements IPmEntity {
	private static final long serialVersionUID = -6785852624705590482L;
	private String belogDept;				//所属部门
	private BigDecimal dealAmount;			//金额
	private Long approveId;					//审批ID
	private String status;					//状态 N-新增，A-审批中，B-驳回，D-完成
	private Long applyUserId;				//申请人ID
	private String applyUserName;			//申请人
	private String costType;				//费用类型
	private String companyName;				//公司名称
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
	private Date applyDate;					//申请日期
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
	private Date requestPayDate;			//要求支付日期
	private String subject;					//摘要
	private String remark;					//备注
	private String fileId;					//附件
	private Long enterpriseId;				//企业账套ID
	private Long deptId;                     //部门Id
	private Long contractId;                     //合同Id

	/**
	 * 合并付款list
	 */
	private String contractList;
	/**
	 * 收款方
	 */
	private String receiveCompanyName;
	/**
	 * 银行账户
	 */
	private String bankAccount;

	/**
	 * 银行名字
	 */
	private  String bankName;

	/**
	 * 承运商
	 */
	private String carrier;

	/**
	 * 所属区域
	 */
	private String ownRegion;

	/**
	 * 关联合同ID
	 */
	private String linkContractIds;
	/**
	 * 关联合同号
	 */
	private String linkContractNos;
	/**
	 * 诉讼案件ID
	 */
	private String litigationCaseId;
	/**
	 *诉讼案件费用申请类型
	 */
	private String litigationCaseType;

	public String getOwnRegion() {
		return ownRegion;
	}

	public void setOwnRegion(String ownRegion) {
		this.ownRegion = ownRegion;
	}

	public String getCarrier() {
		return carrier;
	}

	public void setCarrier(String carrier) {
		this.carrier = carrier;
	}

	public Long getContractId() {
		return contractId;
	}

	public void setContractId(Long contractId) {
		this.contractId = contractId;
	}

	public String getContractList() {
		return contractList;
	}

	public void setContractList(String contractList) {
		this.contractList = contractList;
	}

	public String getReceiveCompanyName() {
		return receiveCompanyName;
	}

	public void setReceiveCompanyName(String receiveCompanyName) {
		this.receiveCompanyName = receiveCompanyName;
	}

	public String getBankAccount() {
		return bankAccount;
	}

	public void setBankAccount(String bankAccount) {
		this.bankAccount = bankAccount;
	}

	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public String getBelogDept() {
		return belogDept;
	}
	public void setBelogDept(String belogDept) {
		this.belogDept = belogDept;
	}
	public BigDecimal getDealAmount() {
		return dealAmount;
	}
	public void setDealAmount(BigDecimal dealAmount) {
		this.dealAmount = dealAmount;
	}
	public Long getApproveId() {
		return approveId;
	}
	public void setApproveId(Long approveId) {
		this.approveId = approveId;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
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
	public String getCostType() {
		return costType;
	}
	public void setCostType(String costType) {
		this.costType = costType;
	}
	public Date getApplyDate() {
		return applyDate;
	}
	public void setApplyDate(Date applyDate) {
		this.applyDate = applyDate;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public String getFileId() {
		return fileId;
	}
	public void setFileId(String fileId) {
		this.fileId = fileId;
	}
	public Long getEnterpriseId() {
		return enterpriseId;
	}
	public void setEnterpriseId(Long enterpriseId) {
		this.enterpriseId = enterpriseId;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public Long getDeptId() {
		return deptId;
	}

	public void setDeptId(Long deptId) {
		this.deptId = deptId;
	}

	public Date getRequestPayDate() {
		return requestPayDate;
	}

	public void setRequestPayDate(Date requestPayDate) {
		this.requestPayDate = requestPayDate;
	}

	public String getLinkContractIds() {
		return linkContractIds;
	}

	public void setLinkContractIds(String linkContractIds) {
		this.linkContractIds = linkContractIds;
	}

	public String getLinkContractNos() {
		return linkContractNos;
	}

	public void setLinkContractNos(String linkContractNos) {
		this.linkContractNos = linkContractNos;
	}

	public String getLitigationCaseId() {
		return litigationCaseId;
	}

	public void setLitigationCaseId(String litigationCaseId) {
		this.litigationCaseId = litigationCaseId;
	}

	public String getLitigationCaseType() {
		return litigationCaseType;
	}

	public void setLitigationCaseType(String litigationCaseType) {
		this.litigationCaseType = litigationCaseType;
	}

	@Override
	public String toString() {
		return "ApplyBusinessPay{" +
				"belogDept='" + belogDept + '\'' +
				", dealAmount=" + dealAmount +
				", approveId=" + approveId +
				", status='" + status + '\'' +
				", applyUserId=" + applyUserId +
				", applyUserName='" + applyUserName + '\'' +
				", costType='" + costType + '\'' +
				", companyName='" + companyName + '\'' +
				", applyDate=" + applyDate +
				", subject='" + subject + '\'' +
				", remark='" + remark + '\'' +
				", fileId='" + fileId + '\'' +
				", enterpriseId=" + enterpriseId +
				", id=" + id +
				", createdDate=" + createdDate +
				", updatedDate=" + updatedDate +
				'}';
	}
}
