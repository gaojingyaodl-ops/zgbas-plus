package com.spt.bas.client.remote;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.StockDetailPresell;
import com.spt.bas.client.vo.StockDetailPresellVo;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.http.feign.FeignConfig;


@FeignClient(name = BasConstants.SERVER_NAME,path= BasConstants.SERVER_NAME+"/stock/detailPresell",url=BasConstants.SERVER_URL,configuration=FeignConfig.class)
public interface IStockDetailPresellClient extends BaseClient<StockDetailPresell> {
	@PostMapping(value="findByProductId")
	public StockDetailPresell findByProductId(@RequestBody Long productId);

	/*@PostMapping(value="findApplyPage")
	public PageDown<StockDetailPresellVo> findApplyPage(@RequestBody PageSearchVo searchVo);*/
	
	@PostMapping(value="findList")
	public List<StockDetailPresellVo> findList(@RequestBody Long contractId);
}

