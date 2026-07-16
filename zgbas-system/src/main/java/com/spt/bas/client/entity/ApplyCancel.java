package com.spt.bas.client.entity;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.spt.pm.inter.IPmEntity;
import com.spt.tools.jpa.vo.IdEntity;

/**
 * 申请单-作废申请表
 * 
 * @author wlddh
 *
 */
@Entity
@Table(name = "t_apply_cancel")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class ApplyCancel extends IdEntity implements IPmEntity{
	private static final long serialVersionUID = 1L;
	private Long approveId;// 审批id
	private String approveNo;// 审批编号
	private String status;// 审批状态
	private String contractId;// 合同id
	private String contractNo;// 合同编号
	private String contractStatus;//合同状态
	private String cancelType;// 作废类型:"P":付款作废;"E":收款作废;"I":入库作废;"O"：出库作废;"N":开票作废;"V":"收票作废";"CN":合同作废
	private String remark;// 作废原因
	private String fileId;// 附件id
	private Long enterpriseId;// 企业帐套ID

	public Long getApproveId() {
		return approveId;
	}

	public void setApproveId(Long approveId) {
		this.approveId = approveId;
	}

	public String getApproveNo() {
		return approveNo;
	}

	public void setApproveNo(String approveNo) {
		this.approveNo = approveNo;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getContractId() {
		return contractId;
	}

	public void setContractId(String contractId) {
		this.contractId = contractId;
	}

	public String getContractNo() {
		return contractNo;
	}

	public void setContractNo(String contractNo) {
		this.contractNo = contractNo;
	}

	public String getCancelType() {
		return cancelType;
	}

	public void setCancelType(String cancelType) {
		this.cancelType = cancelType;
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

	public String getContractStatus() {
		return contractStatus;
	}

	public void setContractStatus(String contractStatus) {
		this.contractStatus = contractStatus;
	}
	
}
