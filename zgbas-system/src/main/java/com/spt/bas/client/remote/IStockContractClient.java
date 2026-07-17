package com.spt.bas.client.remote;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.StockContract;
import com.spt.bas.client.vo.StockContractRelaVo;
import com.spt.tools.core.bean.PageSearchVo;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.data.vo.PageDown;
import com.spt.tools.http.feign.FeignConfig;

@FeignClient(name = BasConstants.SERVER_NAME, path = BasConstants.SERVER_NAME
		+ "/stock/contract", url = BasConstants.SERVER_URL, configuration = FeignConfig.class)
public interface IStockContractClient extends BaseClient<StockContract> {

//	@PostMapping("findPageStockContractList")
//	public PageDown<StockContractVo> findPageStockContractList(@RequestBody StockContractSearchVo queryVo);
	
//	@PostMapping("findPageVo")
//	public PageDown<StockContractVo> findPageVo(@RequestBody StockContractSearchVo queryVo);
	
	@PostMapping("findStockContractRela")
	public PageDown<StockContractRelaVo> findStockContractRela(@RequestBody PageSearchVo searchVo);
}
