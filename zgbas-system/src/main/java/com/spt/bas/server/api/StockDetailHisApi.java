package com.spt.bas.server.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.spt.bas.client.entity.StockDetailHis;
import com.spt.bas.client.vo.StockDetailHisVo;
import com.spt.bas.server.service.IStockDetailHisService;
import com.spt.tools.core.bean.PageSearchVo;
import com.spt.tools.data.service.BaseApi;
import com.spt.tools.jpa.service.IBaseService;


@RestController
@RequestMapping(value = "/stock/detailHis")
public class StockDetailHisApi extends BaseApi<StockDetailHis> {
	@Autowired
	private IStockDetailHisService stockDetailHisService;
	
	@Override
	public IBaseService<StockDetailHis> getService() {
		return stockDetailHisService;
	}
	
	@PostMapping("findPageVo")
	public Page<StockDetailHisVo> findPageVo(@RequestBody PageSearchVo searchVo){
		return stockDetailHisService.findPageVo(searchVo);
	}
	
}

