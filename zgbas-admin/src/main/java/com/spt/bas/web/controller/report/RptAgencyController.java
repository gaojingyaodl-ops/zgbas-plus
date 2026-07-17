package com.spt.bas.web.controller.report;

import com.spt.auth.sdk.cache.DictUtil;
import com.spt.bas.client.cache.BsCompanyOurUtil;
import com.spt.bas.client.cache.BsDictUtil;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.CtrContract;
import com.spt.bas.client.remote.IBsProductTypeClient;
import com.spt.bas.client.remote.ICtrContractClient;
import com.spt.bas.report.client.entity.RptCtrContractAgencyReport;
import com.spt.bas.report.client.remote.IRptCtrContractAgencyClient;
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

/**
 * 代采代销明细
 */
@Controller
@RequestMapping(value = "/rpt/agency")
public class RptAgencyController extends PageController<CtrContract, BaseVo> {
	@Autowired
	private ICtrContractClient ctrContractClient;
	@Resource
	private WebParamUtils webParamUtils;
	@Autowired
	private IRptCtrContractAgencyClient ctrContractAgencyClient;
	@Autowired
	private IBsProductTypeClient bsProductTypeClient;
	@Override
	public BaseClient<CtrContract> getService() {
		return ctrContractClient;
	}

	@RequestMapping(value = "")
	public String detail(Model model) {
		model.addAttribute("contractTypeJson",
				JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_CONTRACTTYPE)));
		model.addAttribute("contractStatusJson",
				JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_CONTRACTSTATUS)));
		model.addAttribute("productTypeJson",
				JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_BUYPRODUCT)));
		model.addAttribute("applyTypeJson",
				JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_APPLYTYPE)));
		model.addAttribute("deliveryModeJson", JsonUtil.obj2Json(
				BsDictUtil.getListByCategory(ShiroUtil.getEnterpriseId(), BasConstants.DICT_TYPE_DELIVERYMODE)));
		// 货品树
		List<EasyTreeNode> productTree = bsProductTypeClient.findAllProductTree(ShiroUtil.getEnterpriseId());
		model.addAttribute("productTypeJson", JsonUtil.obj2Json(productTree));
		// 我方抬头
		model.addAttribute("ourCompanyJson", JsonUtil.obj2Json(
				BsCompanyOurUtil.getCompanyOurToBsDictDataList()));
		// 获取业务员树
		EasyTreeNode nodes = webParamUtils.getDeptEasyTreeNode(true);
		model.addAttribute("matchUserNameTree", JsonUtil.obj2Json(nodes.getChildren()));
		return "report/agency";
	}

	@RequestMapping(value = "findAgency")
	public void findAgency(RptAssementSearchVo searchVo, HttpServletRequest request, HttpServletResponse response) {
		searchVo.setEnterpriseId(ShiroUtil.getEnterpriseId());
		PageDown<RptCtrContractAgencyReport> page = ctrContractAgencyClient.findPageAgency(searchVo);
		RptCtrContractAgencyReport total = ctrContractAgencyClient.findPageTotal(searchVo);
		Map<String, Object> footer = new HashMap<>();
		footer.put("buyPrice", "合计");
		footer.put("buyTotalNumber", total.getBuyTotalNumber());
		footer.put("sellTotalNumber", total.getSellTotalNumber());
		footer.put("warehouseNumber", total.getWarehouseNumber());
		JsonEasyUI.renderJson(response, page, null, footer);
	}
	
	@RequestMapping(value = "findSecondCalculatePage")
	public void findSecondCalculatePage(RptAssementSearchVo searchVo, HttpServletRequest request, HttpServletResponse response) {
		searchVo.setEnterpriseId(ShiroUtil.getEnterpriseId());
		searchVo.setMatchUserId(ShiroUtil.getCurrentUserId());
		PageDown<RptCtrContractAgencyReport> page = ctrContractAgencyClient.findSecondCalculatePage(searchVo);
		JsonEasyUI.renderJson(response, page, null, null);
	}

	/**
	 * 代采代销EXCEL导出
	 * 
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
		PageDown<RptCtrContractAgencyReport> page = ctrContractAgencyClient.findPageAgency(searchVo);
		Page<RptCtrContractAgencyReport> pageVo = preContractData(page);
		String title = "代采代销明细";
		String[] titles = new String[] { "采购合同号", "业务类型", "采购合同时间", "我方抬头", "品名", "牌号", "厂商", "供货商", "采购单价(元)",
				"采购数量(吨)", "采购总额(元)", "采购发票", "未收金额(元)", "发票状态", "最后收票时间", "采购方式", "运输费(元)", "仓储费(元)", "采购业务员", "销售合同号",
				"需货商", "销售单价(元)", "销售数量(吨)", "销售总额(元)", "销售发票", "销售合同时间", "未开金额(元)", "销售方式", "运输费(元)", "仓储费(元)",
				"销售业务员", "出库数量(吨)", "毛利(元)" };
		String[] attrs = new String[] { "buyContractNo", "source", "buyContractDate", "ourCompanyName", "productName",
				"brandNumber", "factoryName", "buyCompanyName", "buyPrice", "buyTotalNumber", "buyTotalAmount",
				"receiveBillNo", "invoiceBillAmount", "billFlgStr", "lastBillDate", "buyDeliveryMode",
				"buyTransportAmount", "buyWarehouseAmount", "buyMatchUserName", "sellContractNo", "sellCompanyName",
				"sellPrice", "sellTotalNumber", "sellTotalAmount", "invoiceBillNo", "sellContractDate",
				"receiveBillAmount", "sellDeliveryMode", "sellTransportAmount", "sellWarehouseAmount",
				"sellMatchUserName", "warehouseNumber", "profit" };
		int[] widths = new int[] { 15, 15, 20, 25, 15, 15, 15, 20, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15,
				20, 15, 15, 15, 15, 20, 15, 15, 15, 15, 15, 15 };
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
			PoiExcelUtil.createRows(sheet, pageVo.getContent(), attrs, start, cellStyle, DateOperator.FORMAT_STR);
			if (pageVo.hasNext()) {
				searchVo.setPage(searchVo.getPage() + 1);
				page = ctrContractAgencyClient.findPageAgency(searchVo);
				pageVo = preContractData(page);
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
	
	private Page<RptCtrContractAgencyReport> preContractData(Page<RptCtrContractAgencyReport> pageVo){
		if(pageVo != null && pageVo.getContent().size() > 0){
			for (RptCtrContractAgencyReport vo : pageVo.getContent()) {
				vo.setSource(DictUtil.getValue(BasConstants.APPLY_TYPE, vo.getSource()));
				vo.setBuyDeliveryMode(DictUtil.getValue(BasConstants.DICT_TYPE_DELIVERYMODE, vo.getBuyDeliveryMode()));
				vo.setSellDeliveryMode(DictUtil.getValue(BasConstants.DICT_TYPE_DELIVERYMODE, vo.getSellDeliveryMode()));
				if (vo.getBillFlg()) {
					vo.setBillFlgStr("已收票");
				}else {
					vo.setBillFlgStr("未收票");
				}
			}
		}
		return pageVo;
	}

}
