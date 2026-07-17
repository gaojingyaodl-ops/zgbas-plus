/**
 * 
 */
package com.spt.bas.report.client.vo;

import java.util.List;

/**
 * @author huangjian
 *
 */
public class RptQueryNotifyReadUpEntity {
	private List<String> historyIdList; //消息id列表
	private String userId;
	private String historyId;
	private String optType;	//操作类型：A-已读，D-删除
	private String category;//通知归类
	private String pushChannel;
	private String source;//来源,ios，andorid，web

	public List<String> getHistoryIdList() {
		return historyIdList;
	}

	public void setHistoryIdList(List<String> historyIdList) {
		this.historyIdList = historyIdList;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getHistoryId() {
		return historyId;
	}

	public void setHistoryId(String historyId) {
		this.historyId = historyId;
	}

	public String getOptType() {
		return optType;
	}

	public void setOptType(String optType) {
		this.optType = optType;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getPushChannel() {
		return pushChannel;
	}

	public void setPushChannel(String notifyChannel) {
		this.pushChannel = notifyChannel;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

}
