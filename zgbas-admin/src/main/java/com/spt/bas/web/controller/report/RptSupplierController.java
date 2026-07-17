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
import com.spt.bas.report.client.entity.RptSupplier;
import com.spt.bas.report.client.remote.IRptSupplierClient;
import com.spt.bas.report.client.vo.RptCompanySearchVo;
import com.spt.bas.web.shiro.ShiroUtil;
import com.spt.bas.web.util.EasyTreeUtil2;
import com.spt.bas.web.util.ProvinceUtil;
import com.spt.bas.web.util.StringUtils;
import com.spt.bas.web.util.WebParamUtils;
import com.spt.pm.vo.PmProcessSearchVo;
import com.spt.tools.core.date.DateOperator;
import com.spt.tools.core.exception.ApplicationException;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Slf4j
@Controller
@RequestMapping(value = "/rpt/supplierReport")
public class RptSupplierController {
	
	@Autowired
	private IRptSupplierClient rptSupplierClient;
	@Resource
	private WebParamUtils webParamUtils;
	@Autowired
	private IBsAreaClient areaClient;
	@Autowired
	private IAuthOpenFacade authOpenFacade;

	@RequestMapping(value = "supplierAnalyse")
	public String companyAnalyse(Model model) {
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
		model.addAttribute("supplierLevelJson",
				JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_SUPPLIERLEVEL)));// 线上化查询
		List<BsArea> bsAreaLs = areaClient.findAll();
		model.addAttribute("areaJson", JsonUtil.obj2Json(bsAreaLs));
		model.addAttribute("permReportSupplierAnalyseExport", ShiroUtil.isPermitted(PermissionEnum.PERM_REPORT_SUPPLIIER_ANALYSE_EXPORT.getPermissionCode()));
		return "report/supplier-analyse";
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
	@RequestMapping(value = "findRptSupplierPage")
	public void findRptSupplierPage(RptCompanySearchVo searchVo, HttpServletRequest request, HttpServletResponse response){
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
		PageDown<RptSupplier> page = rptSupplierClient.findRptSupplierPage(searchVo);
		Map<String, Object> footer = new HashMap<>();
		JsonEasyUI.renderJson(response, page,null,footer);
	}

	/**
	 * 供应商分析表导出Exccel表格
	 * @param searchVo
	 * @param request
	 * @param response
	 * @throws ApplicationException
	 */
	@RequestMapping(value = "/supplierAnalyseexportExcel")
	@ResponseBody
	public void supplierAnalyseexportExcel(RptCompanySearchVo searchVo, HttpServletRequest request, HttpServletResponse response)
			throws ApplicationException {
		int batchSize = 500;
		searchVo.setRows(batchSize);
		searchVo.setEnterpriseId(ShiroUtil.getEnterpriseId());
		
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
		
		PageDown<RptSupplier> pageVo = rptSupplierClient.findRptSupplierPage(searchVo);
		String title = "供应商分析报表查询";
		List<RptSupplier> content = pageVo.getContent();
		for(int i=0;i<content.size();i++){
			RptSupplier rptSupplier = content.get(i);
			SysDeptSdk sysDept = webParamUtils.getDeptById(rptSupplier.getDeptId());
			/* 部门名称 */
			if (sysDept != null){
				rptSupplier.setDeptName(sysDept.getDeptName());
			}
			else{
				rptSupplier.setDeptName("");
			}
			
			String provinceCode = rptSupplier.getProvinceCode();
			if(StringUtils.isNotEmpty(provinceCode)) {
				rptSupplier.setProvinceName(ProvinceUtil.getProvinceName(provinceCode));
			}
			String creditRating = rptSupplier.getSupplierRating();
			if(StringUtils.equals("W",creditRating)) {
				rptSupplier.setSupplierRatingName("白名单");
			} else if(StringUtils.equals("G",creditRating)) {
				rptSupplier.setSupplierRatingName("灰名单");
			} else if(StringUtils.equals("B",creditRating)) {
				rptSupplier.setSupplierRatingName("黑名单");
			}
//			String supplierLevel = rptSupplier.getSupplierLevel();
//			if(StringUtils.equals("A",supplierLevel)) {
//				rptSupplier.setSupplierRatingName("A级-龙头供应商");
//			} else if(StringUtils.equals("B",supplierLevel)) {
//				rptSupplier.setSupplierRatingName("B级-扶持供应商");
//			} else if(StringUtils.equals("C",supplierLevel)) {
//				rptSupplier.setSupplierRatingName("C级-小规模供应商");
//			}
			String supplierDelivery = rptSupplier.getSupplierDelivery();
			if(StringUtils.equals("1",supplierDelivery)) {
				rptSupplier.setSupplierDelivery("是");
			} else {
				rptSupplier.setSupplierDelivery("否");
			} 
		}


		String[] titles = new String[] { "客户名称","区位（市/省）", "注册资本", "供应商等级","供应商级别","系统录入时间",
				"供应商配送","常用牌号", "交易单数","交易吨数", "交易单价","交易总价", 
				"加价", "最近成交时间", "业务区域","业务员"
		};
		String[] attrs = new String[] { "companyName","provinceName", "registerCapital", "supplierRatingName", "supplierGrade", "createdDate",
				"supplierDelivery","commonBrandNumber", "tradeCount" ,"tradeTonnes", "buyUnitPrice" ,"buyTotalAmount", 
				"premium" ,"lastContractTime", "deptName" ,"matchUserName"
		};

		int[] widths = new int[] { 30, 15, 15, 15, 15, 15, 15, 30, 15, 15, 15, 15, 15, 15, 20, 15};
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
		String[] firstTitles = new String[] { "供应商基本信息","", "", "","", "", "", "交易信息","", "","", "","", "", "",""};
		// 设置第一行
		PoiExcelUtil.creatHeads(workbook, sheet, firstTitles, widthes);

		CellRangeAddress mergedRegion1 = new CellRangeAddress(0, 0, 0, 6);
		CellRangeAddress mergedRegion2 = new CellRangeAddress(0, 0, 7, 15);
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
		Cell cell2 = sheet.getRow(0).getCell(7);
		cell.setCellStyle(style);
		cell2.setCellStyle(style2);
		
		PoiExcelUtil.createHeadsForstartRow(workbook, sheet, titles, widthes,1);
		int start = 1;
		while (pageVo != null && pageVo.getContent().size() > 0) {
			PoiExcelUtil.createRows(sheet, pageVo.getContent(), attrs, start, cellStyle,
					DateOperator.FORMAT_STR_WITH_TIME);
			if (pageVo.hasNext()) {
				searchVo.setPage(searchVo.getPage() + 1);
				pageVo = rptSupplierClient.findRptSupplierPage(searchVo);
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
	
	
}
