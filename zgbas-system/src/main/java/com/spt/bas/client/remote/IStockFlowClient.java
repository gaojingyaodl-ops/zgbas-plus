package com.spt.bas.client.remote;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.StockFlow;
import com.spt.bas.client.vo.StockFlowVo;
import com.spt.tools.core.bean.PageSearchVo;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.data.vo.PageDown;
import com.spt.tools.http.feign.FeignConfig;


@FeignClient(name = BasConstants.SERVER_NAME,path= BasConstants.SERVER_NAME+"/bs/stockFlow",url=BasConstants.SERVER_URL,configuration=FeignConfig.class)
public interface IStockFlowClient extends BaseClient<StockFlow> {
	
	@PostMapping("findPageVo")
	public PageDown<StockFlowVo> findPageVo(@RequestBody PageSearchVo queryVo);
}

