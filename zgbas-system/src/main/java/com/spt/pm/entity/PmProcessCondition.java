/**
 * 
 */
package com.spt.pm.entity;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.spt.tools.jpa.vo.IdEntity;

/**
 * 流程条件
 * @author wlddh
 *
 */
@Entity
@Table(name = "t_pm_process_condition")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class PmProcessCondition extends IdEntity {

	private static final long serialVersionUID = 6658243740074115160L;
	private Long processId;// 流程id
	private String conditionName;// 条件名称
	private String conditionValue;// 条件内容
	private Long dispOrderNo;// 序号
	private String remark;// 备注
	@JsonSerialize(using = ToStringSerializer.class)
	private Boolean enableFlg = true;// 是否有效 默认有效

	private Long enterpriseId;//企业帐套ID
	public Long getProcessId() {
		return processId;
	}

	public void setProcessId(Long processId) {
		this.processId = processId;
	}

	public String getConditionName() {
		return conditionName;
	}

	public void setConditionName(String conditionName) {
		this.conditionName = conditionName;
	}

	public String getConditionValue() {
		return conditionValue;
	}

	public void setConditionValue(String conditionValue) {
		this.conditionValue = conditionValue;
	}

	public Long getDispOrderNo() {
		return dispOrderNo;
	}

	public void setDispOrderNo(Long dispOrderNo) {
		this.dispOrderNo = dispOrderNo;
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
