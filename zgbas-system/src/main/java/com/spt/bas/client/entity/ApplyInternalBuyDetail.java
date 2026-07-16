package com.spt.bas.client.entity;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.spt.tools.jpa.vo.IdEntity;

/**
 * 内部交易明细
 */
@Entity
@Table(name = "t_apply_internal_buy_detail")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class ApplyInternalBuyDetail extends IdEntity {

	private static final long serialVersionUID = 6469341417077614673L;
	private Long applyInternalBuyId;
	private Long shipperMatchUserId;//货主业务员
	private String shipperMatchUserName;
	private Long stockDetailId;//原库存明细
	private String detailType;
	private String remark;
	private Long enterpriseId;
	
	public Long getApplyInternalBuyId() {
		return applyInternalBuyId;
	}
	public void setApplyInternalBuyId(Long applyInternalBuyId) {
		this.applyInternalBuyId = applyInternalBuyId;
	}
	public Long getShipperMatchUserId() {
		return shipperMatchUserId;
	}
	public void setShipperMatchUserId(Long shipperMatchUserId) {
		this.shipperMatchUserId = shipperMatchUserId;
	}
	public String getShipperMatchUserName() {
		return shipperMatchUserName;
	}
	public void setShipperMatchUserName(String shipperMatchUserName) {
		this.shipperMatchUserName = shipperMatchUserName;
	}
	public Long getStockDetailId() {
		return stockDetailId;
	}
	public void setStockDetailId(Long stockDetailId) {
		this.stockDetailId = stockDetailId;
	}
	
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	
	public String getDetailType() {
		return detailType;
	}
	public void setDetailType(String detailType) {
		this.detailType = detailType;
	}
	public Long getEnterpriseId() {
		return enterpriseId;
	}
	public void setEnterpriseId(Long enterpriseId) {
		this.enterpriseId = enterpriseId;
	}
	

}
