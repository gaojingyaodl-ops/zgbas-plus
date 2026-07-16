package com.spt.bas.client.vo;


import com.spt.bas.client.entity.ApplyDeliveryOut;

public class ApplyDeliveryVo extends ApplyDeliveryOut{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -741989257515736070L;
	private Long deliveryOutId;
	private Integer printCount;
	private String operation;
	private String subject;//摘要
	
	
	
	public String getSubject() {
		return subject;
	}
	
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public Integer getPrintCount() {
		return printCount;
	}
	public void setPrintCount(Integer printCount) {
		this.printCount = printCount;
	}
	public String getOperation() {
		return operation;
	}
	public void setOperation(String operation) {
		this.operation = operation;
	}	
	public Long getDeliveryOutId() {
		return deliveryOutId;
	}

	public void setDeliveryOutId(Long deliveryOutId) {
		this.deliveryOutId = deliveryOutId;
	}
}
