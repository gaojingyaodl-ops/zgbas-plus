package com.spt.bas.web.controller.report;


import com.spt.bas.client.cache.BsCompanyOurUtil;
import com.spt.bas.client.cache.BsDictUtil;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.BsDictData;
import com.spt.bas.report.client.vo.RptCtrContractFinanceSearch;
import com.spt.bas.report.client.remote.IRptCtrContractFinanceClient;
import com.spt.bas.report.client.vo.RptCtrContractFinanceVo;
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
import java.util.List;
import java.util.stream.Collectors;

/**
 * 自营审核
 */
@Controller
@RequestMapping(value = "/rpt/contractFinance")
public class RptContractFinanceController extends PageController<RptCtrContractFinanceVo, BaseVo> {

	@Autowired
	private IRptCtrContractFinanceClient ctrContractFinanceClient;
	@Override
	public BaseClient<RptCtrContractFinanceVo> getService() {
		return ctrContractFinanceClient;
	}

	@RequestMapping(value = "")
	public String assessment(Model model,HttpServletRequest request) {

		model.addAttribute("statisticalTypeJson",
				JsonUtil.obj2Json(BsDictUtil.getListByCategory(ShiroUtil.getEnterpriseId(), BasConstants.DICT_STATISTICAL_TYPE)));
		List<BsDictData> companyOurToBsDictDataList = BsCompanyOurUtil.getCompanyOurToBsDictDataList();
		//我方抬头
		model.addAttribute("ourCompanyJson",
				JsonUtil.obj2Json(companyOurToBsDictDataList));
		model.addAttribute("statisticsType", request.getParameter("statisticsType"));

		model.addAttribute("businessType", request.getParameter("businessType"));

		model.addAttribute("companyName", request.getParameter("companyName"));
		model.addAttribute("contractTimeBegin", request.getParameter("contractDateBegin"));
		model.addAttribute("contractTimeEnd", request.getParameter("contractDateEnd"));
		model.addAttribute("contractType", request.getParameter("contractType"));
		model.addAttribute("contractStatus", request.getParameter("contractStatus"));
		model.addAttribute("productType", request.getParameter("productType"));

		String ourCompanyCd = request.getParameter("ourCompanyCd");

		model.addAttribute("ourCompanyCd", ourCompanyCd);
		if (StringUtils.isEmpty(ourCompanyCd)) {
			String allOurCompanyFlag = request.getParameter("allOurCompanyFlag");
			if (StringUtils.equals("true",allOurCompanyFlag)) {
				List<BsDictData> companyOurFalagList = BsCompanyOurUtil.getCompanyOurFlagToBsDictDataList();
				List<String> ourCompanyNames = companyOurFalagList.stream()
						.map(data -> data.getDictName().trim()) // 去除每个 dictName 两端的空格
						.collect(Collectors.toList());
				model.addAttribute("ourCompanyCd", ourCompanyNames);
			}
		}

		
		
		return "report/ctrContractFinance";
	}

	// 自营考核统计
	@RequestMapping(value = "findContractFinancePage")
	public void findContractFinancePage(RptCtrContractFinanceSearch searchVo, HttpServletRequest request, HttpServletResponse response) {

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

		PageDown<RptCtrContractFinanceVo> page = ctrContractFinanceClient.findContractFinancePage(searchVo);
		JsonEasyUI.renderJson(response, page, null, null);
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
	public void exportExcel(RptCtrContractFinanceSearch searchVo, HttpServletRequest request, HttpServletResponse response) throws ApplicationException {
		initSearch(searchVo, request);
		int batchSize = 500;
		searchVo.setRows(batchSize);
		PageDown<RptCtrContractFinanceVo> page = ctrContractFinanceClient.findContractFinancePage(searchVo);
		page = preContractData(page);
		String title = "财务合同统计";

		String[] titles = new String[]{"合同编号", "业务类型", "货名", "对方企业名称", "合同数量(吨)", "合同总价(元)", "签订日", "我方抬头", "业务员", "区域",
				"已收/付款金额", "收/付全款日期", "已开/收票金额", "开/收票日期"};
		String[] attrs = new String[]{"contractNo", "businessType", "productName", "companyName", "totalNumber",
				"totalAmount", "contractTime", "ourCompanyName", "matchUserName", "deptName", "dealedAmount", "payFullTime", "billedAmount", "realBillDate"};
		int[] widths = new int[]{20, 15, 15, 20, 20, 10, 10, 15, 15, 20, 15, 15, 20, 15};
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
				page = ctrContractFinanceClient.findContractFinancePage(searchVo);
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

	private PageDown<RptCtrContractFinanceVo> preContractData(PageDown<RptCtrContractFinanceVo> page){
		if (page != null && page.getContent().size() > 0) {
			for (RptCtrContractFinanceVo contractShowVo : page.getContent()) {
				String businessType = contractShowVo.getBusinessType();
				Boolean matchCreditFlg = contractShowVo.getMatchCreditFlg();
				String businessTypeDcsxString;
				if (businessType.equals("ZY-BB")){
					if (matchCreditFlg){
						businessTypeDcsxString = "赊销";
					}else{
						businessTypeDcsxString = "代采";
					}
					contractShowVo.setBusinessType(businessTypeDcsxString);
				}
			}
		}
		return page;
	}
}
