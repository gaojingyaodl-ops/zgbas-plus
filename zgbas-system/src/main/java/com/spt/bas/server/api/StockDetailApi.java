package com.spt.bas.server.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.spt.bas.client.entity.StockContractRela;
import com.spt.bas.client.entity.StockDetail;
import com.spt.bas.client.vo.BasStockDetailVo;
import com.spt.bas.client.vo.DeliveryOutChangeVo;
import com.spt.bas.client.vo.StockDetailMoveVo;
import com.spt.bas.client.vo.StockDetailSearchVo;
import com.spt.bas.client.vo.StockDetailVo;
import com.spt.bas.client.vo.WarehouseAndInNumberVo;
import com.spt.bas.server.dao.StockDetailDao;
import com.spt.bas.server.service.IStockDetailService;
import com.spt.bas.server.stock.service.IStockContractRelaService;
import com.spt.bas.server.stock.service.StockDetailFacade;
import com.spt.tools.core.bean.PageSearchVo;
import com.spt.tools.data.service.BaseApi;
import com.spt.tools.jpa.service.IBaseService;


@RestController
@RequestMapping(value = "stock/detail")
public class StockDetailApi extends BaseApi<StockDetail> {
	@Autowired
	private StockDetailFacade stockDetailFacade;
	@Autowired
	private IStockContractRelaService stockContractRelaService;
	@Autowired
	private StockDetailDao stockDetailDao;
	@Autowired
	private IStockDetailService stockDetailService;
	
	@Override
	public IBaseService<StockDetail> getService() {
		return stockDetailService;
	}
	@PostMapping("findWarehoseList")
	public List<WarehouseAndInNumberVo> findList(@RequestBody StockDetailSearchVo vo){
		//vo.setProductAttr(BasConstants.STOCK_PRODUCT_ATTR_N);
		return stockDetailService.findWarehoseList(vo);
	}
	
	
	@PostMapping("findWarehouseName")
	public StockDetail findWarehouseName(@RequestBody String warehouseName){
		return stockDetailService.findWarehouseName(warehouseName);
	}
	
	@PostMapping("findPageVo")
	public Page<StockDetailVo> findPageVo(@RequestBody StockDetailSearchVo queryVo){
		return stockDetailService.findPageVo(queryVo);
	}
	
	@PostMapping("changeWarehouse")
	public void changeWarehouse(@RequestBody StockDetailMoveVo changeVo){
		stockDetailFacade.changeWarehouse(changeVo);
	}
	/**根据销售合同id，查询对接库存明细记录*/ 
	@PostMapping("findByContractId")
	public StockDetail findByContractId(@RequestBody String contractId) {
		List<StockContractRela> lstRela = stockContractRelaService.findByContractId(Long.valueOf(contractId), StockContractRela.RELATYPE_SELL);
		if (lstRela.size() > 0) {
			StockContractRela rela = lstRela.get(0);
			List<StockDetail> lstDetail = stockDetailDao.findByStockContractId(rela.getStockContractId());
			if (lstDetail.size() > 0) {
				return lstDetail.get(0);
			}
		}
		return null;
	}

	@PostMapping("sumPageVo")
	public StockDetail sumPageVo(@RequestBody StockDetailSearchVo queryVo){
		return stockDetailService.sumPageVo(queryVo);
	}
	
	@PostMapping("findByCondition")
	public Page<StockDetail> findByCondition(@RequestBody DeliveryOutChangeVo vo){
		return stockDetailService.findByCondition(vo);
	}
	
	@PostMapping("findPageList")
	public Page<BasStockDetailVo> findPageList(@RequestBody PageSearchVo searchVo){
		return stockDetailService.findPageList(searchVo);
	}
}

