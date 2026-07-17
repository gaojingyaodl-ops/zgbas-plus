package com.spt.bas.report.client.vo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RptPushMsgVo {
	private String notifyTitle;// 消息标题
	private String module;// 消息对应模块
	// Z1:专区挂单详情，Z2:专区合同详情;N1:撮合报单详情,N2:撮合报单确认详情,N3:撮合订单成交确认详情，N4:撮合订单详情
	private String fromModule;
	private String pushType; // 通知类型
	private String businessId; // 业务主键
	private List<String> userIds; // 通知用户
	private String content;// 消息内容
	private Map<String, Object> param = new HashMap<>();// 模板参数

	public String getBusinessId() {
		return businessId;
	}

	public void setBusinessId(String businessId) {
		this.businessId = businessId;
	}

	public List<String> getUserIds() {
		return userIds;
	}

	public void setUserIds(List<String> userIds) {
		this.userIds = userIds;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getNotifyTitle() {
		return notifyTitle;
	}

	public void setNotifyTitle(String notifyTitle) {
		this.notifyTitle = notifyTitle;
	}

	public String getModule() {
		return module;
	}

	public void setModule(String module) {
		this.module = module;
	}

	public String getFromModule() {
		return fromModule;
	}

	public void setFromModule(String fromModule) {
		this.fromModule = fromModule;
	}


	public Map<String, Object> getParam() {
		return param;
	}

	public void setParam(Map<String, Object> param) {
		this.param = param;
	}

	public String getPushType() {
		return pushType;
	}

	public void setPushType(String pushType) {
		this.pushType = pushType;
	}

}
