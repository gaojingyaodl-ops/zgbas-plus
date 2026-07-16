package com.spt.bas.client.entity;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.spt.tools.jpa.vo.IdEntity;
/**
 * 仓库地址表
 * @author yangjie
 *
 */
@Entity
@Table(name = "t_bs_warehouse_addr")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class BsWarehouseAddr extends IdEntity{
	/**
	 * 
	 */
	private static final long serialVersionUID = 3980531605954526561L;
	
	private Long warehouseId;//仓库ID
	
	private String warehouseShortName;//仓库地址简称
	
	private String warehouseAddr;//地址
	
	private Date createdDate;//创建时间
	
	private Date updatedDate;//更新时间
	
	@JsonSerialize(using = ToStringSerializer.class)
	private Boolean defaultFlg;//是否默认

	private BigDecimal warehouseUnitCost;// 仓储费单价

	private String provinceCode;

	private String cityCode;

	private String areaCode;
	
	public String getWarehouseShortName() {
		return warehouseShortName;
	}

	public void setWarehouseShortName(String warehouseShortName) {
		this.warehouseShortName = warehouseShortName;
	}

	public String getWarehouseAddr() {
		return warehouseAddr;
	}

	public void setWarehouseAddr(String warehouseAddr) {
		this.warehouseAddr = warehouseAddr;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public Date getUpdatedDate() {
		return updatedDate;
	}

	public void setUpdatedDate(Date updatedDate) {
		this.updatedDate = updatedDate;
	}

	public Long getWarehouseId() {
		return warehouseId;
	}

	public void setWarehouseId(Long warehouseId) {
		this.warehouseId = warehouseId;
	}

	public Boolean getDefaultFlg() {
		return defaultFlg;
	}

	public void setDefaultFlg(Boolean defaultFlg) {
		this.defaultFlg = defaultFlg;
	}

	public BigDecimal getWarehouseUnitCost() {
		return warehouseUnitCost;
	}

	public void setWarehouseUnitCost(BigDecimal warehouseUnitCost) {
		this.warehouseUnitCost = warehouseUnitCost;
	}

	public String getProvinceCode() {
		return provinceCode;
	}

	public void setProvinceCode(String provinceCode) {
		this.provinceCode = provinceCode;
	}

	public String getCityCode() {
		return cityCode;
	}

	public void setCityCode(String cityCode) {
		this.cityCode = cityCode;
	}

	public String getAreaCode() {
		return areaCode;
	}

	public void setAreaCode(String areaCode) {
		this.areaCode = areaCode;
	}
}
