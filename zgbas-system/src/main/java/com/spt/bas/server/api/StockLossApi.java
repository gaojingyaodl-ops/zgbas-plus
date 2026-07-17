package com.spt.bas.server.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.spt.bas.client.entity.StockLoss;
import com.spt.bas.server.service.IStockLossService;
import com.spt.tools.data.service.BaseApi;
import com.spt.tools.jpa.service.IBaseService;


@RestController
@RequestMapping(value = "stock/loss")
public class StockLossApi extends BaseApi<StockLoss> {
	@Autowired
	private IStockLossService stockLossService;
	
	@Override
	public IBaseService<StockLoss> getService() {
		return stockLossService;
	}
	
	
	
}

