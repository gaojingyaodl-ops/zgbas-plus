package com.spt.bas.web.controller.report;

import com.spt.auth.sdk.open.IAuthOpenFacade;
import com.spt.bas.client.cache.BsCompanyOurUtil;
import com.spt.bas.client.cache.BsDictUtil;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.constant.PermissionEnum;
import com.spt.bas.client.entity.BsDictData;
import com.spt.bas.client.remote.IBsCompanyOurClient;
import com.spt.bas.client.remote.ICtrContractClient;
import com.spt.bas.report.client.entity.RptCompanyReceivables;
import com.spt.bas.report.client.remote.IRptCompanyReceivablesClient;
import com.spt.bas.report.client.vo.RptCompanyReceivablesSearchVo;
import com.spt.bas.web.shiro.ShiroUtil;
import com.spt.bas.web.util.StringUtils;
import com.spt.tools.core.date.DateOperator;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.core.json.JsonUtil;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.data.vo.BaseVo;
import com.spt.tools.data.vo.PageDown;
import com.spt.tools.file.poi.PoiExcelUtil;
import com.spt.tools.web.controller.PageController;
import com.spt.tools.web.util.JsonEasyUI;
import org.apache.commons.collections.CollectionUtils;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 客户应收款
 */
@Controller
@RequestMapping(value = "/rpt/companyReceivables")
public class RptCompanyReceivablesController extends PageController<RptCompanyReceivables, BaseVo> {

	@Autowired
	private IRptCompanyReceivablesClient rptCompanyReceivablesClient;
	@Autowired
	private ICtrContractClient ctrContractClient;
	@Override
	public BaseClient<RptCompanyReceivables> getService() {
		return rptCompanyReceivablesClient;
	}
	@Autowired
	private IBsCompanyOurClient bsCompanyOurClient;
	@Autowired
	private IAuthOpenFacade authOpenFacade;

	@RequestMapping(value = "")
	public String index(Model model,HttpServletRequest request) {
		//我方抬头
		model.addAttribute("ourCompanyJson",
				JsonUtil.obj2Json(BsCompanyOurUtil.getCompanyOurToBsDictDataList()));
		
		return "report/companyReceivables";
	}

	@RequestMapping(value = "detail")
	public String detail(Model model,HttpServletRequest request) {
		//我方抬头
		model.addAttribute("ourCompanyJson",
				JsonUtil.obj2Json(BsCompanyOurUtil.getCompanyOurToBsDictDataList()));

		model.addAttribute("ourCompanyName",request.getParameter("ourCompanyName"));
		model.addAttribute("companyName",request.getParameter("companyName"));
		model.addAttribute("productType",request.getParameter("productType"));

		return "report/companyReceivablesDetail";
	}

	/**
	 * 开票信息分页查询
	 * @param searchVo
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "findRptCompanyReceivablesPage")
	public void findContractFinancePage(RptCompanyReceivablesSearchVo searchVo, HttpServletRequest request, HttpServletResponse response) {
		List<BsDictData> listByCategory = BsDictUtil.getListByCategory(ShiroUtil.getEnterpriseId(), BasConstants.DICT_HG_MATCH_USER_IDS);
		List<Long> hgMatchUserIdList = new ArrayList<>();
		if (CollectionUtils.isNotEmpty(listByCategory)) {
			for (BsDictData bsDictData : listByCategory) {
				try {
					String dictCd = bsDictData.getDictCd();
					Long matchUserId = Long.valueOf(dictCd);
					hgMatchUserIdList.add(matchUserId);
				} catch (Exception e) {
				}
			}
		}
		searchVo.setHgMatchUserIdList(hgMatchUserIdList);
		if (ShiroUtil.isPermitted(PermissionEnum.ZGBAS_NEW_USER_FUNDER.getPermissionCode())) {
			searchVo.setFunderFlg(true);
			searchVo.setUserId(ShiroUtil.getCurrentUserId());
		}
		
		PageDown<RptCompanyReceivables> page = rptCompanyReceivablesClient.findRptCompanyReceivablesPage(searchVo);
		JsonEasyUI.renderJson(response, page, null, getFooter(searchVo));
	}
	
	/**
	 * 开票信息分页查询
	 * @param searchVo
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "findRptCompanyReceivablesDetailPage")
	public void findRptCompanyReceivablesDetailPage(RptCompanyReceivablesSearchVo searchVo, HttpServletRequest request, HttpServletResponse response) {
		List<BsDictData> listByCategory = BsDictUtil.getListByCategory(ShiroUtil.getEnterpriseId(), BasConstants.DICT_HG_MATCH_USER_IDS);
		List<Long> hgMatchUserIdList = new ArrayList<>();
		if (CollectionUtils.isNotEmpty(listByCategory)) {
			for (BsDictData bsDictData : listByCategory) {
				try {
					String dictCd = bsDictData.getDictCd();
					Long matchUserId = Long.valueOf(dictCd);
					hgMatchUserIdList.add(matchUserId);
				} catch (Exception e) {
				}
			}
		}
		searchVo.setHgMatchUserIdList(hgMatchUserIdList);
		if (ShiroUtil.isPermitted(PermissionEnum.ZGBAS_NEW_USER_FUNDER.getPermissionCode())) {
			searchVo.setFunderFlg(true);
			searchVo.setUserId(ShiroUtil.getCurrentUserId());
		}
		
		PageDown<RptCompanyReceivables> page = rptCompanyReceivablesClient.findRptCompanyReceivablesDetailPage(searchVo);
		JsonEasyUI.renderJson(response, page, null, getFooter(searchVo));
	}
	
	public Map<String, Object> getFooter(RptCompanyReceivablesSearchVo searchVo){
		Map<String, Object> footer = new HashMap<>();
		RptCompanyReceivables sum = rptCompanyReceivablesClient.findRptCompanyReceivablesSum(searchVo);
		footer.put("ourCompanyName", "合计");
		footer.put("tradeTonnes", sum.getTradeTonnes());
		footer.put("totalAmount", sum.getTotalAmount());
		footer.put("tradeCount", sum.getTradeCount());
		footer.put("dealedAmount", sum.getDealedAmount());
		footer.put("receivablePrincipal", sum.getReceivablePrincipal());
		footer.put("breachAmount", sum.getBreachAmount());
		footer.put("receiveBreachAmount", sum.getReceiveBreachAmount());
		footer.put("receivableBreachAmount", sum.getReceivableBreachAmount());
		return footer;
	}

	/**
	 * EXCEL导出
	 * @param searchVo
	 * @param request
	 * @param response
	 * @throws ApplicationException
	 */
	@RequestMapping(value = "/exportExcel")
	@ResponseBody
	public void exportExcel(RptCompanyReceivablesSearchVo searchVo, HttpServletRequest request, HttpServletResponse response) throws ApplicationException {
		List<BsDictData> listByCategory = BsDictUtil.getListByCategory(ShiroUtil.getEnterpriseId(), BasConstants.DICT_HG_MATCH_USER_IDS);
		List<Long> hgMatchUserIdList = new ArrayList<>();
		if (CollectionUtils.isNotEmpty(listByCategory)) {
			for (BsDictData bsDictData : listByCategory) {
				try {
					String dictCd = bsDictData.getDictCd();
					Long matchUserId = Long.valueOf(dictCd);
					hgMatchUserIdList.add(matchUserId);
				} catch (Exception e) {
				}
			}
		}
		searchVo.setHgMatchUserIdList(hgMatchUserIdList);
		if (ShiroUtil.isPermitted(PermissionEnum.ZGBAS_NEW_USER_FUNDER.getPermissionCode())) {
			searchVo.setFunderFlg(true);
			searchVo.setUserId(ShiroUtil.getCurrentUserId());
		}
		
		int batchSize = 500;
		searchVo.setRows(batchSize);
		
		PageDown<RptCompanyReceivables> page = rptCompanyReceivablesClient.findRptCompanyReceivablesPage(searchVo);
		String title = "客户应收款统计";

		String[] titles = new String[]{"我方抬头", "企业名称", "交易吨数", "销售总价", "交易单数","应收本金", "应收罚息"};
		String[] attrs = new String[]{"ourCompanyName", "companyName", "tradeTonnes", "totalAmount", "tradeCount","receivablePrincipal","receivableBreachAmount"};
		int[] widths = new int[]{30, 30, 15, 20, 15, 20, 20};
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
				page = rptCompanyReceivablesClient.findRptCompanyReceivablesPage(searchVo);
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
	
	/**
	 * EXCEL导出明细
	 * @param searchVo
	 * @param request
	 * @param response
	 * @throws ApplicationException
	 */
	@RequestMapping(value = "/exportExcelDetail")
	@ResponseBody
	public void exportExcelDetail(RptCompanyReceivablesSearchVo searchVo, HttpServletRequest request, HttpServletResponse response) throws ApplicationException {
		List<BsDictData> listByCategory = BsDictUtil.getListByCategory(ShiroUtil.getEnterpriseId(), BasConstants.DICT_HG_MATCH_USER_IDS);
		List<Long> hgMatchUserIdList = new ArrayList<>();
		if (CollectionUtils.isNotEmpty(listByCategory)) {
			for (BsDictData bsDictData : listByCategory) {
				try {
					String dictCd = bsDictData.getDictCd();
					Long matchUserId = Long.valueOf(dictCd);
					hgMatchUserIdList.add(matchUserId);
				} catch (Exception e) {
				}
			}
		}
		searchVo.setHgMatchUserIdList(hgMatchUserIdList);
		if (ShiroUtil.isPermitted(PermissionEnum.ZGBAS_NEW_USER_FUNDER.getPermissionCode())) {
			searchVo.setFunderFlg(true);
			searchVo.setUserId(ShiroUtil.getCurrentUserId());
		}
		
		int batchSize = 500;
		searchVo.setRows(batchSize);
		
		PageDown<RptCompanyReceivables> page = rptCompanyReceivablesClient.findRptCompanyReceivablesDetailPage(searchVo);
		page = preContractData(page);
		String title = "客户应收款统计明细";

		String[] titles = new String[]{"合同编号", "业务类型", "产品名称", "我方抬头", "企业名称", "双签日期", "交易吨数", "销售总价"
				, "应收本金", "已收本金", "罚息", "应收罚息", "已收罚息", "业务员", "区域"};
		String[] attrs = new String[]{"contractNo", "businessType", "productName", "ourCompanyName", "companyName", "sealDate",
				"tradeTonnes", "totalAmount", "receivablePrincipal", "dealedAmount", "breachAmount", "receivableBreachAmount",
				"receiveBreachAmount", "matchUserName", "deptName"};
		int[] widths = new int[]{20, 20, 30, 30, 30, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20};
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
				page = rptCompanyReceivablesClient.findRptCompanyReceivablesDetailPage(searchVo);
				page = preContractData(page);
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

	private PageDown<RptCompanyReceivables> preContractData(PageDown<RptCompanyReceivables> page){
		if (page != null && page.getContent().size() > 0) {
			for (RptCompanyReceivables receivables : page.getContent()) {
				String businessType = receivables.getBusinessType();
				Boolean matchCreditFlg = receivables.getMatchCreditFlg();
				String businessTypeDcsxString;
				if (StringUtils.equals(businessType, "ZY-BB")) {
					if (matchCreditFlg){
						businessTypeDcsxString = "赊销";
					}else{
						businessTypeDcsxString = "代采";
					}
					receivables.setBusinessType(businessTypeDcsxString);
				}
			}
		}
		return page;
	}
	
}
