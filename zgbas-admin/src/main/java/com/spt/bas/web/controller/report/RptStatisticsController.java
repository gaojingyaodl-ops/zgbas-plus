package com.spt.bas.web.controller.report;

import com.spt.auth.sdk.cache.DictUtil;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.CtrContract;
import com.spt.bas.client.remote.ICtrContractClient;
import com.spt.bas.report.client.entity.RptCtrContractMatchingReport;
import com.spt.bas.report.client.remote.IRptCtrContractMatchingClient;
import com.spt.bas.report.client.vo.RptAssementSearchVo;
import com.spt.bas.web.shiro.ShiroUtil;
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
import java.util.Map;

/**
 * 撮合考核统计
 */
@Controller
@RequestMapping(value = "/rpt/statistics")
public class RptStatisticsController extends PageController<CtrContract, BaseVo> {
	@Resource
	private WebParamUtils webParamUtils;
	@Autowired
	private ICtrContractClient ctrContractClient;
	@Autowired
	private IRptCtrContractMatchingClient ctrContractMatchingClient;

	@Override
	public BaseClient<CtrContract> getService() {
		return ctrContractClient;
	}

	@RequestMapping(value = "matching")
	public String matching(Model model) {
		model.addAttribute("contractTypeJson",
				JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_CONTRACTTYPE)));
		model.addAttribute("contractStatusJson",
				JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_CONTRACTSTATUS)));
		model.addAttribute("productTypeJson",
				JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_BUYPRODUCT)));
		// 获取业务员树
		EasyTreeNode nodes = webParamUtils.getDeptEasyTreeNode(true);
		model.addAttribute("matchUserNameTree", JsonUtil.obj2Json(nodes.getChildren()));
		return "report/matching";
	}

	// 撮合考核统计
	@RequestMapping(value = "findMatching")
	public void findMatching(RptAssementSearchVo searchVo, HttpServletRequest request, HttpServletResponse response) {
		searchVo.setEnterpriseId(ShiroUtil.getEnterpriseId());
		PageDown<RptCtrContractMatchingReport> page = ctrContractMatchingClient.findPageMatching(searchVo);
		RptCtrContractMatchingReport total = ctrContractMatchingClient.findPageTotal(searchVo);
		Map<String, Object> footer = new HashMap<>();
		footer.put("buyCompanyName", "合计");
		footer.put("sellNumber", total.getSellNumber());
		footer.put("balance", total.getBalance());
		footer.put("transportAmount", total.getTransportAmount());
		footer.put("profit", total.getProfit());
		footer.put("bountyAmoun", total.getBountyAmoun());
		footer.put("bounty", total.getBounty());
		JsonEasyUI.renderJson(response, page, null, footer);
	}

	/**
	 * 撮合考核统计EXCEL导出
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
		PageDown<RptCtrContractMatchingReport> page = ctrContractMatchingClient.findPageMatching(searchVo);
		String title = "撮合考核统计";

		String[] titles = new String[] { "品名", "牌号", "厂商", "采购企业(上家)", "销售数量(吨)", "采购单价(元)", "付款日期", "销售企业(下家)",
				"销售单价(元)", "收款日期", "差额(元)", "运费(元)", "毛利(元)", "奖励金额(元)", "采购业务员", "奖励(元)", "销售业务员", "奖励(元)" };
		String[] attrs = new String[] { "productName", "brandNumber", "factoryName", "buyCompanyName", "sellNumber",
				"buyPrice", "buyPayTime", "sellCompanyName", "sellPrice", "sellPayTime", "balance", "transportAmount",
				"profit", "bountyAmoun", "buyMatchName", "bounty", "sellMatchName", "bounty" };
		int[] widths = new int[] { 15, 15, 15, 20, 15, 15, 20, 20, 15, 20, 15, 15, 15, 15, 15, 15, 15, 15 };
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
				page = ctrContractMatchingClient.findPageMatching(searchVo);
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
