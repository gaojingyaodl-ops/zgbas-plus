package com.spt.pm.entity;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.spt.tools.jpa.vo.IdEntity;

@Entity
@Table(name = "t_pm_process_node")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class PmProcessNode extends IdEntity {

	/**
	 * 流程节点
	 */
	private static final long serialVersionUID = -5930593879665988642L;
	private String nodeCode; // 节点代码
	private String nodeName; // 节点名称
	private String nodeUserId; // 节点负责人,多人用|隔开
	private String nodeType; // 节点类型:U-人员，D-部门，G-组 
	private String remark; // 备注
	@JsonSerialize(using = ToStringSerializer.class)
	private Boolean enableFlg; // 是否有效

	private Long enterpriseId;//企业帐套ID
	public String getNodeCode() {
		return nodeCode;
	}

	public void setNodeCode(String nodeCode) {
		this.nodeCode = nodeCode;
	}

	public String getNodeName() {
		return nodeName;
	}

	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}

	public String getNodeUserId() {
		return nodeUserId;
	}

	public void setNodeUserId(String nodeUserId) {
		this.nodeUserId = nodeUserId;
	}

	public String getNodeType() {
		return nodeType;
	}

	public void setNodeType(String nodeType) {
		this.nodeType = nodeType;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public Boolean getEnableFlg() {
		return enableFlg;
	}

	public void setEnableFlg(Boolean enableFlg) {
		this.enableFlg = enableFlg;
	}

	public Long getEnterpriseId() {
		return enterpriseId;
	}

	public void setEnterpriseId(Long enterpriseId) {
		this.enterpriseId = enterpriseId;
	}

}
