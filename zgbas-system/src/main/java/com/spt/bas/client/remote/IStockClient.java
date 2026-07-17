package com.spt.bas.client.remote;//package com.spt.bas.client.remote;
//
//import java.util.List;
//
//import org.springframework.cloud.openfeign.FeignClient;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//
//import com.spt.bas.client.constant.BasConstants;
//import com.spt.bas.client.entity.Stock;
//import com.spt.bas.client.vo.ApplySellWarehouseVo;
//import com.spt.bas.client.vo.ProductDetailVo;
//import com.spt.bas.client.vo.StockSearchVo;
//import com.spt.bas.client.vo.StockVo;
//import com.spt.tools.data.service.BaseClient;
//import com.spt.tools.data.vo.PageDown;
//import com.spt.tools.http.feign.FeignConfig;
//
//
//@FeignClient(name = BasConstants.SERVER_NAME,path= BasConstants.SERVER_NAME+"/stock",url=BasConstants.SERVER_URL,configuration=FeignConfig.class)
//public interface IStockClient extends BaseClient<Stock> {
//	
//	@PostMapping("findBrandNumber")
//	public Stock findBrandNumber(@RequestBody ApplySellWarehouseVo vo);
//	
//	@PostMapping("findDealNumber")
//	public StockVo findDealNumber(@RequestBody ApplySellWarehouseVo vo);
//
//	@PostMapping("isNotExistStock")
//	public ProductDetailVo isNotExistStock(@RequestBody List<ProductDetailVo> insertList);
//	
//	@PostMapping("findPageStock")
//	public PageDown<Stock> findPageStock(@RequestBody StockSearchVo searchVo);
//	
//	@PostMapping("sumPageVo")
//	public Stock sumPageVo(@RequestBody StockSearchVo queryVo);
//}
//
