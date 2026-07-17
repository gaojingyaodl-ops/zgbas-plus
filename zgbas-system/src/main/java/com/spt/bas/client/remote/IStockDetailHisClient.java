package com.spt.bas.client.remote;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.StockDetailHis;
import com.spt.bas.client.vo.StockDetailHisVo;
import com.spt.tools.core.bean.PageSearchVo;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.data.vo.PageDown;
import com.spt.tools.http.feign.FeignConfig;


@FeignClient(name = BasConstants.SERVER_NAME,path= BasConstants.SERVER_NAME+"/stock/detailHis",url=BasConstants.SERVER_URL,configuration=FeignConfig.class)
public interface IStockDetailHisClient extends BaseClient<StockDetailHis> {
	@PostMapping("findPageVo")
	public PageDown<StockDetailHisVo> findPageVo(@RequestBody PageSearchVo searchVo);
}

