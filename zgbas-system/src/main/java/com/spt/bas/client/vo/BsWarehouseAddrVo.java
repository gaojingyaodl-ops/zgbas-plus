package com.spt.bas.client.vo;

import java.util.List;

import com.spt.bas.client.entity.BsWarehouse;
import com.spt.bas.client.entity.BsWarehouseAddr;

public class BsWarehouseAddrVo extends BsWarehouse{
	/**
	 * 
	 */
	private static final long serialVersionUID = 6852670938496854447L;
	
    private String warehouseShortName;//仓库地址简称
	
	private String warehouseAddr;//地址
	
	private Boolean defaultFlg;//是否默认
	
	private List<BsWarehouseAddr> lstInsert;
	private List<BsWarehouseAddr> lstUpdate;
	private List<BsWarehouseAddr> lstDelete;
	
	public List<BsWarehouseAddr> getLstInsert() {
		return lstInsert;
	}
	public void setLstInsert(List<BsWarehouseAddr> lstInsert) {
		this.lstInsert = lstInsert;
	}
	public List<BsWarehouseAddr> getLstUpdate() {
		return lstUpdate;
	}
	public void setLstUpdate(List<BsWarehouseAddr> lstUpdate) {
		this.lstUpdate = lstUpdate;
	}
	public List<BsWarehouseAddr> getLstDelete() {
		return lstDelete;
	}
	public void setLstDelete(List<BsWarehouseAddr> lstDelete) {
		this.lstDelete = lstDelete;
	}
	
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
	
	public Boolean getDefaultFlg() {
		return defaultFlg;
	}
	public void setDefaultFlg(Boolean defaultFlg) {
		this.defaultFlg = defaultFlg;
	}
	
	public void setBatchSub(List<BsWarehouseAddr> lstInsert, List<BsWarehouseAddr> lstUpdate, List<BsWarehouseAddr> lstDelete) {
		setLstInsert((List<BsWarehouseAddr>)lstInsert);
		setLstUpdate((List<BsWarehouseAddr>)lstUpdate);
		setLstDelete((List<BsWarehouseAddr>)lstDelete);
	}
	
	
	
	

}
