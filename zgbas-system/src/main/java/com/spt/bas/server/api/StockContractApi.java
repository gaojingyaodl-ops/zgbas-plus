package com.spt.bas.server.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.spt.bas.client.entity.StockContract;
import com.spt.bas.client.vo.StockContractRelaVo;
import com.spt.bas.server.stock.service.IStockContractRelaService;
import com.spt.bas.server.stock.service.IStockContractService;
import com.spt.tools.core.bean.PageSearchVo;
import com.spt.tools.data.service.BaseApi;
import com.spt.tools.jpa.service.IBaseService;


@RestController
@RequestMapping(value = "stock/contract")
public class StockContractApi extends BaseApi<StockContract> {
	@Autowired
	private IStockContractService stockContractService;
	@Autowired
	private IStockContractRelaService stockContractRelaService;
	@Override
	public IBaseService<StockContract> getService() {
		return stockContractService;
	}
	
//	@PostMapping("findPageVo")
//	public Page<StockContractVo> findPageVo(@RequestBody StockContractSearchVo queryVo){
//		return stockContractService.findPageVo(queryVo);
//	}
	
//	@PostMapping("findPageVo")
//	public Page<StockContractVo> findPageVo(@RequestBody StockContractSearchVo queryVo){
//		
//		Page<StockContract> page = findPage(queryVo);
//		List<StockContractVo> lstVo = new ArrayList<>();
//		for (StockContract entity : page.getContent()) {
//			StockContractVo vo = new StockContractVo();
//			entity2Vo(entity, vo);
//			lstVo.add(vo);
//		}
//		PageRequest pageRequest = PageRequest.of(queryVo.getPage() - 1, queryVo.getRows());
//		Page<StockContractVo> pageVo = new PageImpl<>(lstVo, pageRequest, page.getTotalElements());
//		return pageVo;
//	}
//	
//	private void entity2Vo(StockContract entity ,StockContractVo vo){
//		try {
//			PropertyUtils.copyProperties(vo, entity);
//			Long buyContractId = entity.getBuyContractId();
//			if (buyContractId != null) {
//				CtrContract contract =	ctrContractService.getEntity(entity.getBuyContractId());
//				vo.setOurCompanyName(contract.getOurCompanyName());
//				vo.setQualityStandard(contract.getQualityStandard());
//				vo.setRemainNumber(entity.getBuyNumber().subtract(entity.getSellNumber()).subtract(entity.getSellingNumber()));
//				Long buyProductId = entity.getBuyProductId();
//				if (buyProductId != null) {
//					CtrProduct ctrProduct = ctrProductService.getEntity(buyProductId);
//					vo.setWrapSpecs(ctrProduct.getWrapSpecs());
//					vo.setWarehousePos(ctrProduct.getWarehousePos());
//					vo.setProductAttr(ctrProduct.getProductAttr());
//				}
//			}
//		} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
//			e.printStackTrace();
//		}
//	}
	
	
//	@PostMapping("findPageStockContractList")
//	public Page<StockContractVo> findPageStockContractList(@RequestBody StockContractSearchVo queryVo){
//		return stockContractService.findPageStockContractList(queryVo);
//	}
	@PostMapping("findStockContractRela")
	public Page<StockContractRelaVo> findStockContractRela(@RequestBody PageSearchVo searchVo){
		return stockContractRelaService.findStockContractRela(searchVo);
	}
}

