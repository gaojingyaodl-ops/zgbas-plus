package com.spt.bas.server.service;

import com.spt.bas.client.entity.StockAdjust;
import com.spt.bas.client.vo.StockAdjustAuditVo;
import com.spt.bas.client.vo.StockAdjustVo;
import com.spt.tools.jpa.service.IBaseService;

public interface IStockAdjustService extends IBaseService<StockAdjust> {
	
	public void saveAdjust(StockAdjustVo vo);
	/**库存盘点调整，只能调整可用数量*/
	public void audit(StockAdjustAuditVo vo);
	
}

