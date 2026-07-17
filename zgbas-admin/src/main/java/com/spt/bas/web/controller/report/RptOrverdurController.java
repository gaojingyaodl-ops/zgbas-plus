package com.spt.bas.web.controller.report;

import com.spt.auth.sdk.cache.DictUtil;
import com.spt.auth.sdk.entity.SysDeptSdk;
import com.spt.bas.client.cache.BsCompanyOurUtil;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.constant.PermissionEnum;
import com.spt.bas.client.entity.CtrContract;
import com.spt.bas.client.entity.CtrContractFollow;
import com.spt.bas.client.remote.ICtrContractClient;
import com.spt.bas.client.remote.ICtrContractFollowClient;
import com.spt.bas.client.remote.IPmProcessClient;
import com.spt.bas.report.client.entity.RptCtrContractOrverdur;
import com.spt.bas.report.client.remote.IRptCtrContractOrverdurClient;
import com.spt.bas.web.shiro.ShiroUtil;
import com.spt.bas.web.util.EasyTreeUtil2;
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
import com.spt.tools.web.util.RenderUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * 逾期收/付款报表
 */
@Controller
@RequestMapping(value = "/rpt/orverdur")
public class RptOrverdurController extends PageController<CtrContract, BaseVo> {
	@Resource
	private WebParamUtils webParamUtils;
	@Autowired
	private IPmProcessClient processClient;
	@Autowired
	private ICtrContractClient ctrContractClient;
	@Autowired
	private ICtrContractFollowClient ctrContractFollowClient;
	@Autowired
	private IRptCtrContractOrverdurClient ctrContractOrverdurClient;
	@Override
	public BaseClient<CtrContract> getService() {
		return ctrContractClient;
	}

	@RequestMapping(value = "pay")
	public String pay(Model model) {
		model = initData(model);
		model.addAttribute("type", "B");
		model.addAttribute("searchType", "pay");
		return "ctr/contract-orverdur";
	}

	@RequestMapping(value = "receive")
	public String receive(Model model) {
		model = initData(model);
		model.addAttribute("type", "S");
		model.addAttribute("searchType", "receive");
		return "ctr/contract-orverdur";
	}
	
	@RequestMapping(value = "invoice")
	public String invoice(Model model) {
		model = initData(model);
		model.addAttribute("type", "S");
		model.addAttribute("searchType", "invoice");
		return "ctr/contract-orverdur";
	}

	private Model initData(Model model){
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
		model.addAttribute("sellAndBuyStatusJson",
				JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_BUYSTATUS)));
		model.addAttribute("contractsTypeJson",
				JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_CONTRACTTYPES)));
		model.addAttribute("deliveryModeJson",
				JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_DELIVERYMODE)));
		model.addAttribute("ourCompanyJson", JsonUtil.obj2Json(
				BsCompanyOurUtil.getCompanyOurToBsDictDataList()));
		model.addAttribute("businessTypeJson",
				JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_BUSINESSTYPE)));
		//企业流程列表
		PmProcessSearchVo searchVo = new PmProcessSearchVo();
		searchVo.setEnterpriseId(ShiroUtil.getEnterpriseId());
		List<PmProcess> processList = processClient.findByEnterpriseId(searchVo);
		model.addAttribute("processListJson", JsonUtil.obj2Json(processList));
		// 获取业务员树
		List<SysDeptSdk> deptList = webParamUtils.getDeptAll();
		EasyTreeNode nodes = EasyTreeUtil2.getDeptTree(deptList, true);
		model.addAttribute("deptJson", JsonUtil.obj2Json(deptList));
		model.addAttribute("matchUserNameTree", JsonUtil.obj2Json(nodes.getChildren()));
		// 逾期通知权限
		model.addAttribute("canNotify", ShiroUtil.isPermitted(PermissionEnum.PERM_CTR_CAN_NOTIFY.getPermissionCode()));
		return model;
	}
	
	@RequestMapping(value = "contractList")
	public void contractList(RptCtrContractOrverdur searchVo, HttpServletRequest request, HttpServletResponse response) {
		searchVo.setEnterpriseId(ShiroUtil.getEnterpriseId());
		//searchVo.setDeptIds(deptIds);
		PageDown<RptCtrContractOrverdur> findPageOrverdur = ctrContractOrverdurClient.findPageOrverdur(searchVo);
		RptCtrContractOrverdur sum = ctrContractOrverdurClient.findPageTotal(searchVo);
		Map<String, Object> footer = new HashMap<>();
		footer.put("companyName", "合计");
		footer.put("totalNumber", sum.getTotalNumber());
		footer.put("warehouseNumber", sum.getWarehouseNumber());
		footer.put("bondAmount", sum.getBondAmount());
		footer.put("totalAmount", sum.getTotalAmount());
		footer.put("dealedAmount", sum.getDealedAmount());
		footer.put("billedAmount", sum.getBilledAmount());
		footer.put("orverdurAmount", sum.getOrverdurAmount());
		JsonEasyUI.renderJson(response, findPageOrverdur,null,footer);
	}

	/**
	 * 添加逾期 通知功能
	 */
	@RequestMapping(value = "notify", method = RequestMethod.POST)
	public void notify(HttpServletRequest request, HttpServletResponse response) {
		try {
			String id = request.getParameter("id");
			if (StringUtils.isNotBlank(id)) {
				CtrContractFollow follow = new CtrContractFollow();
				follow.setCtrContractId(Long.valueOf(id));
				follow.setCreateUserId(ShiroUtil.getCurrentUserId());
				follow.setCreateUserName(ShiroUtil.getCurrentUserName());
				ctrContractFollowClient.toNotify(follow);
				RenderUtil.renderText("success", response);
			}
		} catch (Exception e) {
			e.printStackTrace();
			RenderUtil.renderText("fail", response);
		}
	}
	
	/**
	 * 逾期收付款表Excel导出
	 * @param searchVo
	 * @param request
	 * @param response
	 * @throws ApplicationException
	 */
	@RequestMapping(value = "/exportExcel")
	@ResponseBody
	public void exportExcel(RptCtrContractOrverdur searchVo, HttpServletRequest request, HttpServletResponse response)
			throws ApplicationException {
		int batchSize = 500;
		initSearch(searchVo, request);
		searchVo.setEnterpriseId(ShiroUtil.getEnterpriseId());
		searchVo.setRows(batchSize);
		//searchVo.setDeptIds(deptIds);
		PageDown<RptCtrContractOrverdur> page = ctrContractOrverdurClient.findPageOrverdur(searchVo);
		Page<RptCtrContractOrverdur> pageVo = preDeliveryReprtData(page);
		String contractType = searchVo.getContractType();
		String title = "逾期收款表";
		String warehouseNumber = "出库数量(吨)";
		String dealedAmount = "已收金额(元)";
		String billedAmount = "开票金额(元)";
		String payBondTime = "收定金日期";
		String payFullTime = "收款日期";
		if (contractType.equals(BasConstants.CONTRACT_TYPE_B)) {
			title = "逾期付款表";
			warehouseNumber = "入库数量(吨)";
			dealedAmount = "已付金额(元)";
			billedAmount = "收票金额(元)";
			payBondTime = "付定金日期";
			payFullTime = "付款日期";
		}
		String[] titles = new String[] { "业务类型", "合同编号", "货品","我方抬头", "对方企业名称","交货方式", "合同数量(吨)", warehouseNumber, "定金(元)", "合同总价(元)",
				dealedAmount, billedAmount,"逾期金额", "合同状态", payFullTime, payBondTime, "合同时间", "业务员" ,"事业部"};
		String[] attrs = new String[] { "businessType", "contractNo", "productsName","ourCompanyName", "companyName","deliveryMode", "totalNumber",
				"warehouseNumber", "bondAmount", "totalAmount", "dealedAmount", "billedAmount", "orverdurAmount","contractStatus",
				"payFullTime", "payBondTime", "contractTime", "matchUserName" ,"deptName"};
		int[] widths = new int[] { 15, 15, 25,20, 20,15, 15, 15, 15, 15,15, 15, 15, 15, 20, 20, 20, 15 ,15};
		Workbook workbook = PoiExcelUtil.newWorkbook(PoiExcelUtil.WB_TYPE_2007);
		// 生成一个表格
		Sheet sheet = workbook.createSheet(title);
		// 设置表格默认列宽度为 16 个字节
		sheet.setDefaultColumnWidth(16);
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
				page = ctrContractOrverdurClient.findPageOrverdur(searchVo);
				pageVo = preDeliveryReprtData(page);
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

	private Page<RptCtrContractOrverdur> preDeliveryReprtData(Page<RptCtrContractOrverdur> pageVo) {
		List<SysDeptSdk> deptList = webParamUtils.getDeptAll();
		if (pageVo != null && pageVo.getContent().size() > 0) {
			for (RptCtrContractOrverdur orverdurVo : pageVo.getContent()) {
				orverdurVo.setSource(DictUtil.getValue(BasConstants.DICT_TYPE_BUSINESSTYPE, orverdurVo.getBusinessType()));
				orverdurVo.setDeliveryMode(DictUtil.getValue(BasConstants.DICT_TYPE_DELIVERYMODE, orverdurVo.getDeliveryMode()));
				orverdurVo.setContractStatus(DictUtil.getValue(BasConstants.DICT_TYPE_CONTRACTSTATUS, orverdurVo.getContractStatus()));
				for (SysDeptSdk sysDept : deptList) {
					if(orverdurVo.getDeptId().equals(sysDept.getDeptId())){
						orverdurVo.setDeptName(sysDept.getDeptName());
					}
				}
			}
		}
		return pageVo;
	}
}
