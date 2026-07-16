package com.spt.bas.client.entity;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.spt.tools.jpa.vo.IdEntity;
/**
 * 库存核对
 */
@Entity
@Table(name = "t_stock_check")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class StockCheck extends IdEntity{

	private static final long serialVersionUID = 7128656353943121096L;	
	private String checkResult;
	private String remark;
	private Long enterpriseId;
	public String getCheckResult() {
		return checkResult;
	}
	public void setCheckResult(String checkResult) {
		this.checkResult = checkResult;
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
	
}
