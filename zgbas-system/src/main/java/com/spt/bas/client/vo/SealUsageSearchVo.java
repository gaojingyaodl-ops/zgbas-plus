package com.spt.bas.client.vo;

import com.spt.tools.core.bean.PageSearchVo;

public class SealUsageSearchVo extends PageSearchVo{
	private String fileType;			//文件类型
	private String fileName;			//文件名称
	private String customerName;		//客户/提供方
	private String sealType;			//印章类型
	private String companyName;			//公司名称
	private String applyUserName;		//申请人名称
	private String approveNo;			//审批编号
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
	public String getCustomerName() {
		return customerName;
	}
	public void setCustomerName(String customerName) {
		this.customerName = customerName;
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
	public String getApplyUserName() {
		return applyUserName;
	}
	public void setApplyUserName(String applyUserName) {
		this.applyUserName = applyUserName;
	}
	public String getApproveNo() {
		return approveNo;
	}
	public void setApproveNo(String approveNo) {
		this.approveNo = approveNo;
	}
	
	
}
