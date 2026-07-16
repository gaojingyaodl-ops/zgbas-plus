package com.spt.bas.client.entity;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.spt.tools.jpa.vo.IdEntity;

@Entity
@Table(name = "t_push_contract")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class PushContract extends IdEntity{
	/**
	 * 
	 */
	private static final long serialVersionUID = 3709869173139044097L;
	private String pushContractNo;			//推送合同编号
	private String pushType;				//推送类型
	private String pushUrl;					//推送路径
	private String pushData;				//推送数据
	@JsonSerialize(using = ToStringSerializer.class)
	private Boolean pushFlg = false;		//推送状态
	private String remark;					//备注
	private String targetCode;				//推送目标项目标识
	private Long enterpriseId;				//企业账套ID
	public String getPushType() {
		return pushType;
	}
	public void setPushType(String pushType) {
		this.pushType = pushType;
	}
	public String getPushUrl() {
		return pushUrl;
	}
	public void setPushUrl(String pushUrl) {
		this.pushUrl = pushUrl;
	}
	public Boolean getPushFlg() {
		return pushFlg;
	}
	public void setPushFlg(Boolean pushFlg) {
		this.pushFlg = pushFlg;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public String getTargetCode() {
		return targetCode;
	}
	public void setTargetCode(String targetCode) {
		this.targetCode = targetCode;
	}
	public String getPushContractNo() {
		return pushContractNo;
	}
	public void setPushContractNo(String pushContractNo) {
		this.pushContractNo = pushContractNo;
	}
	public String getPushData() {
		return pushData;
	}
	public void setPushData(String pushData) {
		this.pushData = pushData;
	}
	public Long getEnterpriseId() {
		return enterpriseId;
	}
	public void setEnterpriseId(Long enterpriseId) {
		this.enterpriseId = enterpriseId;
	}
	
}
