package com.spt.bas.client.entity;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.spt.tools.jpa.vo.IdEntity;

/**
 * 厂商信息表
 */
@Entity
@Table(name = "t_bs_factory")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class BsFactory extends IdEntity{

	private static final long serialVersionUID = -8549038710987017427L;
	
	private String factoryName;//厂商名称
	private String contactName;//联系人
	private String contactPhone;//联系电话
	private String remark;//备注
	private Long enterpriseId;//企业账套ID
	@JsonSerialize(using = ToStringSerializer.class)
	private Boolean enableFlg;// 是否有效
	
	public String getFactoryName() {
		return factoryName;
	}
	public void setFactoryName(String factoryName) {
		this.factoryName = factoryName;
	}
	public String getContactName() {
		return contactName;
	}
	public void setContactName(String contactName) {
		this.contactName = contactName;
	}
	public String getContactPhone() {
		return contactPhone;
	}
	public void setContactPhone(String contactPhone) {
		this.contactPhone = contactPhone;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public Long getEnterpriseId() {
		return enterpriseId;
	}
	public void setEnterpriseId(Long enterpriseId) {
		this.enterpriseId = enterpriseId;
	}
	public Boolean getEnableFlg() {
		return enableFlg;
	}
	public void setEnableFlg(Boolean enableFlg) {
		this.enableFlg = enableFlg;
	}
	
	
	
	
	
}
