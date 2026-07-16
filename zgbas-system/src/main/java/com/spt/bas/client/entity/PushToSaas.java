package com.spt.bas.client.entity;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.spt.tools.jpa.vo.IdEntity;

/**
 * 审批状态推送至Saas
 */
@Entity
@Table(name = "t_push_to_saas")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class PushToSaas extends IdEntity{

	private static final long serialVersionUID = -8057360116897922733L;
	private String pushType;
	private String saasOrderId;
	private String saasContractNo;
	private String saasContractStatus;
	@JsonSerialize(using = ToStringSerializer.class)
	private Boolean pushFlg = false;
	
	public String getPushType() {
		return pushType;
	}
	public void setPushType(String pushType) {
		this.pushType = pushType;
	}
	public String getSaasContractNo() {
		return saasContractNo;
	}
	public void setSaasContractNo(String saasContractNo) {
		this.saasContractNo = saasContractNo;
	}
	public String getSaasContractStatus() {
		return saasContractStatus;
	}
	public void setSaasContractStatus(String saasContractStatus) {
		this.saasContractStatus = saasContractStatus;
	}
	public Boolean getPushFlg() {
		return pushFlg;
	}
	public void setPushFlg(Boolean pushFlg) {
		this.pushFlg = pushFlg;
	}
	public String getSaasOrderId() {
		return saasOrderId;
	}
	public void setSaasOrderId(String saasOrderId) {
		this.saasOrderId = saasOrderId;
	}
	

}
