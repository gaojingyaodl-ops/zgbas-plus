package com.spt.bas.client.entity;

import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.spt.tools.jpa.vo.IdEntity;

@Entity
@Table(name = "t_stock_loss")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class StockLoss extends IdEntity{

	/**
	 * 库存损耗
	 */
	private static final long serialVersionUID = 311653102160334589L;
	
	private Long stockId;//库存id 
	private BigDecimal lossNumber;//损耗数量
	private Long  enterpriseId;//企业账套ID
	public Long getStockId() {
		return stockId;
	}
	public void setStockId(Long stockId) {
		this.stockId = stockId;
	}
	public BigDecimal getLossNumber() {
		return lossNumber;
	}
	public void setLossNumber(BigDecimal lossNumber) {
		this.lossNumber = lossNumber;
	}
	public Long getEnterpriseId() {
		return enterpriseId;
	}
	public void setEnterpriseId(Long enterpriseId) {
		this.enterpriseId = enterpriseId;
	}
	
	
	

}
