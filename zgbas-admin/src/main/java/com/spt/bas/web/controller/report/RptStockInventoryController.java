package com.spt.bas.web.controller.report;

import com.spt.auth.sdk.cache.DictUtil;
import com.spt.auth.sdk.entity.SysDeptSdk;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.StockDetail;
import com.spt.bas.client.remote.IBsProductTypeClient;
import com.spt.bas.client.remote.IStockDetailClient;
import com.spt.bas.report.client.entity.RptStockInventoryReport;
import com.spt.bas.report.client.remote.IRptStockInventoryClient;
import com.spt.bas.web.shiro.ShiroUtil;
import com.spt.bas.web.util.EasyTreeUtil2;
import com.spt.bas.web.util.WebParamUtils;
import com.spt.tools.core.date.DateOperator;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.core.json.JsonUtil;
import com.spt.tools.data.easyui.EasyTreeNode;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.data.vo.BaseVo;
import com.spt.tools.data.vo.PageDown;
import com.spt.tools.file.poi.PoiExcelUtil;
import com.spt.tools.web.controller.PageController;
import com.spt.tools.web.util.JsonEasyUI;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 库存统计报表
 */
@Controller
@RequestMapping(value = "/rpt/stockInventory")
public class RptStockInventoryController extends PageController<StockDetail, BaseVo> {
	@Resource
	private WebParamUtils webParamUtils;
	@Autowired
	private IBsProductTypeClient bsProductTypeClient;
	@Autowired
	private IStockDetailClient stockDetailClient;
	@Autowired
	private IRptStockInventoryClient stockInventoryClient;
	@Override
	public BaseClient<StockDetail> getService() {
		return stockDetailClient;
	}

	@RequestMapping(value = "")
	public String detail(Model model) {
		model.addAttribute("productAttrJson",
				JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.STOCK__PRODUCT_ATTR)));
		model.addAttribute("stockTypeJson",
				JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_STOCKTYPE)));
		model.addAttribute("spotTypeJson",
				JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_SPOTTYPE)));
		// 货品树
		List<EasyTreeNode> productTree = bsProductTypeClient.findAllProductTree(ShiroUtil.getEnterpriseId());
		model.addAttribute("productTypeJson", JsonUtil.obj2Json(productTree));
		// 获取业务员树
		List<SysDeptSdk> deptList = webParamUtils.getDeptAll();
		EasyTreeNode nodes = EasyTreeUtil2.getDeptTree(deptList, true);
		model.addAttribute("matchUserNameTree", JsonUtil.obj2Json(nodes.getChildren()));
		model.addAttribute("deptJson", JsonUtil.obj2Json(deptList));
		return "report/stockInventory";
	}

	@RequestMapping(value = "findStockInventory")
	public void findStockInventory(RptStockInventoryReport searchVo, HttpServletRequest request, HttpServletResponse response) {
		searchVo.setEnterpriseId(ShiroUtil.getEnterpriseId());
		PageDown<RptStockInventoryReport> page = stockInventoryClient.findPageStockInventory(searchVo);
		// 合计统计
		RptStockInventoryReport total = stockInventoryClient.findTotalStockInventory(searchVo);
		Map<String, Object> footer = new HashMap<>();
		footer.put("longFlg", "合计");
		footer.put("totalNumber", total.getTotalNumber());		//合同数量
		footer.put("soldNumber", total.getSoldNumber());		//已售数量
		footer.put("outSoldNumber", total.getOutSoldNumber());	//未售数量
		footer.put("dealedAmount", total.getDealedAmount());	//付款金额
		footer.put("occupation", total.getOccupation());		//占用资金
		JsonEasyUI.renderJson(response, page,null,footer);	
	}
	
	/**
	 * 库存统计报表EXCEL导出
	 * 
	 * @param searchVo
	 * @param request
	 * @param response
	 * @throws ApplicationException
	 */
	@RequestMapping(value = "/exportExcel")
	@ResponseBody
	public void exportExcel(RptStockInventoryReport searchVo, HttpServletRequest request, HttpServletResponse response)
			throws ApplicationException {
		initSearch(searchVo, request);
		int batchSize = 500;
		searchVo.setRows(batchSize);
		searchVo.setEnterpriseId(ShiroUtil.getEnterpriseId());
		PageDown<RptStockInventoryReport> page = stockInventoryClient.findPageStockInventory(searchVo);
		Page<RptStockInventoryReport> pageVo = preStockInventoryData(page);
		String title = "库存统计";
		String[] titles = new String[] {"合同编号","供货商","品名","牌号","厂商","仓库","货物类型","货权","是否长约","合同数量(吨)",
				"已售数量(吨)","未售数量(吨)","付款金额(元)","占用资金(元)","业务员","所属团队","事业部","创建日期"};
		String[] attrs = new String[] {"contractNo","companyName","productName","brandNumber","factoryName","warehouseName",
				"productAttr","spotType","longFlg","totalNumber","soldNumber","outSoldNumber","dealedAmount","occupation",
				"matchUserName","theirTeam","deptName","createDate"};
		int[] widths = new int[] {15,20,15,15,15,20,15,15,15,15,15,15,15,15,15,15,15,15};
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
		int start = 0;
		while (pageVo != null && pageVo.getContent().size() > 0) {
			PoiExcelUtil.createRows(sheet, page.getContent(), attrs, start, cellStyle, DateOperator.FORMAT_STR);
			if (pageVo.hasNext()) {
				searchVo.setPage(searchVo.getPage() + 1);
				page = stockInventoryClient.findPageStockInventory(searchVo);
				pageVo = preStockInventoryData(page);
				start += batchSize;
			} else {
				pageVo = null;
			}
		}
		try {
			PoiExcelUtil.write(workbook, response, title);
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
	}
	private Page<RptStockInventoryReport> preStockInventoryData(Page<RptStockInventoryReport> pageVo) {
		List<SysDeptSdk> deptList = webParamUtils.getDeptAll();
		if (pageVo != null && pageVo.getContent().size() > 0) {
			for (RptStockInventoryReport stockInventoryReport : pageVo.getContent()) {
				stockInventoryReport.setProductAttr(DictUtil.getValue(BasConstants.STOCK__PRODUCT_ATTR, stockInventoryReport.getProductAttr()));
				stockInventoryReport.setSpotType(DictUtil.getValue(BasConstants.DICT_TYPE_SPOTTYPE, stockInventoryReport.getSpotType()));
				String longFlg = stockInventoryReport.getLongFlg();
				if(Objects.equals(longFlg, "1")){
					stockInventoryReport.setLongFlg("是");
				}else{
					stockInventoryReport.setLongFlg("否");
				}
				for (SysDeptSdk sysDept : deptList) {
					if(stockInventoryReport.getDeptId().equals(sysDept.getDeptId())){
						stockInventoryReport.setDeptName(sysDept.getDeptName());
					}
				}
			}
		}
		return pageVo;
	}
}
