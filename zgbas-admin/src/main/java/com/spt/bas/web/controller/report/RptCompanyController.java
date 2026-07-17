package com.spt.bas.web.controller.report;


import com.spt.auth.sdk.cache.DictUtil;
import com.spt.auth.sdk.entity.SysDeptSdk;
import com.spt.auth.sdk.entity.SysUserSdk;
import com.spt.auth.sdk.open.IAuthOpenFacade;
import com.spt.bas.client.cache.BsCompanyOurUtil;
import com.spt.bas.client.cache.BsDictUtil;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.constant.PermissionEnum;
import com.spt.bas.client.entity.BsArea;
import com.spt.bas.client.entity.BsDictData;
import com.spt.bas.client.remote.IBsAreaClient;
import com.spt.bas.report.client.entity.RptCompany;
import com.spt.bas.report.client.remote.IRptCompanyClient;
import com.spt.bas.report.client.vo.RptCompanySearchVo;
import com.spt.bas.web.shiro.ShiroUtil;
import com.spt.bas.web.util.EasyTreeUtil2;
import com.spt.bas.web.util.ProvinceUtil;
import com.spt.bas.web.util.StringUtils;
import com.spt.bas.web.util.WebParamUtils;
import com.spt.pm.vo.PmProcessSearchVo;
import com.spt.tools.core.date.DateOperator;
import com.spt.tools.core.json.JsonUtil;
import com.spt.tools.data.easyui.EasyTreeNode;
import com.spt.tools.data.vo.PageDown;
import com.spt.tools.file.poi.PoiExcelUtil;
import com.spt.tools.web.util.JsonEasyUI;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Slf4j
@Controller
@RequestMapping(value = "/rpt/companyReport")
public class RptCompanyController {

	@Autowired
	private IRptCompanyClient rptCompanyClient;
	@Resource
	private WebParamUtils webParamUtils;
	@Autowired
	private IBsAreaClient areaClient;
	@Autowired
	private IAuthOpenFacade authOpenFacade;

//	@Override
//	public BaseClient<RptCompany> getService() {
//		return rptCompanyClient;
//	}


	//毛利率
	@RequestMapping(value = "companyAnalyse")
	public String companyAnalyse(Model model,HttpServletRequest request) {
		initData(model);
		model.addAttribute("companyStatus",
				JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_COMPANY_STATUS)));
		model.addAttribute("creditRating",
				JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_CREDITRATING)));// 信用等级
		model.addAttribute("companyType",
				JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_COMPANYTYPE)));// 客户分类
		model.addAttribute("companyGrade",
				JsonUtil.obj2Json(BsDictUtil.getListByCategory(ShiroUtil.getEnterpriseId(), BasConstants.DICT_TYPE_COMPANYGRADE)));// 客户分类
		model.addAttribute("onLineFlg",
				JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_ONLiNEFLG)));// 线上化查询
		List<BsArea> bsAreaLs = areaClient.findAll();
		model.addAttribute("areaJson", JsonUtil.obj2Json(bsAreaLs));

		model.addAttribute("matchUserId", request.getParameter("matchUserId"));
		model.addAttribute("deptId", request.getParameter("deptId"));
		model.addAttribute("permReportCompanyAnalyseExport", ShiroUtil.isPermitted(PermissionEnum.PERM_REPORT_COMPANY_ANALYSE_EXPORT.getPermissionCode()));

		return "report/company-analyse";
	}

	private Model initData(Model model){
		model.addAttribute("approveStatusJson", JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_APPROVESTATUS)));
		model.addAttribute("contractStatusJson",JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_CONTRACTSTATUS)));
		model.addAttribute("productTypeJson",JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_BUYPRODUCT)));
		model.addAttribute("deliveryTypeJson",JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_BUYDELIVERY)));
		model.addAttribute("applyTypeJson",JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_APPLYTYPE)));
		model.addAttribute("sellAndBuyStatusJson",JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_SELLSTATUS)));
		model.addAttribute("contractsTypeJson",JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_CONTRACTTYPES)));
		//model.addAttribute("deliveryModeJson",JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_DELIVERYMODE)));
		model.addAttribute("deliveryModeJson",JsonUtil.obj2Json(BsDictUtil.getListByCategory(ShiroUtil.getEnterpriseId(), BasConstants.DICT_TYPE_DELIVERYMODE)));
		model.addAttribute("contractAttrJson",JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_CONTRACTATTR)));
		model.addAttribute("ourCompanyJson",JsonUtil.obj2Json(BsCompanyOurUtil.getCompanyOurToBsDictDataList()));
		//业务小类
		model.addAttribute("businessTypeJson", JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_BUSINESSTYPE)));
		model.addAttribute("type","B");
		PmProcessSearchVo searchVo = new PmProcessSearchVo();
		searchVo.setEnterpriseId(ShiroUtil.getEnterpriseId());
		//获取业务员树
		List<SysDeptSdk> deptList = webParamUtils.getDeptAll();
		EasyTreeNode nodes = EasyTreeUtil2.getDeptTree(deptList, true,true);
		model.addAttribute("deptJson", JsonUtil.obj2Json(deptList));
		model.addAttribute("matchUserNameTree",JsonUtil.obj2Json(nodes.getChildren()));
		//确认收货权限
		Boolean confirmFlg = ShiroUtil.isPermitted(PermissionEnum.PERM_CTR_CONFIRM.getPermissionCode());
		model.addAttribute("confirmFlg",confirmFlg);
		//签约权限
		Boolean signingFlg = ShiroUtil.isPermitted(PermissionEnum.PERM_CTR_SIGNING.getPermissionCode());
		model.addAttribute("signingFlg", signingFlg);
		return model;
	}

	/**
	 * 客户分析表查询
	 * @param searchVo
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "findRptCompanyPage")
	public void findRptCompanyPage(RptCompanySearchVo searchVo, HttpServletRequest request, HttpServletResponse response){
		searchVo.setEnterpriseId(ShiroUtil.getEnterpriseId());
		List<BsDictData> listByCategory = BsDictUtil.getListByCategory(searchVo.getEnterpriseId(), BasConstants.REPORT_NOT_DEPT);
		List<Long> deptList = new ArrayList<>();
		if(!CollectionUtils.isEmpty(listByCategory)) {
			for (BsDictData bsDictData : listByCategory) {
				deptList.add(Long.valueOf(bsDictData.getDictCd()));
			}
		}
		if(!CollectionUtils.isEmpty(deptList)) {
			List<SysUserSdk> userList = authOpenFacade.findByDeptIds(deptList);
			List<Long> notUserIds = new ArrayList<>();
			if(!CollectionUtils.isEmpty(userList)) {
				for (SysUserSdk sysUserSdk : userList) {
					notUserIds.add(sysUserSdk.getUserId());
				}
				searchVo.setNotUserIds(notUserIds);
			}
		}
		List<Long> deptIdList = new ArrayList<>();
		List<BsDictData> dictDataList = BsDictUtil.getListByCategory(BasConstants.ZG_ENTERPRISE_ID, BasConstants.BRANCH_CD);
		Map<String, String> branchCdMap = dictDataList.stream().collect(Collectors.toMap(BsDictData::getDictCd, BsDictData::getRemark, (a, b) -> b));
		if (ShiroUtil.isPermitted(PermissionEnum.ZGBAS_BASECOST_HD.getPermissionCode())) {
			String deptIdStr = branchCdMap.get(BasConstants.HD);
			if (NumberUtils.isCreatable(deptIdStr)) {
				deptIdList.add(Long.valueOf(deptIdStr));
			}
		}
		if (ShiroUtil.isPermitted(PermissionEnum.ZGBAS_BASECOST_HB.getPermissionCode())) {
			String deptIdStr = branchCdMap.get(BasConstants.HB);
			if (NumberUtils.isCreatable(deptIdStr)) {
				deptIdList.add(Long.valueOf(deptIdStr));
			}
		}
		if (ShiroUtil.isPermitted(PermissionEnum.ZGBAS_BASECOST_HN.getPermissionCode())) {
			String deptIdStr = branchCdMap.get(BasConstants.HN);
			deptIdList.add(Long.valueOf(deptIdStr));
		}
		if (ShiroUtil.isPermitted(PermissionEnum.ZGBAS_BASECOST_HZ.getPermissionCode())) {
			String deptIdStr = branchCdMap.get(BasConstants.HZ);
			if (NumberUtils.isCreatable(deptIdStr)) {
				deptIdList.add(Long.valueOf(deptIdStr));
			}
		}
		if (ShiroUtil.isPermitted(PermissionEnum.ZGBAS_BASECOST_HDO.getPermissionCode())) {
			String deptIdStr = branchCdMap.get(BasConstants.HDO);
			if(StringUtils.isNotEmpty(deptIdStr)) {
				if (NumberUtils.isCreatable(deptIdStr)) {
					deptIdList.add(Long.valueOf(deptIdStr));
				}
			}
		}
		if (CollectionUtils.isNotEmpty(deptIdList)) {
			searchVo.setDeptIdList(deptIdList);
		} else {
			deptIdList.add(-1L);
			searchVo.setDeptIdList(deptIdList);
		}

		PageDown<RptCompany> page = rptCompanyClient.findRptCompanyPage(searchVo);
		Map<String, Object> footer = new HashMap<>();
		JsonEasyUI.renderJson(response, page,null,footer);
	}

	/**
	 * 客户分析表导出Exccel表格
	 * @param searchVo
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "/companyAnalyseexportExcel")
	@ResponseBody
	public void companyAnalyseexportExcel(RptCompanySearchVo searchVo, HttpServletRequest request, HttpServletResponse response) {
		int batchSize = 500;
		searchVo.setRows(batchSize);
		searchVo.setEnterpriseId(ShiroUtil.getEnterpriseId());
		List<BsDictData> listByCategory = BsDictUtil.getListByCategory(searchVo.getEnterpriseId(), BasConstants.REPORT_NOT_DEPT);
		List<Long> deptList = new ArrayList<>();
		if(!CollectionUtils.isEmpty(listByCategory)) {
			for (BsDictData bsDictData : listByCategory) {
				deptList.add(Long.valueOf(bsDictData.getDictCd()));
			}
		}
		if(!CollectionUtils.isEmpty(deptList)) {
			List<SysUserSdk> userList = authOpenFacade.findByDeptIds(deptList);
			List<Long> notUserIds = new ArrayList<>();
			if(!CollectionUtils.isEmpty(userList)) {
				for (SysUserSdk sysUserSdk : userList) {
					notUserIds.add(sysUserSdk.getUserId());
				}
				searchVo.setNotUserIds(notUserIds);
			}
		}
		List<Long> deptIdList = new ArrayList<>();
		List<BsDictData> dictDataList = BsDictUtil.getListByCategory(BasConstants.ZG_ENTERPRISE_ID, BasConstants.BRANCH_CD);
		Map<String, String> branchCdMap = dictDataList.stream().collect(Collectors.toMap(BsDictData::getDictCd, BsDictData::getRemark, (a, b) -> b));
		if (ShiroUtil.isPermitted(PermissionEnum.ZGBAS_BASECOST_HD.getPermissionCode())) {
			String deptIdStr = branchCdMap.get(BasConstants.HD);
			if (NumberUtils.isCreatable(deptIdStr)) {
				deptIdList.add(Long.valueOf(deptIdStr));
			}
		}
		if (ShiroUtil.isPermitted(PermissionEnum.ZGBAS_BASECOST_HB.getPermissionCode())) {
			String deptIdStr = branchCdMap.get(BasConstants.HB);
			if (NumberUtils.isCreatable(deptIdStr)) {
				deptIdList.add(Long.valueOf(deptIdStr));
			}
		}
		if (ShiroUtil.isPermitted(PermissionEnum.ZGBAS_BASECOST_HN.getPermissionCode())) {
			String deptIdStr = branchCdMap.get(BasConstants.HN);
			if (NumberUtils.isCreatable(deptIdStr)) {
				deptIdList.add(Long.valueOf(deptIdStr));
			}
		}
		if (ShiroUtil.isPermitted(PermissionEnum.ZGBAS_BASECOST_HZ.getPermissionCode())) {
			String deptIdStr = branchCdMap.get(BasConstants.HZ);
			if (NumberUtils.isCreatable(deptIdStr)) {
				deptIdList.add(Long.valueOf(deptIdStr));
			}
		}
		if (ShiroUtil.isPermitted(PermissionEnum.ZGBAS_BASECOST_HDO.getPermissionCode())) {
			String deptIdStr = branchCdMap.get(BasConstants.HDO);
			if(StringUtils.isNotEmpty(deptIdStr)) {
				if (NumberUtils.isCreatable(deptIdStr)) {
					deptIdList.add(Long.valueOf(deptIdStr));
				}
			}
		}
		if (CollectionUtils.isNotEmpty(deptIdList)) {
			searchVo.setDeptIdList(deptIdList);
		} else {
			deptIdList.add(-1L);
			searchVo.setDeptIdList(deptIdList);
		}

		PageDown<RptCompany> pageVo = rptCompanyClient.findRptCompanyPage(searchVo);
		preconditioningData(pageVo);
		String title = "客户分析报表查询";
		String[] titles = new String[] { "企业名称","区位（市/省）", "注册资本", "客户等级","客户分类", "人保额度(元)","中银额度(元)",
				"账期", "逾期天数","是否访厂", "开通小程序","是否诉讼", "是否连带","系统录入时间",
				"常用牌号", "交易单数","交易吨数(采购)", "采购单价","采购总价","交易吨数(销售)", "销售单价","销售总价",
				"加价", "毛利率(%)","最近成交时间","首次成交时间", "业务区域","业务员","开户人"
		};
		String[] attrs = new String[] { "companyName","provinceName", "registerCapital", "creditRatingName", "companyGrade","piccCreditAmount","zhongYinCreditAmount",
				"creditDays", "breachDays" ,"accessReportFlgStr", "onLineFlgStr" ,"legalFlgStr", "actualGuaranteeFlgStr" ,"createdDate",
				"commonBrandNumber", "tradeCount" ,"tradeTonnesBuy", "buyUnitPrice" ,"buyTotalAmount","tradeTonnesSell", "sellUnitPrice" ,"sellTotalAmount",
				"premium", "grossProfitMargin" ,"lastContractTime", "firstContractTime", "deptName" ,"matchUserName","ownerOfAccountName"
		};

		int[] widths = new int[] { 30, 15, 15, 15, 15, 15, 15,15, 15, 15, 15, 15, 15, 15,30, 15, 15, 15, 15, 15,15, 15, 15, 15, 15, 15, 20, 15, 15};
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
//		String[] firstTitles = new String[] { "客户基本信息","","","","风控信息", "成交", "归属"};
		String[] firstTitles = new String[] { "客户基本信息","", "", "风控信息","", "", "", "", "","", "","", "","", "成交", "","", "","", "","", "", "", "","","", "归属","", ""};
		// 设置第一行
		PoiExcelUtil.creatHeads(workbook, sheet, firstTitles, widthes);

		CellRangeAddress mergedRegion1 = new CellRangeAddress(0, 0, 0, 2);
		CellRangeAddress mergedRegion2 = new CellRangeAddress(0, 0, 3, 13);
		CellRangeAddress mergedRegion3 = new CellRangeAddress(0, 0, 14, 25);
		CellRangeAddress mergedRegion4 = new CellRangeAddress(0, 0, 26, 28);
		sheet.addMergedRegion(mergedRegion4);
		sheet.addMergedRegion(mergedRegion3);
		sheet.addMergedRegion(mergedRegion2);
		sheet.addMergedRegion(mergedRegion1);
		// 创建一个单元格样式对象
		CellStyle style = workbook.createCellStyle();
		// 设置单元格背景颜色
		style.setFillForegroundColor(IndexedColors.WHITE.getIndex());
		style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		// 设置水平居中
		style.setAlignment(HorizontalAlignment.CENTER);
		// 设置垂直居中
		style.setVerticalAlignment(VerticalAlignment.CENTER);
		// 设置文本颜色
		Font font = workbook.createFont();
		font.setColor(IndexedColors.GREEN.getIndex());
		font.setBold(true);
		style.setFont(font);

		CellStyle style2 = workbook.createCellStyle();
		// 设置单元格背景颜色
		style2.setFillForegroundColor(IndexedColors.WHITE.getIndex());
		style2.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		// 设置水平居中
		style2.setAlignment(HorizontalAlignment.CENTER);
		// 设置垂直居中
		style2.setVerticalAlignment(VerticalAlignment.CENTER);
		// 设置文本颜色为红色
		Font font2 = workbook.createFont();
		font2.setBold(true);
		style2.setFont(font2);


		Cell cell = sheet.getRow(0).getCell(0);
		Cell cell2 = sheet.getRow(0).getCell(3);
		Cell cell3 = sheet.getRow(0).getCell(14);
		Cell cell4 = sheet.getRow(0).getCell(25);
		cell.setCellStyle(style);
		cell2.setCellStyle(style2);
		cell3.setCellStyle(style2);
		cell4.setCellStyle(style2);


		PoiExcelUtil.createHeadsForstartRow(workbook, sheet, titles, widthes, 1);
		int start = 1;
		while (pageVo != null && pageVo.getContent().size() > 0) {
			PoiExcelUtil.createRows(sheet, pageVo.getContent(), attrs, start, cellStyle,
					DateOperator.FORMAT_STR_WITH_TIME);
			if (pageVo.hasNext()) {
				searchVo.setPage(searchVo.getPage() + 1);
				pageVo = rptCompanyClient.findRptCompanyPage(searchVo);
				preconditioningData(pageVo);
				start += batchSize;
			} else {
				pageVo = null;
			}
		}

		try {
			PoiExcelUtil.write(workbook, response, title);
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		}

	}

	private void preconditioningData(PageDown<RptCompany> pageVo){
		for (RptCompany rptCompany : pageVo.getContent()) {
			SysDeptSdk sysDept = webParamUtils.getDeptById(rptCompany.getDeptId());
			/* 部门名称 */
			if (sysDept != null){
				rptCompany.setDeptName(sysDept.getDeptName());
			} else{
				rptCompany.setDeptName("");
			}
			BigDecimal grossProfitMargin = rptCompany.getGrossProfitMargin();//毛利

			if(grossProfitMargin != null) {
				if (grossProfitMargin.compareTo(BigDecimal.ZERO)==0){//如果净毛利为0的情况下不执行除法
					BigDecimal decimal = new BigDecimal("0");
					rptCompany.setGrossProfitMargin(decimal);//毛利率
				}else{
					BigDecimal num1 = new BigDecimal("100");
					BigDecimal num2 = grossProfitMargin.multiply(num1);//BigDecimal乘法
					rptCompany.setGrossProfitMargin(num2);//毛利率
				}
			}
			String provinceCode = rptCompany.getProvinceCode();
			if(StringUtils.isNotEmpty(provinceCode)) {
				rptCompany.setProvinceName(ProvinceUtil.getProvinceName(provinceCode));
			}
			String creditRating = rptCompany.getCreditRating();
			if(StringUtils.equals("W",creditRating)) {
				rptCompany.setCreditRatingName("白名单");
			} else if(StringUtils.equals("G",creditRating)) {
				rptCompany.setCreditRatingName("灰名单");
			} else if(StringUtils.equals("B",creditRating)) {
				rptCompany.setCreditRatingName("黑名单");
			}
			/* 是否线上化 */
			Boolean onLineFlg = rptCompany.getOnLineFlg();
			if (onLineFlg != null && Boolean.TRUE.equals(onLineFlg)) {
				rptCompany.setOnLineFlgStr("是");
			} else {
				rptCompany.setOnLineFlgStr("否");
			}
			Boolean accessReportFlg = rptCompany.getAccessReportFlg();
			if (accessReportFlg != null && Boolean.TRUE.equals(accessReportFlg)) {
				rptCompany.setAccessReportFlgStr("是");
			} else {
				rptCompany.setAccessReportFlgStr("否");
			}
			Boolean legalFlg = rptCompany.getLegalFlg();
			if (legalFlg != null && Boolean.TRUE.equals(legalFlg)) {
				rptCompany.setLegalFlgStr("是");
			} else {
				rptCompany.setLegalFlgStr("否");
			}
			Boolean actualGuaranteeFlg = rptCompany.getActualGuaranteeFlg();
			if (actualGuaranteeFlg != null && Boolean.TRUE.equals(actualGuaranteeFlg)) {
				rptCompany.setActualGuaranteeFlgStr("是");
			} else {
				rptCompany.setActualGuaranteeFlgStr("否");
			}
		}
	}

}
