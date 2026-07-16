package com.spt.bas.client.vo;

import java.math.BigDecimal;
import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;

public class StockDetailMoveVo {
	
//	private Long entityId;
//	
//	private String warehouseName;
//	
//	private BigDecimal moveAvailableNumber;
//	
//	private BigDecimal moveFrozenNumber;
	
	private Long curUserId;				//业务员ID
	
	private String curUserName;			//业务员
	
	private Long targetWarehouseId;		//移至仓库ID
	
	private String warehouseName;
	
	private Long originalDetailId;		//原库存明细
	
	private BigDecimal moveRealNumber = BigDecimal.ZERO;  //移动可用数量
	
	private BigDecimal moveFrozenNumber = BigDecimal.ZERO;//移动冻结数量
	
	private String warehouseAddrs;		//一致目标仓库地址
	
	private String spotType;			//现货货权
	
	private String moveType;			//移库方式
	
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
	private Date moveDate;				//移库日期
	
	private String remark;				//移库备注
	
	private String applyNo;				//提单号

	public Long getCurUserId() {
		return curUserId;
	}

	public void setCurUserId(Long curUserId) {
		this.curUserId = curUserId;
	}

	public String getCurUserName() {
		return curUserName;
	}

	public void setCurUserName(String curUserName) {
		this.curUserName = curUserName;
	}

	public Long getTargetWarehouseId() {
		return targetWarehouseId;
	}

	public void setTargetWarehouseId(Long targetWarehouseId) {
		this.targetWarehouseId = targetWarehouseId;
	}

	public BigDecimal getMoveRealNumber() {
		return moveRealNumber;
	}

	public void setMoveRealNumber(BigDecimal moveRealNumber) {
		this.moveRealNumber = moveRealNumber;
	}

	public Long getOriginalDetailId() {
		return originalDetailId;
	}

	public void setOriginalDetailId(Long originalDetailId) {
		this.originalDetailId = originalDetailId;
	}

	public BigDecimal getMoveFrozenNumber() {
		return moveFrozenNumber;
	}

	public void setMoveFrozenNumber(BigDecimal moveFrozenNumber) {
		this.moveFrozenNumber = moveFrozenNumber;
	}

	public String getWarehouseName() {
		return warehouseName;
	}

	public void setWarehouseName(String warehouseName) {
		this.warehouseName = warehouseName;
	}

	public String getWarehouseAddrs() {
		return warehouseAddrs;
	}

	public void setWarehouseAddrs(String warehouseAddrs) {
		this.warehouseAddrs = warehouseAddrs;
	}

	public String getSpotType() {
		return spotType;
	}

	public void setSpotType(String spotType) {
		this.spotType = spotType;
	}

	public String getMoveType() {
		return moveType;
	}

	public void setMoveType(String moveType) {
		this.moveType = moveType;
	}

	public Date getMoveDate() {
		return moveDate;
	}

	public void setMoveDate(Date moveDate) {
		this.moveDate = moveDate;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getApplyNo() {
		return applyNo;
	}

	public void setApplyNo(String applyNo) {
		this.applyNo = applyNo;
	}
	
	

}
