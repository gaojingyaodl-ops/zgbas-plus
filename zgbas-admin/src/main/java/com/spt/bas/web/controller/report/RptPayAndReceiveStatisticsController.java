package com.spt.bas.web.controller.report;

import com.spt.auth.sdk.cache.DictUtil;
import com.spt.bas.client.cache.BsCompanyOurUtil;
import com.spt.bas.client.cache.BsDictUtil;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.CtrContract;
import com.spt.bas.client.remote.ICtrContractClient;
import com.spt.bas.client.remote.IPmProcessClient;
import com.spt.bas.report.client.entity.RptCtrContractPayAndReceiveReport;
import com.spt.bas.report.client.remote.IRptCtrContractPayAndReceiveClient;
import com.spt.bas.report.client.vo.RptPayAndReceiveSearchVo;
import com.spt.bas.web.shiro.ShiroUtil;
import com.spt.bas.web.util.WebParamUtils;
import com.spt.pm.entity.PmProcess;
import com.spt.pm.vo.PmProcessSearchVo;
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
 * 应付/应收统计表
 * 
 * @author zhoukun
 */

@Controller
@RequestMapping(value = "/rpt/payandreceivestatistics")
public class RptPayAndReceiveStatisticsController extends PageController<CtrContract, BaseVo>{
	
	@Autowired
	private ICtrContractClient ctrContractClient;
	@Resource
	private WebParamUtils webParamUtils;
	@Autowired
	private IPmProcessClient processClient;
	@Autowired
	private IRptCtrContractPayAndReceiveClient ctrContractPayAndReceiveClient;

	@Override
	public BaseClient<CtrContract> getService() {
		
		return ctrContractClient;
	}
	
	
	@RequestMapping(value = "pay")
	public String pay(Model model) {
		model.addAttribute("approveStatusJson",
				JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_APPROVESTATUS)));
		model.addAttribute("contractStatusJson",
				JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_CONTRACTSTATUS)));
		model.addAttribute("productTypeJson",
				JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_BUYPRODUCT)));
		model.addAttribute("deliveryTypeJson",
				JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_BUYDELIVERY)));
		model.addAttribute("applyTypeJson",
				JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_APPLYTYPE)));
		model.addAttribute("contractsTypeJson",
				JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_CONTRACTTYPES)));
		model.addAttribute("deliveryModeJson",
				JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_DELIVERYMODE)));
		model.addAttribute("ourCompanyJson", JsonUtil.obj2Json(
				BsCompanyOurUtil.getCompanyOurToBsDictDataList()));
		model.addAttribute("shouldPayJson", JsonUtil.obj2Json(
				DictUtil.getListByCategory(BasConstants.DICT_TYPE_SHOULD_PAY_TYPE)));
		//交货方式
		model.addAttribute("deliveryMode", JsonUtil.obj2Json(
				BsDictUtil.getListByCategory(ShiroUtil.getEnterpriseId(), BasConstants.TEMPLATE_CONTENT_DELIVERYMODE)));
		model.addAttribute("businessTypeJson", JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_BUSINESSTYPE)));
		model.addAttribute("type", "B");
		PmProcessSearchVo searchVo = new PmProcessSearchVo();
		searchVo.setEnterpriseId(ShiroUtil.getEnterpriseId());
		List<PmProcess> processList = processClient.findByEnterpriseId(searchVo);
		model.addAttribute("processListJson", JsonUtil.obj2Json(processList));
		// 获取业务员树
		EasyTreeNode nodes = webParamUtils.getDeptEasyTreeNode(true);
		model.addAttribute("matchUserNameTree", JsonUtil.obj2Json(nodes.getChildren()));
		return "report/payAndReceiveStatistics";
	}
	@RequestMapping(value = "receive")
	public String receive(Model model) {
		model.addAttribute("approveStatusJson",
				JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_APPROVESTATUS)));
		model.addAttribute("contractStatusJson",
				JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_CONTRACTSTATUS)));
		model.addAttribute("productTypeJson",
				JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_BUYPRODUCT)));
		model.addAttribute("deliveryTypeJson",
				JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_BUYDELIVERY)));
		model.addAttribute("applyTypeJson",
				JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_APPLYTYPE)));
		model.addAttribute("contractsTypeJson",
				JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_CONTRACTTYPES)));
		model.addAttribute("deliveryModeJson",
				JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_DELIVERYMODE)));
		model.addAttribute("ourCompanyJson", JsonUtil.obj2Json(
				BsCompanyOurUtil.getCompanyOurToBsDictDataList()));
		model.addAttribute("shouldPayJson", JsonUtil.obj2Json(
				DictUtil.getListByCategory(BasConstants.DICT_TYPE_SHOULD_PAY_TYPE)));
		model.addAttribute("type", "S");
		//应收业务类型
		model.addAttribute("businessTypeJson", JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_BUSINESSTYPE)));
		
		PmProcessSearchVo searchVo = new PmProcessSearchVo();
		searchVo.setEnterpriseId(ShiroUtil.getEnterpriseId());
		List<PmProcess> processList = processClient.findByEnterpriseId(searchVo);
		model.addAttribute("processListJson", JsonUtil.obj2Json(processList));
		// 获取业务员树
		EasyTreeNode nodes = webParamUtils.getDeptEasyTreeNode(true);
		model.addAttribute("matchUserNameTree", JsonUtil.obj2Json(nodes.getChildren()));
	
		return "report/payAndReceiveStatistics";
	}
	@RequestMapping(value = "findPay")
	public void findPay(RptPayAndReceiveSearchVo searchVo, HttpServletRequest request, HttpServletResponse response) {
		searchVo.setEnterpriseId(ShiroUtil.getEnterpriseId());
		PageDown<RptCtrContractPayAndReceiveReport> page = ctrContractPayAndReceiveClient.findPagePay(searchVo);
		RptCtrContractPayAndReceiveReport total = ctrContractPayAndReceiveClient.findPayTotalPage(searchVo);
		Map<String, Object> footer = new HashMap<>();
		footer.put("companyName", "合计");
		footer.put("totalNumber", total.getTotalNumber());
		footer.put("totalAmount", total.getTotalAmount());
		footer.put("dealedAmount", total.getDealedAmount());
		footer.put("shouldAmount", total.getShouldAmount());
		JsonEasyUI.renderJson(response, page, null, footer);
	}
	
	@RequestMapping(value = "findReceive")
	public void findAgency(RptPayAndReceiveSearchVo searchVo, HttpServletRequest request, HttpServletResponse response) {
//		if(StringUtils.equals(BasConstants.CONTRACTTYPE_SELL, searchVo.getContractType())) {
//			searchVo.setBusinessType(searchVo.getSource());
//		}
		searchVo.setEnterpriseId(ShiroUtil.getEnterpriseId());
		PageDown<RptCtrContractPayAndReceiveReport> page = ctrContractPayAndReceiveClient.findPageReceive(searchVo);
		RptCtrContractPayAndReceiveReport total = ctrContractPayAndReceiveClient.findPageReceiveSum(searchVo);
		Map<String, Object> footer = new HashMap<>();
		//footer.put("deliveryMode", "合计");
		footer.put("contractNo", "合计");
		footer.put("totalNumber", total.getSumTotalNumber());
		footer.put("totalAmount", total.getSumTotalAmount());
		footer.put("warehouseNumber", total.getSumWarehouseNumber());
		footer.put("confirmReceiveNumber", total.getSumConfirmReceiveNumber());
		footer.put("receiveAmount", total.getSumReceiveAmount());	//收款
		footer.put("dealedAmount", total.getSumDealedAmount());	//已收
		footer.put("shouldAmount", total.getSumShouldAmount());
		
		JsonEasyUI.renderJson(response, page, null, footer);
	}
	/**
	 * 应付/应收Excel导出
	 * @param searchVo
	 * @param request
	 * @param response
	 * @throws ApplicationException
	 */
	@RequestMapping(value = "/exportExcel")
	@ResponseBody
	public void exportExcel(RptPayAndReceiveSearchVo searchVo, HttpServletRequest request, HttpServletResponse response)
			throws ApplicationException {
		initSearch(searchVo, request);
		int batchSize = 500;
		searchVo.setRows(batchSize);
		searchVo.setEnterpriseId(ShiroUtil.getEnterpriseId());
		PageDown<RptCtrContractPayAndReceiveReport> page = ctrContractPayAndReceiveClient.findPageReceive(searchVo);
		PageDown<RptCtrContractPayAndReceiveReport> pageVo = preDeliveryReprtData(page);
		String contractType = searchVo.getSearchParams().get("EQS_contractType").toString();
		String title = "应收统计表";
		String dealedAmount = "已收金额(元)";
		String billedAmount = "开票金额(元)";
//		String payBondTime = "收定金日期";
//		String payFullTime = "收全款日期";
		String[] titles;
		String[] attrs;
		int[] widths;
		if (contractType.equals(BasConstants.CONTRACT_TYPE_B)) {
			title = "应付统计表";
			dealedAmount = "已付金额(元)";
			billedAmount = "收票金额(元)";
//			payBondTime = "付定金日期";
//			payFullTime = "付全款日期";
			page = ctrContractPayAndReceiveClient.findPagePay(searchVo);
			pageVo = preDeliveryReprtData(page);
			titles = new String[] { "业务类型", "合同编号", "货品","我方抬头", "对方企业名称", "合同数量(吨)", "定金(元)", "合同总价(元)",
					dealedAmount, billedAmount, "合同状态", "全款时间", "定金时间", "合同时间","应付金额(元)","应付款类型","应付款日期", "业务员" };
			
			attrs = new String[] { "businessType", "contractNo", "productsName", "ourCompanyName", "companyName",
					"totalNumber", "bondAmount", "totalAmount", "dealedAmount", "billedAmount", "contractStatus",
					"payFullTime", "payBondTime", "contractTime", "shouldAmount", "shouldPayType", "shouldPayDate",
					"matchUserName" };
			widths = new int[] { 15, 15, 25, 20, 20, 15, 15, 15, 15, 15, 15, 20, 20, 20, 15, 15, 15, 15 };
			} else {
				titles = new String[] { "业务类型", "合同编号", "货品","我方抬头", "对方企业名称", "合同数量(吨)", "合同总价(元)","合同状态", "发货(吨)", "最后一次发货时间",
						"收货确认(吨)","最后一次收货确认时间",dealedAmount, "最后一次收款时间", "合同时间", "业务员" };
				
				attrs = new String[] { "businessType", "contractNo", "productsName","ourCompanyName","companyName", "totalNumber","totalAmount","contractStatus","warehouseNumber","realWarehoseDate",
						"confirmReceiveNumber","confirmDate","receiveAmount","receiveDate","contractTime","matchUserName"};
				widths= new int[] { 15, 15, 25, 20, 20, 15,15, 15, 15, 15, 15, 20, 20, 20, 15,15 };
			}
		
		
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
				page = ctrContractPayAndReceiveClient.findPagePay(searchVo);
				start += batchSize;
				pageVo = preDeliveryReprtData(page);
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
	private PageDown<RptCtrContractPayAndReceiveReport> preDeliveryReprtData(PageDown<RptCtrContractPayAndReceiveReport> page) {
		if (page != null && page.getContent().size() > 0) {
			for (RptCtrContractPayAndReceiveReport vo : page.getContent()) {
				vo.setBusinessType(DictUtil.getValue(BasConstants.DICT_TYPE_BUSINESSTYPE, vo.getBusinessType()));
				vo.setContractStatus(DictUtil.getValue(BasConstants.DICT_TYPE_CONTRACTSTATUS, vo.getContractStatus()));
				vo.setShouldPayType(DictUtil.getValue(BasConstants.DICT_TYPE_SHOULD_PAY_TYPE, vo.getShouldPayType()));
			}
		}
		return page;
	}
}
