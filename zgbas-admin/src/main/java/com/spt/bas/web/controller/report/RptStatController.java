package com.spt.bas.web.controller.report;

import com.spt.auth.sdk.cache.DictUtil;
import com.spt.auth.sdk.entity.SysDeptSdk;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.CtrContract;
import com.spt.bas.client.remote.IBsProductTypeClient;
import com.spt.bas.client.remote.ICtrContractClient;
import com.spt.bas.report.client.entity.RptCtrContractAsseMentReport;
import com.spt.bas.report.client.remote.IRptCtrContractAsseMentClient;
import com.spt.bas.report.client.vo.RptAssementSearchVo;
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

/**
 * 自营审核
 */
@Controller
@RequestMapping(value = "/rpt/stat")
public class RptStatController extends PageController<CtrContract, BaseVo> {
	@Resource
	private WebParamUtils webParamUtils;
	@Autowired
	private ICtrContractClient ctrContractClient;
	@Autowired
	private IRptCtrContractAsseMentClient ctrContractAsseMentClient;
	@Autowired
	private IBsProductTypeClient bsProductTypeClient;
	@Override
	public BaseClient<CtrContract> getService() {
		return ctrContractClient;
	}

	@RequestMapping(value = "assessment")
	public String assessment(Model model) {
		model.addAttribute("contractTypeJson",
				JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_CONTRACTTYPE)));
		model.addAttribute("contractStatusJson",
				JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_CONTRACTSTATUS)));
		model.addAttribute("productTypeJson",
				JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_BUYPRODUCT)));
		// 货品树
		List<EasyTreeNode> productTree = bsProductTypeClient.findAllProductTree(ShiroUtil.getEnterpriseId());
		model.addAttribute("productTypeJson", JsonUtil.obj2Json(productTree));
		// 获取业务员树
		List<SysDeptSdk> deptList = webParamUtils.getDeptAll();
		EasyTreeNode nodes = EasyTreeUtil2.getDeptTree(deptList, true);
		model.addAttribute("matchUserNameTree", JsonUtil.obj2Json(nodes.getChildren()));
		model.addAttribute("deptJson", JsonUtil.obj2Json(deptList));
		return "report/assessment";
	}

	// 自营考核统计
	@RequestMapping(value = "findAssessment")
	public void findAssessment(RptAssementSearchVo searchVo, HttpServletRequest request, HttpServletResponse response) {
		searchVo.setEnterpriseId(ShiroUtil.getEnterpriseId());
		PageDown<RptCtrContractAsseMentReport> page = ctrContractAsseMentClient.findPageAssessment(searchVo);
		// 合计
		RptCtrContractAsseMentReport report = ctrContractAsseMentClient.findPageTotal(searchVo);
		Map<String, Object> footer = new HashMap<>();
		footer.put("buyCompanyName", "合计");
		footer.put("sellNumber", report.getSellNumber());// 销售总量
		footer.put("transAmount", report.getTransAmount());//运费
		footer.put("buyMatchProfit", report.getBuyMatchProfit());// 采购员毛利
		footer.put("sellMatchProfit", report.getSellMatchProfit());// 销售员毛利
		JsonEasyUI.renderJson(response, page, null, footer);
	}

	/**
	 * 自营审核统计EXCEL导出
	 * @param searchVo
	 * @param request
	 * @param response
	 * @throws ApplicationException
	 */
	@RequestMapping(value = "/exportExcel")
	@ResponseBody
	public void exportExcel(RptAssementSearchVo searchVo, HttpServletRequest request, HttpServletResponse response)
			throws ApplicationException {
		initSearch(searchVo, request);
		int batchSize = 500;
		searchVo.setRows(batchSize);
		searchVo.setEnterpriseId(ShiroUtil.getEnterpriseId());
		PageDown<RptCtrContractAsseMentReport> page = ctrContractAsseMentClient.findPageAssessment(searchVo);
		String title = "自营考核统计";

		String[] titles = new String[] { "采购日期", "品名", "牌号", "厂商", "采购企业(上家)", "销售数量(吨)", "采购单价(元)", "采购额(元)", "运费(元)",
				"采购业务员", "采购员毛利(元)", "收款日期", "销售单价(元)", "销售额(元)", "销售企业(下家)", "销售业务员", "销售员毛利(元)" };
		String[] attrs = new String[] { "buyDate", "productName", "brandNumber", "factoryName", "buyCompanyName",
				"sellNumber", "buyPrice", "buyAmount", "transAmount", "buyUserName", "buyMatchProfit", "payTime",
				"sellPrice", "sellAmount", "sellCompanyName", "sellUserName", "sellMatchProfit" };
		int[] widths = new int[] { 20, 15, 15, 15, 20, 15, 15, 15, 15, 15, 15, 20, 15, 15, 20, 15, 15 };
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
		while (page != null && page.getContent().size() > 0) {
			PoiExcelUtil.createRows(sheet, page.getContent(), attrs, start, cellStyle, DateOperator.FORMAT_STR_WITH_TIME);
			if (page.hasNext()) {
				searchVo.setPage(searchVo.getPage() + 1);
				page = ctrContractAsseMentClient.findPageAssessment(searchVo);
				start += batchSize;
			} else {
				page = null;
			}
		}
		try {
			PoiExcelUtil.write(workbook, response, title);
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
	}
}
