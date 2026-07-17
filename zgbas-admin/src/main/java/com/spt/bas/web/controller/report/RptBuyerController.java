package com.spt.bas.web.controller.report;

import com.spt.auth.sdk.cache.DictUtil;
import com.spt.auth.sdk.entity.SysDeptSdk;
import com.spt.bas.client.cache.BsCompanyOurUtil;
import com.spt.bas.client.cache.BsDictUtil;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.StockDetail;
import com.spt.bas.client.remote.IBsProductTypeClient;
import com.spt.bas.client.remote.IStockDetailClient;
import com.spt.bas.report.client.entity.RptDeliveryOutReport;
import com.spt.bas.report.client.remote.IRptStockInventoryClient;
import com.spt.bas.web.shiro.ShiroUtil;
import com.spt.bas.web.util.EasyTreeUtil2;
import com.spt.bas.web.util.WebParamUtils;
import com.spt.tools.core.bean.PageSearchVo;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 实际出库明细统计
 */
@Controller
@RequestMapping(value = "/rpt/buyer")
public class RptBuyerController extends PageController<StockDetail, BaseVo> {
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

	@RequestMapping(value = "deliveryReport")
	public String deliveryReport(Model model) {
		model.addAttribute("contractTypeJson",
				JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_CONTRACTTYPE)));
		model.addAttribute("contractStatusJson",
				JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_CONTRACTSTATUS)));
		model.addAttribute("productTypeJson",
				JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_BUYPRODUCT)));
		model.addAttribute("contractAttrJson",
				JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_CONTRACTATTR)));
		model.addAttribute("ourCompanyJson", JsonUtil.obj2Json(
				BsCompanyOurUtil.getCompanyOurToBsDictDataList()));
		// 货品树
		List<EasyTreeNode> productTree = bsProductTypeClient.findAllProductTree(ShiroUtil.getEnterpriseId());
		model.addAttribute("productJson", JsonUtil.obj2Json(productTree));
		// 获取业务员树
		List<SysDeptSdk> deptList = webParamUtils.getDeptAll();
		EasyTreeNode nodes = EasyTreeUtil2.getDeptTree(deptList, true);
		model.addAttribute("matchUserNameTree", JsonUtil.obj2Json(nodes.getChildren()));
		model.addAttribute("deptAllJson", JsonUtil.obj2Json(deptList));
		return "report/deliveryOut-report";
	}

	// 出库明细统计
	@RequestMapping(value = "findDeliveryReport")
	public void findDeliveryReport(RptDeliveryOutReport searchVo, HttpServletRequest request,
			HttpServletResponse response) {
		initSearch(searchVo, request);
		searchVo.setEnterpriseId(ShiroUtil.getEnterpriseId());
		PageDown<RptDeliveryOutReport> page = stockInventoryClient.findDeliveryOut(searchVo);
		RptDeliveryOutReport total = stockInventoryClient.findTotalDeliveryOut(searchVo);
		Map<String, Object> footer = new HashMap<>();
		footer.put("companyName", "合计");
		footer.put("totalNumber", total.getTotalNumber());
		footer.put("deliveryInNumber", total.getDeliveryInNumber());
		footer.put("deliveryOutNumber", total.getDeliveryOutNumber());
		footer.put("surplusNumber", total.getSurplusNumber());
		JsonEasyUI.renderJson(response, page, null, footer);
	}

	@RequestMapping(value = "deliveryDetail/{id}", method = RequestMethod.GET)
	public String deliveryDeatil(@PathVariable("id") Long id, Model model) {
		if (id != null && id > 0L) {
			model.addAttribute("stockDetailId", id);
			model.addAttribute("deliveryTypeJson",
					JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_BUYDELIVERY)));
			model.addAttribute("contractStatusJson",
					JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_CONTRACTSTATUS)));
			return "report/delivery-datail";
		}
		return null;
	}

	@RequestMapping(value = "deliveryList/{id}")
	public void deliveryList(@PathVariable("id") String stockDetailId, PageSearchVo searchVo, HttpServletRequest request,
			HttpServletResponse response) {
		//TODO 这里需要增加方法：根据stockDetailId查询所有出库申请单
//		initSearch(searchVo, request);
//		Map<String, Object> map = new HashMap<String, Object>();
//		StockDetail stockDetail = stockDetailClient.getEntity(Long.valueOf(stockDetailId));
//		String sellIds = stockDetail.getSellContractId();
//		if (StringUtils.isNotBlank(sellIds)) {
//			String[] split = sellIds.split(",");
//			String[] ids = FormConfigUtil.removeArrayEmptyTextBackNewArray(split);
//			Long[] sellArr = FormConfigUtil.formateArray(ids);
//			map.put("INL_contractId", sellArr);
//		}
//		searchVo.setSearchParams(map);
//		PageDown<DeliveryDetailVo> page = applyDeliveryClient.findPageDetail(searchVo);
//		JsonEasyUI.renderJson(response, page);
	}
	/**
	 * 实际出库明细EXCEL导出
	 * @param searchVo
	 * @param request
	 * @param response
	 * @throws ApplicationException
	 */
	@RequestMapping(value = "/exportExcel")
	@ResponseBody
	public void exportExcel(RptDeliveryOutReport searchVo, HttpServletRequest request, HttpServletResponse response)
			throws ApplicationException {
		initSearch(searchVo, request);
		int batchSize = 500;
		searchVo.setRows(batchSize);
		searchVo.setEnterpriseId(ShiroUtil.getEnterpriseId());
		PageDown<RptDeliveryOutReport> page = stockInventoryClient.findDeliveryOut(searchVo);
		Page<RptDeliveryOutReport> pageVo = preDeliveryReprtData(page);
		String title = "实际出库明细";

		String[] titles = new String[] { "采购合同号", "现货/期货", "采购日期", "品名","牌号","厂商","我方抬头", "供方单位", "采购数量(吨)", "入库数量(吨)", "出库数量(吨)",
				"结余数量(吨)", "入库单号", "入库仓库", "业务员", "所属事业部" };
		String[] attrs = new String[] { "contractNo", "contractAttr", "contractTime", "productName","brandNumber","factoryName","ourCompanyName","companyName",
				"totalNumber", "deliveryInNumber", "deliveryOutNumber", "surplusNumber", "warehouseNo", "warehouse",
				"matchUserName", "deptName" };
		int[] widths = new int[] { 15, 15, 20, 15,15,15,20, 20, 15, 15, 15, 15, 15, 15, 15, 15 };
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
			PoiExcelUtil.createRows(sheet, pageVo.getContent(), attrs, start, cellStyle, DateOperator.FORMAT_STR_WITH_TIME);
			if (pageVo.hasNext()) {
				searchVo.setPage(searchVo.getPage() + 1);
				page = stockInventoryClient.findDeliveryOut(searchVo);
				pageVo = preDeliveryReprtData(page);
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

	private Page<RptDeliveryOutReport> preDeliveryReprtData(Page<RptDeliveryOutReport> pageVo) {
		List<SysDeptSdk> deptList = webParamUtils.getDeptAll();
		if (pageVo != null && pageVo.getContent().size() > 0) {
			for (RptDeliveryOutReport reportVo : pageVo.getContent()) {
				reportVo.setContractAttr(DictUtil.getValue(BasConstants.STOCK__CONTRACT_ATTR, reportVo.getContractAttr()));
				for (SysDeptSdk sysDept : deptList) {
					if(reportVo.getDeptId().equals(sysDept.getDeptId())){
						reportVo.setDeptName(sysDept.getDeptName());
					}
				}
			}
		}
		return pageVo;
	}
}
