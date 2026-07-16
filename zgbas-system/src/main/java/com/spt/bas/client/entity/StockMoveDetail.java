package com.spt.bas.client.entity;

import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.spt.tools.jpa.vo.IdEntity;
/**
 * 移库明细
 */

@Entity
@Table(name = "t_stock_move_detail")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class StockMoveDetail extends IdEntity {

	private static final long serialVersionUID = -6873080110648491240L;
	private Long originalDetailId;			//原库存明细id
	private Long originalWarehouseId;		//原仓库
	private String originalWarehouseName;
	private BigDecimal moveRealNumber;		//移动可用数量
	private BigDecimal moveFrozenNumber;	//移动冻结数量
	private Long targetWarehouseId;			//移动至目标仓库
	private String targetWarehouseName;
	private Long targetDetailId;			//目标库存明细id
	private Long matchUserId;
	private String matchUserName;
	private Long enterpriseId;
	public Long getOriginalDetailId() {
		return originalDetailId;
	}
	public void setOriginalDetailId(Long originalDetailId) {
		this.originalDetailId = originalDetailId;
	}
	public Long getOriginalWarehouseId() {
		return originalWarehouseId;
	}
	public void setOriginalWarehouseId(Long originalWarehouseId) {
		this.originalWarehouseId = originalWarehouseId;
	}
	public String getOriginalWarehouseName() {
		return originalWarehouseName;
	}
	public void setOriginalWarehouseName(String originalWarehouseName) {
		this.originalWarehouseName = originalWarehouseName;
	}
	public BigDecimal getMoveRealNumber() {
		return moveRealNumber;
	}
	public void setMoveRealNumber(BigDecimal moveRealNumber) {
		this.moveRealNumber = moveRealNumber;
	}
	public BigDecimal getMoveFrozenNumber() {
		return moveFrozenNumber;
	}
	public void setMoveFrozenNumber(BigDecimal moveFrozenNumber) {
		this.moveFrozenNumber = moveFrozenNumber;
	}
	public Long getTargetWarehouseId() {
		return targetWarehouseId;
	}
	public void setTargetWarehouseId(Long targetWarehouseId) {
		this.targetWarehouseId = targetWarehouseId;
	}
	public String getTargetWarehouseName() {
		return targetWarehouseName;
	}
	public void setTargetWarehouseName(String targetWarehouseName) {
		this.targetWarehouseName = targetWarehouseName;
	}
	public Long getTargetDetailId() {
		return targetDetailId;
	}
	public void setTargetDetailId(Long targetDetailId) {
		this.targetDetailId = targetDetailId;
	}
	public Long getMatchUserId() {
		return matchUserId;
	}
	public void setMatchUserId(Long matchUserId) {
		this.matchUserId = matchUserId;
	}
	public String getMatchUserName() {
		return matchUserName;
	}
	public void setMatchUserName(String matchUserName) {
		this.matchUserName = matchUserName;
	}
	public Long getEnterpriseId() {
		return enterpriseId;
	}
	public void setEnterpriseId(Long enterpriseId) {
		this.enterpriseId = enterpriseId;
	}
	

}
