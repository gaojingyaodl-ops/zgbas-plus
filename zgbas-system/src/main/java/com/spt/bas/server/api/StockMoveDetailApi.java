package com.spt.bas.server.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.spt.bas.client.entity.StockMoveDetail;
import com.spt.bas.server.service.IStockMoveDetailService;
import com.spt.tools.data.service.BaseApi;
import com.spt.tools.jpa.service.IBaseService;


@RestController
@RequestMapping(value = "stock/moveDetail")
public class StockMoveDetailApi extends BaseApi<StockMoveDetail> {
	@Autowired
	private IStockMoveDetailService stockMoveDetailService;
	
	@Override
	public IBaseService<StockMoveDetail> getService() {
		return stockMoveDetailService;
	}
	
	
	
}

