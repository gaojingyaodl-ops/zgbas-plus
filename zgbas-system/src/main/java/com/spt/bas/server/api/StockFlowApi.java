package com.spt.bas.server.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.spt.bas.client.entity.StockFlow;
import com.spt.bas.client.vo.StockFlowVo;
import com.spt.bas.server.service.IStockFlowService;
import com.spt.tools.core.bean.PageSearchVo;
import com.spt.tools.data.service.BaseApi;
import com.spt.tools.jpa.service.IBaseService;


@RestController
@RequestMapping(value = "bs/stockFlow")
public class StockFlowApi extends BaseApi<StockFlow> {
	@Autowired
	private IStockFlowService stockFlowService;
	
	@Override
	public IBaseService<StockFlow> getService() {
		return stockFlowService;
	}
	
	@PostMapping("findPageVo")
	public Page<StockFlowVo> findPageVo(@RequestBody PageSearchVo queryVo){
		return stockFlowService.findPageVo(queryVo);
	}
	
}

