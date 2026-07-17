package com.spt.bas.report.client.vo;

public class RptPushTarget {

	private String id;
	private String mobile;
	private String email;

	public RptPushTarget() {

	}

	public RptPushTarget(String id, String mobile, String email) {
		this.id = id;
		this.mobile = mobile;
		this.email = email;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
}
