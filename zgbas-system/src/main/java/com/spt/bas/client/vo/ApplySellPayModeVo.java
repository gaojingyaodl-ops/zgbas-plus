package com.spt.bas.client.vo;

public class ApplySellPayModeVo {

	private String serialNumber;
	private String content;
	
	public String getSerialNumber() {
		return serialNumber;
	}
	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public ApplySellPayModeVo(String serialNumber, String content) {
		super();
		this.serialNumber = serialNumber;
		this.content = content;
	}
	
	
	
}
