package com.spt.bas.web.controller.report;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.spt.auth.sdk.open.IAuthOpenFacade;
import com.spt.bas.client.cache.BsCompanyOurUtil;
import com.spt.bas.client.cache.BsDictUtil;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.constant.PermissionEnum;
import com.spt.bas.client.entity.BsDictData;
import com.spt.bas.client.remote.IBsCompanyOurClient;
import com.spt.bas.client.remote.ICtrContractClient;
import com.spt.bas.report.client.entity.RptNotBillStatistics;
import com.spt.bas.report.client.remote.IRptNotBillStatisticsClient;
import com.spt.bas.report.client.vo.RptNotBillStatisticsSearchVo;
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
import java.util.*;

/**
 * 未收票明细
 */
@Controller
@RequestMapping(value = "/rpt/notBillStatistics")
public class RptNotBillStatisticsController extends PageController<RptNotBillStatistics, BaseVo> {

	@Autowired
	private IRptNotBillStatisticsClient rptNotBillStatisticsClient;
	@Override
	public BaseClient<RptNotBillStatistics> getService() {
		return rptNotBillStatisticsClient;
	}


	@RequestMapping(value = "")
	public String index(Model model,HttpServletRequest request) {
		//我方抬头
		model.addAttribute("ourCompanyJson",
				JsonUtil.obj2Json(BsCompanyOurUtil.getCompanyOurToBsDictDataList()));
		// 获取当前日期
		Date currentDate = new Date();
		// 计算上月的开始日期
		Date lastMonthStart = DateUtil.beginOfMonth(DateUtil.offsetMonth(currentDate, 0));
		model.addAttribute("lastMonthStart",lastMonthStart);
		// 获取月份的最后一天
		DateTime lastMonthEnd = DateUtil.endOfMonth(lastMonthStart);
		model.addAttribute("lastMonthEnd",lastMonthEnd);
		
		return "report/notBillStatistics";
	}
	

	/**
	 * 未收票明细分页查询
	 * @param searchVo
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "findRptNotBillStatisticsPage")
	public void findContractFinancePage(RptNotBillStatisticsSearchVo searchVo, HttpServletRequest request, HttpServletResponse response) {
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
		
		PageDown<RptNotBillStatistics> page = rptNotBillStatisticsClient.findRptNotBillStatisticsPage(searchVo);
		page = preContractData(page);
		JsonEasyUI.renderJson(response, page, null, getFooter(searchVo));
	}
	
	public Map<String, Object> getFooter(RptNotBillStatisticsSearchVo searchVo){
		Map<String, Object> footer = new HashMap<>();
		RptNotBillStatistics sum = rptNotBillStatisticsClient.findRptNotBillStatisticsSum(searchVo);
		footer.put("buyContractNo", "合计");
		if (Objects.nonNull(sum)) {
			footer.put("totalNumber", sum.getTotalNumber());
			footer.put("buyBotalAmount", sum.getBuyTotalAmount());
			footer.put("payAmount", sum.getPayAmount());
			footer.put("receiptBillAmount", sum.getReceiptBillAmount());
		}
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
	public void exportExcel(RptNotBillStatisticsSearchVo searchVo, HttpServletRequest request, HttpServletResponse response) throws ApplicationException {
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
		
		PageDown<RptNotBillStatistics> page = rptNotBillStatisticsClient.findRptNotBillStatisticsPage(searchVo);
		String title = "未收票明细";

		String[] titles = new String[]{"合同编号", "我方抬头", "供应商名称", "业务类型", "合同数量", "合同总价", "签订日", "已付款金额","付全款日期","已收票金额","收票日期"};
		String[] attrs = new String[]{"buyContractNo", "ourCompanyName", "buyCompanyName","profitType", "totalNumber", "buyTotalAmount",
				"contractTime","payAmount","payFullDate","receiptBillAmount","receiptBillDate"};
		int[] widths = new int[]{20, 30, 30, 15, 20, 20, 20, 20, 20, 20, 20 };
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
				page = rptNotBillStatisticsClient.findRptNotBillStatisticsPage(searchVo);
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
	

	private PageDown<RptNotBillStatistics> preContractData(PageDown<RptNotBillStatistics> page){
		if (page != null && page.getContent().size() > 0) {
			for (RptNotBillStatistics notBillStatistics : page.getContent()) {
				String profitType = notBillStatistics.getProfitType();
				String profitTypeText = profitType;
				if (StringUtils.equals("1", profitType)) {
					profitTypeText = "赊销";
				} else if (StringUtils.equals("2", profitType)) {
					profitTypeText = "代采";
				} else if (StringUtils.equals("5", profitType)) {
					profitTypeText = "代采赊销";
				}
				notBillStatistics.setProfitType(profitTypeText);
			}
		}
		return page;
	}
	
}
