package com.spt.bas.web.controller.stock;

import com.google.common.collect.Maps;
import com.spt.auth.sdk.cache.DictUtil;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.BsProductType;
import com.spt.bas.client.entity.StockContract;
import com.spt.bas.client.remote.IBsProductTypeClient;
import com.spt.bas.client.remote.IStockContractClient;
import com.spt.bas.report.client.entity.RptStockContractReportVo;
import com.spt.bas.report.client.remote.IRptStockContractReportClient;
import com.spt.bas.report.client.vo.RptStockContractSearchReportVo;
import com.spt.bas.web.shiro.ShiroUtil;
import com.spt.bas.web.util.WebParamUtils;
import com.spt.tools.core.date.DateOperator;
import com.spt.tools.core.json.JsonUtil;
import com.spt.tools.data.easyui.EasyTreeNode;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.data.vo.BaseVo;
import com.spt.tools.data.vo.PageDown;
import com.spt.tools.file.poi.PoiExcelUtil;
import com.spt.tools.web.controller.SingleCrudControll;
import com.spt.tools.web.util.JsonEasyUI;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 合同库存
 *
 */
@Controller
@RequestMapping(value = "/stock/stockContract")
public class StockContractController extends SingleCrudControll<StockContract, BaseVo>{

	@Autowired
	private IStockContractClient stockContractClient;
	@Autowired
	private IBsProductTypeClient bsProductTypeClient;
	@Resource
	private WebParamUtils webParamUtils;
	@Autowired
	private IRptStockContractReportClient stockContractReportClient;
	@Autowired
	private IBsProductTypeClient productTypeClient;


	@Override
	public BaseClient<StockContract> getService() {
		return stockContractClient;
	}
	
	@Override
	public Map<String, Object> getDefaultFilter() {
		Map<String, Object> map = Maps.newHashMap();
		map.put("EQL_enterpriseId", ShiroUtil.getEnterpriseId());
		return map;
	}
	@Override
	protected void preInsert(StockContract e) {
		e.setEnterpriseId(ShiroUtil.getEnterpriseId());
	}
	
	//跳转库存明细选择页面
	@RequestMapping(value = "choose")
	public String choose(Model model) {
		List<EasyTreeNode> productTree = bsProductTypeClient.findAllProductTree(ShiroUtil.getEnterpriseId());
		model.addAttribute("productTypeJson", JsonUtil.obj2Json(productTree));
		model.addAttribute("stockStatusJson", JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_STOCKSTATUS)));
		model.addAttribute("productType",
				JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_BUYPRODUCT)));
		//获取业务员树
		EasyTreeNode nodes = webParamUtils.getDeptEasyTreeNode(true);
		model.addAttribute("matchUserNameTree",JsonUtil.obj2Json(nodes.getChildren()));
			return "stock/stockContract-choose";
	}
	
	@RequestMapping(value = "findList")
	public String findList(RptStockContractSearchReportVo searchVo, HttpServletRequest request, HttpServletResponse response) {
		initSearch(searchVo, request);
		//PageDown<StockContractVo> page = stockContractClient.findPageVo(searchVo);
		searchVo.setEnterpriseId(ShiroUtil.getEnterpriseId());
		PageDown<RptStockContractReportVo> page = stockContractReportClient.findStockContractPage(searchVo);
		List<BsProductType> allProductAlAndHg = productTypeClient.findAllProductAlAndHg();
		List<String> collect = allProductAlAndHg.stream().map(s -> s.getTypeName()).collect(Collectors.toList());
		String join = StringUtils.join(",", collect);
		page.getContent().stream().forEach(t->{
			if(join.indexOf(t.getProductName()) > 0 ){
				t.setBrandNumber("");
			}
		});
		JsonEasyUI.renderJson(response, page);
		return null;
	}
	
	@RequestMapping(value = "findPage")
	public String findPage(RptStockContractSearchReportVo searchVo, HttpServletRequest request, HttpServletResponse response) {
		initSearch(searchVo, request);
		searchVo.setEnterpriseId(ShiroUtil.getEnterpriseId());
		PageDown<RptStockContractReportVo> page = stockContractReportClient.findPage(searchVo);
		JsonEasyUI.renderJson(response, page);
		return null;
	}
//	@RequestMapping(value = "findList")
//	public String findList(StockContractSearchVo searchVo, HttpServletRequest request, HttpServletResponse response) {
//		initSearch(searchVo, request);
//		PageDown<StockContractVo> page = stockContractClient.findPageVo(searchVo);
//		System.out.println(JsonUtil.obj2Json(page.getContent().get(0)));
//		JsonEasyUI.renderJson(response, page);
//		return null;
//	}
//	
	
	@RequestMapping(value = "/exportExcel")
    @ResponseBody
	public void exportExcel(RptStockContractSearchReportVo searchVo, HttpServletRequest request, HttpServletResponse response) {
		initSearch(searchVo, request);
		int batchSize = 500;
		 searchVo.setRows(batchSize);
		searchVo.setEnterpriseId(ShiroUtil.getEnterpriseId());
		PageDown<RptStockContractReportVo> page = stockContractReportClient.findPage(searchVo);
		String title ="合同库存";
		 String[] titles=new String[] {"采购合同编号","品名","牌号","厂商名称","仓库","采购单元","仓储费(吨)","采购数量(吨)","销售中数量(吨)","销售数量(吨)","入库数量(吨)","出库数量(吨)","合同时长","业务员"};
		 String[] attrs=new String[] {"contractNo","productName","brandNumber","factoryName","warehouseName","dealPrice","warehouseAmount","buyNumber","sellingNumber","sellNumber",
				 "deliveryInNumber","deliveryOutNumber","contractDifTime","bizUserName"};
		 int [] widths =new int[] {15,15,20,15,15,20,15,15,15,15,15,15,15,15};
		 Workbook workbook = PoiExcelUtil.newWorkbook(PoiExcelUtil.WB_TYPE_2007);
		 // 生成一个表格
		 Sheet sheet = workbook.createSheet(title);
		 // 设置表格默认列宽度为 15 个字节
		 sheet.setDefaultColumnWidth(15);
		 // 产生表格标题行
		 // 生成一个样式
		 CellStyle cellStyle = PoiExcelUtil.getCellStyle(workbook);
		 /** 创建表头 */
		 int[] widthes = new int[titles.length];
		 for (int i = 0; i < titles.length; i++) {
		    widthes[i] = widths[i];
		 }
		 PoiExcelUtil.creatHeads(workbook, sheet, titles, widthes);
		 int start =0;
		 while (page!=null && page.getContent().size()>0) {
			 PoiExcelUtil.createRows(sheet, page.getContent(), attrs, start, cellStyle,DateOperator.FORMAT_STR_WITH_TIME);
			 if (page.hasNext()) {
				 searchVo.setPage(searchVo.getPage()+1);
				 page = stockContractReportClient.findPage(searchVo);
				 start += batchSize;
			 }else {
				 page=null;
			 }
		 }
		    
	    try {
	      PoiExcelUtil.write(workbook, response, title);
	    } catch (IOException e) {
	      logger.error(e.getMessage(),e);
	    }  
	}
}
