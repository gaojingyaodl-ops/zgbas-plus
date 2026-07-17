package com.spt.bas.server.api;//package com.spt.bas.server.api;
//
//import java.util.List;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.domain.Page;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import com.spt.bas.client.constant.BasConstants;
//import com.spt.bas.client.entity.Stock;
//import com.spt.bas.client.vo.ApplySellWarehouseVo;
//import com.spt.bas.client.vo.ProductDetailVo;
//import com.spt.bas.client.vo.StockSearchVo;
//import com.spt.bas.client.vo.StockVo;
//import com.spt.bas.server.service.IStockService;
//import com.spt.tools.data.service.BaseApi;
//import com.spt.tools.jpa.service.IBaseService;
//
//
//@RestController
//@RequestMapping(value = "stock")
//public class StockApi extends BaseApi<Stock> {
//	@Autowired
//	private IStockService stockService;
//
//	@Override
//	public IBaseService<Stock> getService() {
//		return stockService;
//	}
//
//	@PostMapping("findBrandNumber")
//	public Stock findBrandNumber(@RequestBody ApplySellWarehouseVo vo){
//		String productAttr = BasConstants.STOCK_PRODUCT_ATTR_N;
//		String brandNumber = vo.getBrandNumber();
//		Long enterpriseId = vo.getEnterpriseId();
//		return stockService.findBrandNumber(brandNumber,productAttr,enterpriseId);
//	}
//
//
//	@PostMapping("findDealNumber")
//	public StockVo findDealNumber(@RequestBody ApplySellWarehouseVo vo){
//		return stockService.findDealNumber(vo);
//	}
//
//	@PostMapping("isNotExistStock")
//	public ProductDetailVo isNotExistStock(@RequestBody List<ProductDetailVo> insertList){
//		ProductDetailVo returnVo = null;
//		for(ProductDetailVo vo :insertList){
//			List<Stock> list = stockService.findStockForzenNumber(vo.getProductCd(), vo.getBrandNumber(),vo.getFactoryId(),vo.getWarehouseName(),vo.getEnterpriseId(),vo.getCurNumber(),vo.getProductAttr());
//			if(list==null||list.size()<=0){
//				returnVo = vo;
//				break;
//			}
//		}
//		return returnVo;
//	}
//	@PostMapping("findPageStock")
//	public Page<Stock> findPageStock(@RequestBody StockSearchVo searchVo){
//		return stockService.findPageStock(searchVo);
//	}
//
//	@PostMapping("sumPageVo")
//	public Stock sumPageVo(@RequestBody StockSearchVo queryVo){
//		return stockService.sumPageVo(queryVo);
//	}
//}
//
