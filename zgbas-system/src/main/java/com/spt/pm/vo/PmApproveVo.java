/**
 * 
 */
package com.spt.pm.vo;

import com.spt.pm.entity.PmApprove;

/**
 * @author wlddh
 *
 */
public class PmApproveVo {

	private PmApprove approve;
	private String bizEntityJson;

	public PmApprove getApprove() {
		return approve;
	}

	public void setApprove(PmApprove approve) {
		this.approve = approve;
	}

	public String getBizEntityJson() {
		return bizEntityJson;
	}

	public void setBizEntityJson(String bizEntityJson) {
		this.bizEntityJson = bizEntityJson;
	}


}
