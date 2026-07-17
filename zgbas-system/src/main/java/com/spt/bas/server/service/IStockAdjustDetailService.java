package com.spt.bas.server.service;

import com.spt.bas.client.entity.StockAdjust;
import com.spt.bas.client.entity.StockAdjustDetail;
import com.spt.bas.client.vo.StockAdjustAuditVo;
import com.spt.bas.client.vo.StockAdjustVo;
import com.spt.tools.jpa.service.IBaseService;

public interface IStockAdjustDetailService extends IBaseService<StockAdjustDetail> {

	public void saveAdjustDetail(StockAdjustVo vo, StockAdjust adjust);

	public void updateStockDetailByAdjust(StockAdjustAuditVo vo);

}

