package com.spt.bas.client.remote;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.StockAdjust;
import com.spt.bas.client.vo.StockAdjustAuditVo;
import com.spt.bas.client.vo.StockAdjustVo;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.http.feign.FeignConfig;


@FeignClient(name = BasConstants.SERVER_NAME,path= BasConstants.SERVER_NAME+"/stock/adjust",url=BasConstants.SERVER_URL,configuration=FeignConfig.class)
public interface IStockAdjustClient extends BaseClient<StockAdjust> {
	
	@PostMapping(value="saveAdjust")
	public void saveAdjust(@RequestBody StockAdjustVo vo);
	
	@PostMapping(value="audit")
	public void audit(@RequestBody StockAdjustAuditVo vo);
	
}

