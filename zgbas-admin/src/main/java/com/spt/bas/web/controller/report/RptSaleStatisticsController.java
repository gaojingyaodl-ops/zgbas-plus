package com.spt.bas.web.controller.report;

import com.spt.auth.sdk.cache.DictUtil;
import com.spt.auth.sdk.entity.SysDeptSdk;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.StockDetail;
import com.spt.bas.client.remote.IBsProductTypeClient;
import com.spt.bas.client.remote.IStockDetailClient;
import com.spt.bas.report.client.entity.RptCtrContractStatistics;
import com.spt.bas.report.client.remote.IRptCtrContractStatisticsClient;
import com.spt.bas.report.client.vo.RptStatisticsVo;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 进销存统计
 * 
 * @author zhouzihang
 */
@Controller
@RequestMapping(value = "/rpt/buyandsell")
public class RptSaleStatisticsController extends PageController<StockDetail, BaseVo> {
	@Resource
	private WebParamUtils webParamUtils;
	@Autowired
	private IRptCtrContractStatisticsClient ctrContractStatisticsClient;
	@Autowired
	private IBsProductTypeClient bsProductTypeClient;
	@Autowired
	private IStockDetailClient stockDetailClient;

	// 进销存统计页
	@RequestMapping(value = "")
	public String Matching(Model model) {
		model.addAttribute("contractAttrJson", // 合同类型(现货-期货)
				JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_CONTRACTATTR)));
		model.addAttribute("deliveryModeJson", // 交货方式
				JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.TEMPLATE_CONTENT_DELIVERYMODE)));
		model.addAttribute("deliveryTypeJson", // 提货方式
				JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_BUYDELIVERY)));
		// 货品树
		List<EasyTreeNode> productTree = bsProductTypeClient.findAllProductTree(ShiroUtil.getEnterpriseId());
		model.addAttribute("productTypeJson", JsonUtil.obj2Json(productTree));
		// 获取业务员树
		List<SysDeptSdk> deptList = webParamUtils.getDeptAll();
		EasyTreeNode nodes = EasyTreeUtil2.getDeptTree(deptList, true, true);
		model.addAttribute("deptJson", JsonUtil.obj2Json(deptList));
		model.addAttribute("matchUserNameTree", JsonUtil.obj2Json(nodes.getChildren()));
		return "report/saleStatistics";
	}

	// 展示采购合同及统计
	@RequestMapping(value = "findContractRela")
	public void findContractRela(RptStatisticsVo vo, HttpServletRequest request, HttpServletResponse response) {
		vo.setEnterpriseId(ShiroUtil.getEnterpriseId());
		// 采购合同分页
		PageDown<RptCtrContractStatistics> page = ctrContractStatisticsClient.findBuyCtrContract(vo);
		// 采购合同数据统计
		RptCtrContractStatistics statistics = ctrContractStatisticsClient.getContractStatistics(vo);
		Map<String, Object> footer = new HashMap<>();
		footer.put("companyName", "合计");
		footer.put("dealNumber", statistics.getDealNumberTotal());// 采购总量
		footer.put("salesNumber", statistics.getSalesNumberTotal());// 销售总量
		footer.put("remainNumber", statistics.getRemainNumberTotal());// 剩余总量
		footer.put("overplusPrice", statistics.getOverplusPriceTotal());// 剩余总货值
		footer.put("occupation", statistics.getOccupation());//资金占用
		JsonEasyUI.renderJson(response, page, null, footer);
	}

	// 展示对应销售合同
	@RequestMapping(value = "findSaleContract")
	public void findSaleContract(@RequestParam Long id, HttpServletResponse response) {
		PageSearchVo searchVo = new PageSearchVo();
		searchVo.setRows(30);
		Map<String, Object> map = new HashMap<>();
		map.put("productId", id);
		searchVo.setSearchParams(map);
		PageDown<RptCtrContractStatistics> page = ctrContractStatisticsClient.findSaleCtrContract(searchVo);
		JsonEasyUI.renderJson(response, page);
	}

	/**
	 * 进销存统计EXCEL导出
	 * 
	 * @param searchVo
	 * @param request
	 * @param response
	 * @throws ApplicationException
	 */
	@RequestMapping(value = "/exportExcel")
	@ResponseBody
	public void exportExcel(RptStatisticsVo searchVo, HttpServletRequest request, HttpServletResponse response)
			throws ApplicationException {
		initSearch(searchVo, request);
		int batchSize = 500;
		searchVo.setRows(batchSize);
		searchVo.setEnterpriseId(ShiroUtil.getEnterpriseId());
		PageDown<RptCtrContractStatistics> page = ctrContractStatisticsClient.findBuyCtrContract(searchVo);
		Page<RptCtrContractStatistics> pageVo = preDeliveryReprtData(page);
		String title = "进销存统计";

		String[] titles = new String[] { "采购合同", "采购日期", "现货/期货", "品名", "牌号", "厂商", "供方单位", "仓库", "采购数量(吨)", "采购单价(元)",
				"付款日期", "已销数量(吨)", "剩余数量(吨)", "在库时长(小时)", "剩余货值(元)","资金占用(元)", "业务员", "所属事业部" };
		String[] attrs = new String[] { "contractNo", "buyDate", "contractAttr", "productName", "brandNumber",
				"factoryName", "companyName", "warehouseAddr", "dealNumber", "dealPrice", "payTime", "salesNumber",
				"remainNumber", "inWarehouseHours", "overplusPrice","occupation", "matchUserName", "deptName" };
		int[] widths = new int[] { 15, 20, 15, 15, 15, 15, 20, 15, 15, 15, 20, 15, 15, 15, 15, 15, 15 ,15};
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
				page = ctrContractStatisticsClient.findBuyCtrContract(searchVo);
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

	private Page<RptCtrContractStatistics> preDeliveryReprtData(Page<RptCtrContractStatistics> pageVo) {
		List<SysDeptSdk> deptList = webParamUtils.getDeptAll();
		if (pageVo != null && pageVo.getContent().size() > 0) {
			for (RptCtrContractStatistics vo : pageVo.getContent()) {
				vo.setContractAttr(DictUtil.getValue(BasConstants.STOCK__CONTRACT_ATTR, vo.getContractAttr()));
				for (SysDeptSdk sysDept : deptList) {
					if(vo.getDeptId().equals(sysDept.getDeptId())){
						vo.setDeptName(sysDept.getDeptName());
					}
				}
			}
		}
		return pageVo;
	}

	@Override
	public BaseClient<StockDetail> getService() {
		return stockDetailClient;
	}
}
