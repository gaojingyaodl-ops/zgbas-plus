package com.spt.bas.web.controller.report;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.CtrContract;
import com.spt.bas.client.remote.IBsProductTypeClient;
import com.spt.bas.client.remote.ICtrContractClient;
import com.spt.bas.report.client.entity.RptCtrContractStatistics;
import com.spt.bas.report.client.remote.IRptCtrContractStatisticsClient;
import com.spt.bas.report.client.vo.RptStatisticsVo;
import com.spt.bas.web.shiro.ShiroUtil;
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

/**
 * 采购统计
 * 
 * @author zhouzihang
 */
@Controller
@RequestMapping(value = "/rpt/buystatistics")
public class RptBuyStatisticsController extends PageController<CtrContract, BaseVo> {
	@Autowired
	private IRptCtrContractStatisticsClient ctrContractStatisticsClient;
	@Autowired
	private IBsProductTypeClient bsProductTypeClient;
	@Autowired
	private ICtrContractClient ctrContractClient;

	// 采购统计页
	@RequestMapping(value = "")
	public String index(Model model) {
		// 货品树
		List<EasyTreeNode> productTree = bsProductTypeClient.findAllProductTree(ShiroUtil.getEnterpriseId());
		model.addAttribute("productTypeJson", JsonUtil.obj2Json(productTree));
		model.addAttribute("contractType", "B");
		return "report/buyOrSellStatistics";
	}

	// 展示采购统计
	@RequestMapping(value = "showStatistics")
	public void showStatistics(RptStatisticsVo vo, HttpServletRequest request, HttpServletResponse response) {
		vo.setEnterpriseId(ShiroUtil.getEnterpriseId());
		PageDown<RptCtrContractStatistics> page = ctrContractStatisticsClient.showStatistics(vo);
		JsonEasyUI.renderJson(response, page);
	}
	
	/**
	 * 采购统计EXCEL导出
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
		PageDown<RptCtrContractStatistics> page = ctrContractStatisticsClient.showStatistics(searchVo);
		String title = "销售统计";
		String contractType = searchVo.getContractType();
		if (contractType.equals(BasConstants.CONTRACTTYPE_BUY)) {
			title = "采购统计";
		}
		String[] titles = new String[] { "统计项", "总数量(吨)", "总金额(元)" };
		String[] attrs = new String[] { "statisticsType", "dealNumber", "totalPrice" };
		int[] widths = new int[] { 20, 20, 20 };
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
			PoiExcelUtil.createRows(sheet, page.getContent(), attrs, start, cellStyle, DateOperator.FORMAT_STR);
			if (page.hasNext()) {
				searchVo.setPage(searchVo.getPage() + 1);
				page = ctrContractStatisticsClient.showStatistics(searchVo);
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

	@Override
	public BaseClient<CtrContract> getService() {
		return ctrContractClient;
	}

}
