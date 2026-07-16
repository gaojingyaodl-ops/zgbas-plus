package com.spt.bas.client.vo;

import java.util.List;

import com.spt.bas.client.entity.StockAdjust;
import com.spt.bas.client.entity.StockAdjustDetail;

public class StockAdjustVo extends StockAdjust{
	
	private List<StockAdjustDetail> lstInsert;
	private List<StockAdjustDetail> lstUpdate;
	private List<StockAdjustDetail> lstDelete;
	public List<StockAdjustDetail> getLstInsert() {
		return lstInsert;
	}
	public void setLstInsert(List<StockAdjustDetail> lstInsert) {
		this.lstInsert = lstInsert;
	}
	public List<StockAdjustDetail> getLstUpdate() {
		return lstUpdate;
	}
	public void setLstUpdate(List<StockAdjustDetail> lstUpdate) {
		this.lstUpdate = lstUpdate;
	}
	public List<StockAdjustDetail> getLstDelete() {
		return lstDelete;
	}
	public void setLstDelete(List<StockAdjustDetail> lstDelete) {
		this.lstDelete = lstDelete;
	}
	
	public void setBatchSub(List<StockAdjustDetail> lstInsert, List<StockAdjustDetail> lstUpdate, List<StockAdjustDetail> lstDelete) {
		setLstInsert((List<StockAdjustDetail>)lstInsert);
		setLstUpdate((List<StockAdjustDetail>)lstUpdate);
		setLstDelete((List<StockAdjustDetail>)lstDelete);
	}

}
