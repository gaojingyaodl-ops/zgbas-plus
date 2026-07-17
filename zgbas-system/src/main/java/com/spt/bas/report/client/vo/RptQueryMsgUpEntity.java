package com.spt.bas.report.client.vo;

public class RptQueryMsgUpEntity {

	private String pushChannel;
	private Long historyId; // PrimaryKey
	
	public Long getHistoryId() {
		return historyId;
	}
	public void setHistoryId(Long historyId) {
		this.historyId = historyId;
	}
	public String getPushChannel() {
		return pushChannel;
	}
	public void setPushChannel(String pushChannel) {
		this.pushChannel = pushChannel;
	}
}
