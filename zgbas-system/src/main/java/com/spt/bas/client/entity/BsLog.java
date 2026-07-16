package com.spt.bas.client.entity;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import com.spt.tools.jpa.vo.IdEntity;

/**
 * 描述：系统日志entity
 * 
 */
@Entity
@Table(name = "T_BS_LOG")
@DynamicInsert
@DynamicUpdate
public class BsLog extends IdEntity {
	private static final long serialVersionUID = -4572776766720084534L;
	/** ip地址 */
	private String ipAddre;
	/** 远程端口 */
	private Integer remortPort;
	/** 操作类型 (直接用中文:0-增加；1-删除；2-修改；3-登入；4-退出) */
	private String operation;
	/** 操作人ID */
	private Long operatorId;
	private String operatorName;
	/** 日志内容 */
	private String remark;
	/** 操作目标 */
	private String targetName;

	public String getIpAddre() {
		return ipAddre;
	}

	public void setIpAddre(String ipAddre) {
		this.ipAddre = ipAddre;
	}

	public Integer getRemortPort() {
		return remortPort;
	}

	public void setRemortPort(Integer remortPort) {
		this.remortPort = remortPort;
	}

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

	public Long getOperatorId() {
		return operatorId;
	}

	public void setOperatorId(Long operatorId) {
		this.operatorId = operatorId;
	}

	public String getTargetName() {
		return targetName;
	}

	public void setTargetName(String targetName) {
		this.targetName = targetName;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getOperatorName() {
		return operatorName;
	}

	public void setOperatorName(String operatorName) {
		this.operatorName = operatorName;
	}

}
