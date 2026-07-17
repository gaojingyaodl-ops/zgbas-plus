/**
 * 
 */
package com.spt.bas.report.client.vo;

import java.util.Date;

import com.spt.tools.core.bean.PageSearchVo;

/**
 * @author huangjian
 *
 */
public class RptQueryNotifyHistoryUpEntity extends PageSearchVo {
	private String appCode;//应用代码
	private String pushType;
	private String category;//通知归类
	private String title; // 标题
	private String pushContent; // 内容
	private String pushChannel; // 渠道
	private String notifyTo;
	private String readFlag;
	private String businessId;//类务id
    private String pushHisId; //分表公用唯一性键值

	private Date dateFrom;
	private Date dateTo;
	
	public String getPushType() {
		return pushType;
	}

	public void setPushType(String pushType) {
		this.pushType = pushType;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getBusinessId() {
		return businessId;
	}

	public void setBusinessId(String businessId) {
		this.businessId = businessId;
	}

	
	public String getReadFlag() {
		return readFlag;
	}

	public void setReadFlag(String readFlag) {
		this.readFlag = readFlag;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String notifyTitle) {
		this.title = notifyTitle;
	}

	public String getPushContent() {
		return pushContent;
	}

	public void setPushContent(String notifyContent) {
		this.pushContent = notifyContent;
	}

	public String getPushChannel() {
		return pushChannel;
	}

	public void setPushChannel(String notifyChannels) {
		this.pushChannel = notifyChannels;
	}

	public Date getDateFrom() {
		return dateFrom;
	}

	public void setDateFrom(Date dateFrom) {
		this.dateFrom = dateFrom;
	}

	public Date getDateTo() {
		return dateTo;
	}

	public void setDateTo(Date dateTo) {
		this.dateTo = dateTo;
	}

	public String getNotifyTo() {
		return notifyTo;
	}

	public void setNotifyTo(String notifyTo) {
		this.notifyTo = notifyTo;
	}

	
	public String getPushHisId() {
		return pushHisId;
	}

	public void setPushHisId(String pushHisId) {
		this.pushHisId = pushHisId;
	}

	public String getAppCode() {
		return appCode;
	}

	public void setAppCode(String appCode) {
		this.appCode = appCode;
	}

}
