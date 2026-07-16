package com.spt.bas.client.entity;

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
 * 印章外借记录
 *
 */
@Entity
@Table(name = "t_seal_borrow")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class SealBorrow extends IdEntity implements IPmEntity{

	private static final long serialVersionUID = 1811174193465411133L;
	private Long keepUserId;			//保管人ID
	private String KeepUserName;		//保管人
	private Long applyUserId;			//申请人ID
	private String itemType;			//物品类型
	private String reason;				//外借原因
	private String address;				//外借地点
	private String remark;				//备注
	private String applyUserName;		//申请人
	private String companyName;			//公司名称
	private String fileId;				//附件ID
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
	private Date startDate;				//借出日期
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
	private Date endDate;				//归还日期
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
	private Date returnDate;			//实际归还日期
	private String sealStatus;			//印章状态
	private Long approveId;				//审批ID
	private Long enterpriseId;			//企业账套ID
	private String alreadyReturn;		//已归还物品
	@JsonSerialize(using = ToStringSerializer.class)
	private Boolean finSealFlg = false;	//包含财务章标识

	private Long deptId; //部门Id

	public Long getKeepUserId() {
		return keepUserId;
	}
	public void setKeepUserId(Long keepUserId) {
		this.keepUserId = keepUserId;
	}
	public String getKeepUserName() {
		return KeepUserName;
	}
	public void setKeepUserName(String keepUserName) {
		KeepUserName = keepUserName;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
	public String getCompanyName() {
		return companyName;
	}
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}
	public Date getStartDate() {
		return startDate;
	}
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	public Date getEndDate() {
		return endDate;
	}
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	public Date getReturnDate() {
		return returnDate;
	}
	public void setReturnDate(Date returnDate) {
		this.returnDate = returnDate;
	}
	public String getSealStatus() {
		return sealStatus;
	}
	public void setSealStatus(String sealStatus) {
		this.sealStatus = sealStatus;
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
	public String getItemType() {
		return itemType;
	}
	public void setItemType(String itemType) {
		this.itemType = itemType;
	}
	public String getAlreadyReturn() {
		return alreadyReturn;
	}
	public void setAlreadyReturn(String alreadyReturn) {
		this.alreadyReturn = alreadyReturn;
	}
	public Boolean getFinSealFlg() {
		return finSealFlg;
	}
	public void setFinSealFlg(Boolean finSealFlg) {
		this.finSealFlg = finSealFlg;
	}
	@Override
	public void setStatus(String status) {}

	public Long getDeptId() {
		return deptId;
	}

	public void setDeptId(Long deptId) {
		this.deptId = deptId;
	}
}
