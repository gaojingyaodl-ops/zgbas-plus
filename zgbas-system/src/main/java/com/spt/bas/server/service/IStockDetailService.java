package com.spt.bas.server.service;

import java.util.List;

import org.springframework.data.domain.Page;

import com.spt.bas.client.entity.StockDetail;
import com.spt.bas.client.vo.BasStockDetailVo;
import com.spt.bas.client.vo.DeliveryOutChangeVo;
import com.spt.bas.client.vo.StockDetailRequest;
import com.spt.bas.client.vo.StockDetailSearchVo;
import com.spt.bas.client.vo.StockDetailVo;
import com.spt.bas.client.vo.WarehouseAndInNumberVo;
import com.spt.tools.core.bean.PageSearchVo;
import com.spt.tools.jpa.service.IBaseService;

public interface IStockDetailService extends IBaseService<StockDetail> {

	public List<StockDetail> findByBuyContractId(String buyContractId);

//	public List<StockDetail> findSellContractId(String sellContractId);

	public List<WarehouseAndInNumberVo> findWarehoseList(StockDetailSearchVo vo);

	public StockDetail findWarehouseName(String warehouseName);

	public Page<StockDetailVo> findPageVo(StockDetailSearchVo queryVo);

	StockDetail sumPageVo(StockDetailSearchVo queryVo);

	List<StockDetail> findDetailList(StockDetailRequest request, String type, boolean isOut);

//	List<StockDetail> findSellContractId(Long stockId, String sellContractId);

	/**
	 * 出库申请更改货源查询使用 符合查询货源的条件： 品名，仓库，数量=（可用+冻结）
	 */
	public Page<StockDetail> findByCondition(DeliveryOutChangeVo vo);

	public Page<BasStockDetailVo> findPageList(PageSearchVo searchVo);
}
