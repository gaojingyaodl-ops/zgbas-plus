package com.spt.bas.web.controller.report;

import com.spt.bas.client.entity.ApplyInternalBuy;
import com.spt.bas.client.remote.IApplyInternalBuyClient;
import com.spt.bas.client.remote.IBsProductTypeClient;
import com.spt.bas.report.client.entity.RptApplyInternalReport;
import com.spt.bas.report.client.remote.IRptApplyInternalClient;
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
import java.util.List;

/**
 * 内部交易统计报表
 */
@Controller
@RequestMapping(value = "/rpt/internalBuy")
public class RptInternalBuyController extends PageController<ApplyInternalBuy, BaseVo> {
	@Resource
	private WebParamUtils webParamUtils;
	@Autowired
	private IBsProductTypeClient bsProductTypeClient;
	@Autowired
	private IApplyInternalBuyClient applyInternalBuyClient;
	@Autowired
	private IRptApplyInternalClient applyInternalClient;
	@Override
	public BaseClient<ApplyInternalBuy> getService() {
		return applyInternalBuyClient;
	}

	@RequestMapping(value = "")
	public String detail(Model model) {
		// 货品树
		List<EasyTreeNode> productTree = bsProductTypeClient.findAllProductTree(ShiroUtil.getEnterpriseId());
		model.addAttribute("productTypeJson", JsonUtil.obj2Json(productTree));
		// 获取业务员树
		EasyTreeNode nodes = webParamUtils.getDeptEasyTreeNode(true);
		model.addAttribute("matchUserNameTree", JsonUtil.obj2Json(nodes.getChildren()));
		return "report/internalBuy";
	}

	@RequestMapping(value = "findInternalBuy")
	public void findInternalBuy(RptApplyInternalReport searchVo, HttpServletRequest request, HttpServletResponse response) {
		searchVo.setEnterpriseId(ShiroUtil.getEnterpriseId());
		PageDown<RptApplyInternalReport> page = applyInternalClient.findPageInternalBuy(searchVo);
		JsonEasyUI.renderJson(response, page);
	}

	/**
	 * 内部交易统计报表EXCEL导出
	 * 
	 * @param searchVo
	 * @param request
	 * @param response
	 * @throws ApplicationException
	 */
	@RequestMapping(value = "/exportExcel")
	@ResponseBody
	public void exportExcel(RptApplyInternalReport searchVo, HttpServletRequest request, HttpServletResponse response)
			throws ApplicationException {
		initSearch(searchVo, request);
		int batchSize = 500;
		searchVo.setRows(batchSize);
		searchVo.setEnterpriseId(ShiroUtil.getEnterpriseId());
		PageDown<RptApplyInternalReport> page = applyInternalClient.findPageInternalBuy(searchVo);
		String title = "内部采购统计";
		String[] titles = new String[] { "品名", "牌号", "厂商", "仓库","采购数量(吨)","采购人","原货主","采购日期"};
		String[] attrs = new String[] { "productName", "brandNumber", "factoryName", "warehouseName","buyNumber","matchUserName","shipperUserName","createdDate"};
		int[] widths = new int[] { 15, 15, 15, 15, 15, 15, 15, 20};
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
				page = applyInternalClient.findPageInternalBuy(searchVo);
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
