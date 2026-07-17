package com.spt.bas.server.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.spt.bas.client.entity.StockAdjust;
import com.spt.bas.client.vo.StockAdjustAuditVo;
import com.spt.bas.client.vo.StockAdjustVo;
import com.spt.bas.server.service.IStockAdjustService;
import com.spt.tools.data.service.BaseApi;
import com.spt.tools.jpa.service.IBaseService;


@RestController
@RequestMapping(value = "stock/adjust")
public class StockAdjustApi extends BaseApi<StockAdjust> {
	@Autowired
	private IStockAdjustService stockAdjustService;
	
	@Override
	public IBaseService<StockAdjust> getService() {
		return stockAdjustService;
	}
	
	@PostMapping(value="saveAdjust")
	public void saveAdjust(@RequestBody StockAdjustVo vo){
		stockAdjustService.saveAdjust(vo);
	}
	
	@PostMapping(value="audit")
	public void audit(@RequestBody StockAdjustAuditVo vo){
		stockAdjustService.audit(vo);
	}
	
	
}

