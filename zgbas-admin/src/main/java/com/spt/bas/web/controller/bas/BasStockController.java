//package com.spt.bas.web.controller.bas;
//
//import java.io.IOException;
//import java.math.BigDecimal;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//
//import org.apache.poi.ss.usermodel.CellStyle;
//import org.apache.poi.ss.usermodel.Sheet;
//import org.apache.poi.ss.usermodel.Workbook;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.domain.Page;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestMethod;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.ResponseBody;
//
//import com.alibaba.fastjson.JSON;
//import com.google.common.collect.Maps;
//import com.spt.auth.sdk.cache.DictUtil;
//import com.spt.bas.client.cache.BsDictUtil;
//import com.spt.bas.client.constant.BasConstants;
//import com.spt.bas.client.entity.BsFactory;
//import com.spt.bas.client.entity.BsProductType;
//import com.spt.bas.client.entity.BsWarehouse;
//import com.spt.bas.client.entity.Stock;
//import com.spt.bas.client.remote.IBsFactoryClient;
//import com.spt.bas.client.remote.IBsProductTypeClient;
//import com.spt.bas.client.remote.IBsWarehouseClient;
//import com.spt.bas.client.remote.IStockClient;
//import com.spt.bas.client.remote.IStockFlowClient;
//import com.spt.bas.client.vo.ApplySellWarehouseVo;
//import com.spt.bas.client.vo.StockFlowVo;
//import com.spt.bas.client.vo.StockSearchVo;
//import com.spt.bas.report.client.entity.RptStockReport;
//import com.spt.bas.report.client.remote.IRptStockInventoryClient;
//import com.spt.bas.web.shiro.ShiroUtil;
//import com.spt.tools.core.bean.PageSearchVo;
//import com.spt.tools.core.date.DateOperator;
//import com.spt.tools.core.exception.ApplicationException;
//import com.spt.tools.core.json.JsonUtil;
//import com.spt.tools.data.easyui.EasyTreeNode;
//import com.spt.tools.data.service.BaseClient;
//import com.spt.tools.data.vo.BaseVo;
//import com.spt.tools.data.vo.PageDown;
//import com.spt.tools.file.poi.PoiExcelUtil;
//import com.spt.tools.web.controller.PageController;
//import com.spt.tools.web.util.JsonEasyUI;
//import com.spt.tools.web.util.RenderUtil;
//
///**
// * 库存管理
// * @author wanjie
// *
// */
//@Controller
//@RequestMapping("/bas/stock")
//public class BasStockController extends PageController<Stock, BaseVo>{
//
////	@Autowired
////	private IStockClient stockClient; 
//	@Autowired
//	private IBsProductTypeClient bsProductTypeClient;
//	@Autowired
//	private IBsFactoryClient bsFactoryClient;
//	@Autowired
//	private IBsWarehouseClient bsWarehouseClient;
//	@Autowired
//	private IStockFlowClient stockFlowClient;
//	@Autowired
//	private IRptStockInventoryClient stockInventoryClient;
//	@Override
//	public BaseClient<Stock> getService() {
//		return stockClient;
//	}
//	
//	/**
//	 * 库存列表
//	 * @param model
//	 * @return
//	 */
//	@RequestMapping(value="")
//	public String findStock(Model model){
//		//获取品名列表
//		List<EasyTreeNode> productTree = bsProductTypeClient.findAllProductTree(ShiroUtil.getEnterpriseId());
//		List<BsFactory> factory=bsFactoryClient.findByEnterpriseId(ShiroUtil.getEnterpriseId());
//		model.addAttribute("productType", JsonUtil.obj2Json(productTree));
//		model.addAttribute("factory", JsonUtil.obj2Json(factory));
//		//产品类型
//		model.addAttribute("productTypeJson",
//			JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_BUYPRODUCT)));
//		model.addAttribute("ourCompanyJson", JsonUtil.obj2Json(
//				BsCompanyOurUtil.getCompanyOurToBsDictDataList()));
//		//仓库
//		List<BsWarehouse> warehouse=bsWarehouseClient.findAll();
//		model.addAttribute("warehouse", JsonUtil.obj2Json(warehouse));
//		return "bas/stock";
//	}
//	/**
//	 * 获取库存里应有的产品数量
//	 * @param searchVo:货品Cd、牌号、厂商
//	 * @param response
//	 */
//	@RequestMapping(value = "findTotalNumber")
//	public void findTotalNumber(PageSearchVo searchVo, HttpServletRequest request,HttpServletResponse response){
//		searchVo.setSort("updatedDate");
//		searchVo.setOrder("DESC");
//		Page page = this.findPage(searchVo, request, response);
//		List<Stock> list = page.getContent();
//		BigDecimal number = BigDecimal.ZERO;
//		for(Stock stock:list){
//			number = number.add(stock.getRealNumber());
//		}
//		RenderUtil.renderText(number+"", response);
//	}
//	
//	@Override
//	public Map<String, Object> getDefaultFilter() {
//		Map<String, Object> map = Maps.newHashMap();
//		map.put("EQL_enterpriseId", ShiroUtil.getEnterpriseId());
//		return map;
//	}
//	
//	/**
//	 * 删除
//	 * @param id
//	 * @param response
//	 */
//	@RequestMapping(value="delete/{id}",method = RequestMethod.GET)
//	public void delete(@PathVariable("id") Long id,HttpServletResponse response){
//		try {
//			getService().delete(id);
//		} catch (Exception e) {
//			logger.info(e.getMessage(), e);
//			RenderUtil.renderFailure("failure", response);	
//		}	
//		RenderUtil.renderSuccess("success", response);
//		
//	}		
//	@Override
//	protected Map<String, Object> entity2Footer(Stock e) {
//		Map<String, Object> footer =new HashMap<>();
//		if (e!=null) {
//			footer.put("warehouseName", "合计");
//			footer.put("totalNumber", e.getTotalNumber());
//			footer.put("frozenNumber", e.getFrozenNumber());
//			footer.put("realNumber", e.getRealNumber());
//		}
//		
//		return footer;
//	}
//	/**
//	 * 保存
//	 * @param brand
//	 * @param request
//	 * @param response
//	 */
//	@RequestMapping(value = "save", method = RequestMethod.POST)
//	public void save(@RequestParam("param") String param,HttpServletRequest request, HttpServletResponse response) {
//		List<Stock> entity=JSON.parseArray(param,Stock.class);
//		if(entity.size()==0){
//			return ;
//		}
//		try {
//			for(Stock list:entity){	
//				String brandNumber=list.getBrandNumber();
//				ApplySellWarehouseVo vo = new ApplySellWarehouseVo();
//				vo.setBrandNumber(brandNumber);
//				vo.setEnterpriseId(ShiroUtil.getEnterpriseId());
//				Stock brand=stockClient.findBrandNumber(vo);
//				BsProductType product=bsProductTypeClient.findProductTypeCode(list.getProductCd());
//				BsFactory factory=bsFactoryClient.getEntity(list.getFactoryId());
//				//一个牌号只属于一个品类
//				if(brand==null){
//					list.setProductName(product.getTypeName());
//					list.setFactoryName(factory.getFactoryName());
//					stockClient.save(list);					
//					RenderUtil.renderSuccess("success", response);
//				}else{
//					RenderUtil.renderFailure("failure", response);
//				}
//			}
//		} catch (Exception e) {
//		}			
//	}
//	
//	@RequestMapping(value = "stockFlow/{id}", method = RequestMethod.GET)
//	public String stockFlow(@PathVariable("id") Long id,Model model){
//		if(id!=null&&id>0l){
//			model.addAttribute("stockId", id);
//			//库存类型
//			model.addAttribute("operationTypeJson",
//				JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_OPERATIONTYPE)));
//			//查看合同权限
//			boolean canViewContract = false;
//			if(ShiroUtil.isPermitted(BasConstants.PERM_CTR_VIEWALL)){
//				canViewContract = true;
//			}
//			model.addAttribute("canViewContract", canViewContract);
//		}
//		return "bas/stockFlow";
//	}
//	
//	@RequestMapping(value = "listStockFlow/{id}", method = RequestMethod.POST)
//	public void listStockFlow(@PathVariable("id") Long id, HttpServletResponse response){
//		if(id!=null&&id>0l){
//			PageSearchVo searchVo = new PageSearchVo();
//			searchVo.setRows(50);
//			Map<String, Object> searchParams = new HashMap<String, Object>();
//			searchParams.put("EQL_stockId",id);
//			searchVo.setSearchParams(searchParams);
//			PageDown<StockFlowVo> page = stockFlowClient.findPageVo(searchVo);
//			/*PageDown<StockDetailHis> page = stockDetailHisClient.findPage(searchVo);
//			List<StockDetailHis> hisList = new ArrayList<StockDetailHis>();
//			for(StockDetailHis his :page.getContent()){
//				if(hisList.size()==0){
//					hisList.add(his);
//				}else{
//					StockDetailHis compareHis = hisList.get(hisList.size()-1);
//					if(!(his.getOperationType().equals(compareHis.getOperationType())&&his.getDealNumber().equals(compareHis.getDealNumber())
//					  &&his.getWarehouseFrozenRemain().equals(compareHis.getWarehouseFrozenRemain())&&his.getWarehouseRemain().equals(compareHis.getWarehouseRemain()))){
//						hisList.add(his);
//					}
//				}
//			}
//			page.setContent(hisList);*/
//			JsonEasyUI.renderJson(response, page);
//		}
//	}
//	
////	@PostMapping(value="queryStockNumber")
////	@ResponseBody
////	public Stock queryStockNumber(ApplySellWarehouseVo stockVo){
////		stockVo.setEnterpriseId(ShiroUtil.getEnterpriseId());
////		stockVo.setProductAttr(BasConstants.STOCK_PRODUCT_ATTR_N);
////		List<Stock> stocklist = stockClient.findDealNumber(stockVo);
////		Stock stock = stocklist.isEmpty()?new Stock():stocklist.get(0);
////		return stock;
////	}
//	
//	@PostMapping(value="stockList")
//	public void stockList(RptStockReport searchVo, HttpServletRequest request, HttpServletResponse response){
//		/*initSearch(searchVo, request);
//		String stockStatus = searchVo.getStockStatus();
//		if(stockStatus==null){
//			searchVo.setStockStatus(BasConstants.APPROVE_STATUS_C);
//		}
//		PageDown<Stock> page = stockClient.findPageStock(searchVo);
//		
//		Map<String, Object> footer =new HashMap<>();
//		Stock stock = stockClient.sumPageVo(searchVo);
//		footer.put("averagePrice","合计");
//		footer.put("totalNumber", stock.getTotalNumber());
//		footer.put("frozenNumber", stock.getFrozenNumber());
//		footer.put("realNumber", stock.getRealNumber());
//		JsonEasyUI.renderJson(response, page,null,footer);*/
//		searchVo.setEnterpriseId(ShiroUtil.getEnterpriseId());
//		PageDown<RptStockReport> page = stockInventoryClient.findStockPage(searchVo);
//		RptStockReport total = stockInventoryClient.findStockPageTotal(searchVo);
//		Map<String, Object> footer =new HashMap<>();
//		footer.put("averagePrice","合计");
//		footer.put("totalNumber", total.getTotalNumber());
//		footer.put("frozenNumber", total.getFrozenNumber());
//		footer.put("realNumber", total.getRealNumber());
//		JsonEasyUI.renderJson(response, page,null,footer);
//	}
//	
//	@RequestMapping(value = "/exportExcel")
//    @ResponseBody
//    public void exportExcel(StockSearchVo searchVo, HttpServletRequest request,HttpServletResponse response) throws ApplicationException {
//		 initSearch(searchVo, request);
//		 int batchSize = 500;
//		 searchVo.setRows(batchSize);
//		 String stockStatus = searchVo.getStockStatus();
//		 if(stockStatus==null){
//			searchVo.setStockStatus(BasConstants.APPROVE_STATUS_C);
//		 }
//		 PageDown<Stock> page = stockClient.findPageStock(searchVo);
//		 Page<Stock> pageVo = preStockData(page);
//		 String title ="库存查询";
//		 
//		 String[] titles=new String[] {"品名","牌号","厂商","仓库","平均价(元)","总数量(吨)","冻结数量(吨)","可用数量(吨)","在途/现货"};
//		 String[] attrs=new String[] {"productName","brandNumber","factoryName","warehouseName","averagePrice","totalNumber","frozenNumber","realNumber","productAttr"};
//		 int [] widths =new int[] {15,15,20,15,15,15,15,15,15};
//		 Workbook workbook = PoiExcelUtil.newWorkbook(PoiExcelUtil.WB_TYPE_2007);
//		 // 生成一个表格
//		 Sheet sheet = workbook.createSheet(title);
//		 // 设置表格默认列宽度为 15 个字节
//		 sheet.setDefaultColumnWidth(15);
//		 // 产生表格标题行
//		 // 生成一个样式
//		 CellStyle cellStyle = PoiExcelUtil.getCellStyle(workbook);
//		 /** 创建表头 */
//		 int[] widthes = new int[titles.length];
//		 for (int i = 0; i < titles.length; i++) {
//		    widthes[i] = widths[i];
//		 }
//		 PoiExcelUtil.creatHeads(workbook, sheet, titles, widthes);
//		 int start =0;
//		 while (pageVo!=null && pageVo.getContent().size()>0) {
//			 PoiExcelUtil.createRows(sheet, pageVo.getContent(), attrs, start, cellStyle,DateOperator.FORMAT_STR);
//			 if (pageVo.hasNext()) {
//				 searchVo.setPage(searchVo.getPage()+1);
//				 page = stockClient.findPageStock(searchVo);
//				 pageVo = preStockData(page);
//				 start += batchSize;
//			 }else {
//				 pageVo=null;
//			 }
//		 }
//		    
//	    try {
//	      PoiExcelUtil.write(workbook, response, title);
//	    } catch (IOException e) {
//	      logger.error(e.getMessage(),e);
//	    }  
//		  
//	}
//	
//	private Page<Stock> preStockData(Page<Stock> pageVo){
//		if(pageVo!=null && pageVo.getContent().size()>0){
//			for (Stock stock : pageVo.getContent()) {
//				stock.setProductAttr(DictUtil.getValue(BasConstants.STOCK__PRODUCT_ATTR, stock.getProductAttr()));
//			}
//		}
//		return pageVo;
//	}
//}
