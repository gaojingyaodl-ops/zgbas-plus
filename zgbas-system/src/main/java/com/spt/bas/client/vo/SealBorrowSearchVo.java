package com.spt.bas.client.vo;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.spt.tools.core.bean.PageSearchVo;

public class SealBorrowSearchVo extends PageSearchVo{
	private Long applyUserId;			//申请人ID
	private String itemType;			//物品类型
	private String companyName;			//公司名称
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
	public Long getApplyUserId() {
		return applyUserId;
	}
	public void setApplyUserId(Long applyUserId) {
		this.applyUserId = applyUserId;
	}
	public String getItemType() {
		return itemType;
	}
	public void setItemType(String itemType) {
		this.itemType = itemType;
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
	
	
}
