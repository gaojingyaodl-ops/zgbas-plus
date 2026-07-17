package com.spt.bas.server.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.spt.bas.client.entity.StockAdjustDetail;
import com.spt.bas.server.service.IStockAdjustDetailService;
import com.spt.tools.data.service.BaseApi;
import com.spt.tools.jpa.service.IBaseService;


@RestController
@RequestMapping(value = "stock/adjustDetail")
public class StockAdjustDetailApi extends BaseApi<StockAdjustDetail> {
	@Autowired
	private IStockAdjustDetailService stockAdjustDetailService;
	
	@Override
	public IBaseService<StockAdjustDetail> getService() {
		return stockAdjustDetailService;
	}
	
	
	
}

