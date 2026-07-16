package com.spt.pm.entity;

import com.spt.pm.inter.IPmEntity;
import com.spt.tools.jpa.vo.IdEntity;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Entity;
import javax.persistence.Table;
/**
 * 审批通用内容表
 *
 */
@Entity
@Table(name = "t_pm_approve_contents")
@Cache(usage = CacheConcurrencyStrategy.NONE)
public class PmApproveContents extends IdEntity implements IPmEntity{

	private static final long serialVersionUID = -6722680353072513310L;
	private String contents;			//审批内容JSON
	private Long approveId;				//审批ID
	private Long enterpriseId;			//企业账套ID
	private String fileId;				//附件ID
	private String subject;				//标题
	private String status;
	private String applyName;			//通用 审批单名称
	private Long realApproveId;			//关联审批单ID
	private Long deptId;

	/**
	 * 安心签合同编号
	 */
	private String cfcaContractNo;

	/**
	 * 签署短链接
	 */
	private String signShortUrl;

	public String getContents() {
		return contents;
	}
	public void setContents(String contents) {
		this.contents = contents;
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
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getApplyName() {
		return applyName;
	}
	public void setApplyName(String applyName) {
		this.applyName = applyName;
	}

	public Long getRealApproveId() {
		return realApproveId;
	}

	public void setRealApproveId(Long realApproveId) {
		this.realApproveId = realApproveId;
	}

	public Long getDeptId() {
		return deptId;
	}

	public void setDeptId(Long deptId) {
		this.deptId = deptId;
	}

	public String getCfcaContractNo() {
		return cfcaContractNo;
	}

	public void setCfcaContractNo(String cfcaContractNo) {
		this.cfcaContractNo = cfcaContractNo;
	}

	public String getSignShortUrl() {
		return signShortUrl;
	}

	public void setSignShortUrl(String signShortUrl) {
		this.signShortUrl = signShortUrl;
	}
}
