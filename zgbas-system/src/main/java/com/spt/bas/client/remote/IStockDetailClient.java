package com.spt.bas.client.remote;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.StockDetail;
import com.spt.bas.client.vo.BasStockDetailVo;
import com.spt.bas.client.vo.DeliveryOutChangeVo;
import com.spt.bas.client.vo.StockDetailMoveVo;
import com.spt.bas.client.vo.StockDetailSearchVo;
import com.spt.bas.client.vo.StockDetailVo;
import com.spt.bas.client.vo.WarehouseAndInNumberVo;
import com.spt.tools.core.bean.PageSearchVo;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.data.vo.PageDown;
import com.spt.tools.http.feign.FeignConfig;

@FeignClient(name = BasConstants.SERVER_NAME, path = BasConstants.SERVER_NAME
		+ "/stock/detail", url = BasConstants.SERVER_URL, configuration = FeignConfig.class)
public interface IStockDetailClient extends BaseClient<StockDetail> {
	@PostMapping("findWarehoseList")
	public List<WarehouseAndInNumberVo> findWarehoseList(@RequestBody StockDetailSearchVo vo);
	
	@PostMapping("findWarehouseName")
	public StockDetail findWarehouseName(@RequestBody String warehouseName);
	
	@PostMapping("findPageVo")
	public PageDown<StockDetailVo> findPageVo(@RequestBody StockDetailSearchVo queryVo);
	
	@PostMapping("changeWarehouse")
	public void changeWarehouse(@RequestBody StockDetailMoveVo changeVo);
	
	@PostMapping("findByContractId")
	public StockDetail findByContractId(@RequestBody String contractId);
	
	@PostMapping("sumPageVo")
	public StockDetail sumPageVo(@RequestBody StockDetailSearchVo queryVo);
	
	@PostMapping("findByCondition")
	public PageDown<StockDetail> findByCondition(@RequestBody DeliveryOutChangeVo vo);
	
	@PostMapping("findPageList")
	public PageDown<BasStockDetailVo> findPageList(@RequestBody PageSearchVo searchVo);
}
