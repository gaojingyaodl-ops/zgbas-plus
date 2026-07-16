/**
 *
 */
package com.spt.pm.vo;

import com.spt.pm.entity.PmApprove;

/**
 * @author wlddh
 *
 */
public class PmApproveDownVo extends PmApprove {

	private static final long serialVersionUID = 3254464807725919375L;
	private String currApproverUserName; // 当前审批人

	private String openUrl;

	private String mobileOpenUrl;

	private String statusName;

	public String getCurrApproverUserName() {
		return currApproverUserName;
	}

	public void setCurrApproverUserName(String currApproverUserName) {
		this.currApproverUserName = currApproverUserName;
	}

	public String getOpenUrl() {
		return openUrl;
	}

	public void setOpenUrl(String openUrl) {
		this.openUrl = openUrl;
	}

	public String getMobileOpenUrl() {
		return mobileOpenUrl;
	}

	public void setMobileOpenUrl(String mobileOpenUrl) {
		this.mobileOpenUrl = mobileOpenUrl;
	}

	public String getStatusName() {
		return statusName;
	}

	public void setStatusName(String statusName) {
		this.statusName = statusName;
	}
}
