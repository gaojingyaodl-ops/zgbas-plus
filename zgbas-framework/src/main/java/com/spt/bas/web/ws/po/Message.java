package com.spt.bas.web.ws.po;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.Date;

public class Message {

	//发送者name
	public String from;
	//接收者name
	public String to;
	//发送的文本
	public String text;
	//发送时间
	@JSONField(format="yyyy-MM-dd HH:mm:ss")
	public Date date;
	//未读状态条数
	public Long countReadFlg;
	//未完成状态条数
	public Long countCompleteFlg;

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Long getCountReadFlg() {
		return countReadFlg;
	}

	public void setCountReadFlg(Long countReadFlg) {
		this.countReadFlg = countReadFlg;
	}

	public Long getCountCompleteFlg() {
		return countCompleteFlg;
	}

	public void setCountCompleteFlg(Long countCompleteFlg) {
		this.countCompleteFlg = countCompleteFlg;
	}
}
