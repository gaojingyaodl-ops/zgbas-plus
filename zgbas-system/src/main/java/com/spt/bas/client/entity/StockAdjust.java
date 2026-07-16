package com.spt.bas.client.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.spt.tools.jpa.vo.IdEntity;

/**
 * 库存盘点
 */
@Entity
@Table(name="t_stock_adjust")
public class StockAdjust extends IdEntity{

	private static final long serialVersionUID = -7269634340508874329L;
	private String businessNo; //调整单号
	private String adjustStatus;//调整状态
	private String remark;//备注
	private Date adjustDate;//调整日期
	private Long enterpriseId;//企业套账Id
	private Long createUserId;//创建人id
	private String createUserName;//创建人姓名
	public String getBusinessNo() {
		return businessNo;
	}
	public void setBusinessNo(String businessNo) {
		this.businessNo = businessNo;
	}
	public String getAdjustStatus() {
		return adjustStatus;
	}
	public void setAdjustStatus(String adjustStatus) {
		this.adjustStatus = adjustStatus;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public Date getAdjustDate() {
		return adjustDate;
	}
	public void setAdjustDate(Date adjustDate) {
		this.adjustDate = adjustDate;
	}
	public Long getEnterpriseId() {
		return enterpriseId;
	}
	public void setEnterpriseId(Long enterpriseId) {
		this.enterpriseId = enterpriseId;
	}
	public Long getCreateUserId() {
		return createUserId;
	}
	public void setCreateUserId(Long createUserId) {
		this.createUserId = createUserId;
	}
	public String getCreateUserName() {
		return createUserName;
	}
	public void setCreateUserName(String createUserName) {
		this.createUserName = createUserName;
	}
	

}
