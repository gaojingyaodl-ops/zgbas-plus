package com.spt.bas.client.entity;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.spt.tools.jpa.vo.IdEntity;

/**
 * 印章使用加盖记录
 *
 */
@Entity
@Table(name = "t_seal_usage_ophis")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class SealUsageOphis extends IdEntity{

	private static final long serialVersionUID = -5611167979156415973L;
	private Long sealUsageId;				//印章使用ID
	private String sealHisDate;				//加盖日期
	private String remark;					//加盖备注
	private Long opUserId;					//操作人ID
	private String opUserName;				//操作人
	private Long enterpriseId;				//企业账套ID
	public Long getSealUsageId() {
		return sealUsageId;
	}
	public void setSealUsageId(Long sealUsageId) {
		this.sealUsageId = sealUsageId;
	}
	public String getSealHisDate() {
		return sealHisDate;
	}
	public void setSealHisDate(String sealHisDate) {
		this.sealHisDate = sealHisDate;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public Long getOpUserId() {
		return opUserId;
	}
	public void setOpUserId(Long opUserId) {
		this.opUserId = opUserId;
	}
	public String getOpUserName() {
		return opUserName;
	}
	public void setOpUserName(String opUserName) {
		this.opUserName = opUserName;
	}
	public Long getEnterpriseId() {
		return enterpriseId;
	}
	public void setEnterpriseId(Long enterpriseId) {
		this.enterpriseId = enterpriseId;
	}

}
