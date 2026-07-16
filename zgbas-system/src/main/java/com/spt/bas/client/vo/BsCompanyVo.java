package com.spt.bas.client.vo;

import com.spt.bas.client.entity.BsCompany;

import java.math.BigDecimal;

public class BsCompanyVo extends BsCompany {
	private static final long serialVersionUID = 4743464668113555445L;
	private Boolean shareFlag;
	private Boolean myFlag = true;
	private Boolean myDeptFlg = false;
	private String text;
	private BigDecimal remainCreditAmount;
	private String appCode = "bps";
	private String timesptms;
	private String signKey;
	private String lines;
	private String muName;//领用人
	private String muDeptName;//领用人部门
	private String deptName;
	private String ooaName;//开户人
	private String accessReportExist;
	private String shareUserNames;
	private String wfqAuthH5url;

	/**
	 * 授信信息
	 */
	private String creditInfo;

	public String getCreditInfo() {
		return creditInfo;
	}

	public void setCreditInfo(String creditInfo) {
		this.creditInfo = creditInfo;
	}

	public String getWfqAuthH5url() {
		return wfqAuthH5url;
	}

	public void setWfqAuthH5url(String wfqAuthH5url) {
		this.wfqAuthH5url = wfqAuthH5url;
	}

	public String getMuName() {
		return muName;
	}

	public void setMuName(String muName) {
		this.muName = muName;
	}

	public String getLines() {
		return lines;
	}

	public void setLines(String lines) {
		this.lines = lines;
	}

	public Boolean getMyFlag() {
		return myFlag;
	}

	public void setMyFlag(Boolean isAllowFllow) {
		this.myFlag = isAllowFllow;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public Boolean getShareFlag() {
		return shareFlag;
	}

	public void setShareFlag(Boolean shareFlag) {
		this.shareFlag = shareFlag;
	}

	public Boolean getMyDeptFlg() {
		return myDeptFlg;
	}

	public void setMyDeptFlg(Boolean myDeptFlg) {
		this.myDeptFlg = myDeptFlg;
	}

	public BigDecimal getRemainCreditAmount() {
		return remainCreditAmount;
	}

	public void setRemainCreditAmount(BigDecimal remainCreditAmount) {
		this.remainCreditAmount = remainCreditAmount;
	}

	public String getAppCode() {
		return appCode;
	}

	public void setAppCode(String appCode) {
		this.appCode = appCode;
	}

	public String getTimesptms() {
		return timesptms;
	}

	public void setTimesptms(String timesptms) {
		this.timesptms = timesptms;
	}

	public String getSignKey() {
		return signKey;
	}

	public void setSignKey(String signKey) {
		this.signKey = signKey;
	}

	public String getMuDeptName() {
		return muDeptName;
	}

	public void setMuDeptName(String muDeptName) {
		this.muDeptName = muDeptName;
	}

	public String getDeptName() {
		return deptName;
	}

	public void setDeptName(String deptName) {
		this.deptName = deptName;
	}

	public String getOoaName() {
		return ooaName;
	}

	public void setOoaName(String ooaName) {
		this.ooaName = ooaName;
	}

	public String getAccessReportExist() {
		return accessReportExist;
	}

	public void setAccessReportExist(String accessReportExist) {
		this.accessReportExist = accessReportExist;
	}

	public String getShareUserNames() {
		return shareUserNames;
	}

	public void setShareUserNames(String shareUserNames) {
		this.shareUserNames = shareUserNames;
	}
}
