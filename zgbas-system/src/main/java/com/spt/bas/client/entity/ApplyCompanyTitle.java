package com.spt.bas.client.entity;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.spt.pm.inter.IPmEntity;
import com.spt.tools.jpa.vo.IdEntity;

/**
 * 更换抬头
 */
@Entity
@Table(name = "t_apply_company_title")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class ApplyCompanyTitle extends IdEntity implements IPmEntity{

	
	private static final long serialVersionUID = -5740632692012909594L;
	
	private Long contractId;
	private String contractNo;
	private Long approveId;
	private String approveNo;
	private String status;
	private String oldOurCompanyName;
	private String newOurCompanyName;
	private String fileId;
	private String remark;
	public Long getContractId() {
		return contractId;
	}
	public void setContractId(Long contractId) {
		this.contractId = contractId;
	}
	public String getContractNo() {
		return contractNo;
	}
	public void setContractNo(String contractNo) {
		this.contractNo = contractNo;
	}
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
	public String getOldOurCompanyName() {
		return oldOurCompanyName;
	}
	public void setOldOurCompanyName(String oldOurCompanyName) {
		this.oldOurCompanyName = oldOurCompanyName;
	}
	public String getNewOurCompanyName() {
		return newOurCompanyName;
	}
	public void setNewOurCompanyName(String newOurCompanyName) {
		this.newOurCompanyName = newOurCompanyName;
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
	
	
}
