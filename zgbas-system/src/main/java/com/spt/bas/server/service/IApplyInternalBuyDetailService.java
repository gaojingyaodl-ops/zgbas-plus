package com.spt.bas.server.service;

import java.util.List;

import com.spt.bas.client.entity.ApplyInternalBuyDetail;
import com.spt.bas.client.entity.StockDetail;
import com.spt.bas.client.vo.ApplyInternalBuyVo;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.jpa.service.IBaseService;

public interface IApplyInternalBuyDetailService extends IBaseService<ApplyInternalBuyDetail> {

	public void saveDetail(Long interId, StockDetail stockDetail, ApplyInternalBuyVo interVo) throws ApplicationException;

	public List<ApplyInternalBuyDetail> findByApplyInternalBuyId(Long interId);

	public List<ApplyInternalBuyDetail> findByStockDetailId(Long stockDetailId);

	void saveNewDetail(Long interId, ApplyInternalBuyVo interVo) throws ApplicationException;
}

