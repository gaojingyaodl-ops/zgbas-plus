package com.spt.bas.server.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.spt.bas.client.entity.StockDetailPresell;
import com.spt.bas.client.vo.StockDetailPresellVo;
import com.spt.bas.server.service.IStockDetailPresellService;
import com.spt.tools.data.service.BaseApi;
import com.spt.tools.jpa.service.IBaseService;


@RestController
@RequestMapping(value = "/stock/detailPresell")
public class StockDetailPresellApi extends BaseApi<StockDetailPresell> {
	@Autowired
	private IStockDetailPresellService stockDetailPresellService;
	
	@Override
	public IBaseService<StockDetailPresell> getService() {
		return stockDetailPresellService;
	}
	
	@PostMapping(value="findByProductId")
	public StockDetailPresell findByProductId(@RequestBody Long productId){
		return stockDetailPresellService.findByCtrProductId(productId);
	}
	
	/*@PostMapping(value="findApplyPage")
	public Page<StockDetailPresellVo> findApplyPage(@RequestBody PageSearchVo searchVo){
		return stockDetailPresellService.findApplyPage(searchVo);
	}*/
	
	@PostMapping(value="findList")
	public List<StockDetailPresellVo> findList(@RequestBody Long contractId){
		return stockDetailPresellService.findList(contractId);
	}
}

