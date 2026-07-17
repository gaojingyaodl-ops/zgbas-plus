package com.spt.bas.web.controller.ctr;

import com.spt.auth.sdk.cache.DictUtil;
import com.spt.auth.sdk.entity.SysDeptSdk;
import com.spt.bas.client.cache.BsCompanyOurUtil;
import com.spt.bas.client.cache.BsDictUtil;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.constant.PermissionEnum;
import com.spt.bas.client.entity.CtrContract;
import com.spt.bas.client.remote.ICtrContractClient;
import com.spt.bas.client.vo.ContractSearchVo;
import com.spt.bas.client.vo.ContractShowVo;
import com.spt.bas.report.client.entity.RptCtrContractSellOnCreditReport;
import com.spt.bas.report.client.remote.IRptCtrContractSellOnCreditClient;
import com.spt.bas.report.client.vo.RptSellOnCreditSearchVo;
import com.spt.bas.web.shiro.ShiroUtil;
import com.spt.bas.web.util.EasyTreeUtil2;
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
import org.apache.commons.lang3.StringUtils;
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
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/***
 * 赊销合同
 * 
 *
 */

@Controller
@RequestMapping(value = "/ctr/sellOnCredit")
public class ctrContractSellOnCreditController extends PageController<CtrContract, BaseVo>{
	@Autowired
	private ICtrContractClient ctrContractClient;
	@Resource
	private WebParamUtils webParamUtils;
	@Autowired
	private IRptCtrContractSellOnCreditClient ctrContractSellOnCreditClient;

	@Override
	public BaseClient<CtrContract> getService() {
		return ctrContractClient;
	}
	
	@RequestMapping(value = "")
	public String index(Model model) {
		//获取业务员树
		List<SysDeptSdk> deptList = webParamUtils.getDeptAll();
		EasyTreeNode nodes = EasyTreeUtil2.getDeptTree(deptList, true,true);
		model.addAttribute("deptListJson", JsonUtil.obj2Json(deptList));
		model.addAttribute("matchUserNameTree",JsonUtil.obj2Json(nodes.getChildren()));
		model.addAttribute("approveStatusJson",
				JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_APPROVESTATUS))); //审批状态
		model.addAttribute("applyTypeJson",
				JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_APPLYTYPE))); //业务类型
		model.addAttribute("contractStatusJson",
				JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_CONTRACTSTATUS))); //合同状态
		model.addAttribute("ourCompanyJson", JsonUtil.obj2Json(
				BsCompanyOurUtil.getCompanyOurToBsDictDataList())); //我方企业名
		//业务大类
		model.addAttribute("businessJson", JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_BUSINESS)));
		//业务小类
		model.addAttribute("businessTypeJson", JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_BUSINESSTYPE)));
		//交货方式
		model.addAttribute("deliveryModeJson",
				JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_DELIVERYMODE)));
		model.addAttribute("enterpriseId",ShiroUtil.getEnterpriseId());
		return "ctr/ctrContractSellOnCredit";
	}
	
	@RequestMapping(value = "findSellOnCredit")
	public void findSellOnCredit(RptSellOnCreditSearchVo searchVo, HttpServletRequest request, HttpServletResponse response) {
		searchVo.setEnterpriseId(ShiroUtil.getEnterpriseId());
		PageDown<RptCtrContractSellOnCreditReport> page = ctrContractSellOnCreditClient.findPageSellOnCredit(searchVo);
		List<SysDeptSdk> deptList = webParamUtils.getDeptAll();
		if (page != null && page.getContent().size() > 0) {
			for (RptCtrContractSellOnCreditReport reportVo : page.getContent()) {
				for (SysDeptSdk sysDept : deptList) {
					if(reportVo.getDeptId().equals(sysDept.getDeptId())){
						reportVo.setDeptName(sysDept.getDeptName()); //事业部
					}
				}
			}
		}
		JsonEasyUI.renderJson(response, page);
	}
	
	@RequestMapping(value = "/exportExcels")
	@ResponseBody
	public void exportExcels(ContractSearchVo searchVo, HttpServletRequest request, HttpServletResponse response)
			throws ApplicationException {
		initSearch(searchVo, request);
		int batchSize = 500;
		searchVo.setRows(batchSize);
		searchVo.setUserId(ShiroUtil.getCurrentUserId());
		Long deptLeader = webParamUtils.getDeptLeader();
		searchVo.setDeptLeaderId(deptLeader);
		Boolean piccRemainCredit = searchVo.getPiccRemainCredit();
		Map<String, Object> searchParams = searchVo.getSearchParams();
		if (piccRemainCredit != null) {
			if (piccRemainCredit) {
				searchParams.put("GTEM_piccRemainCredit", BigDecimal.ZERO);
			}else {
				searchParams.put("LTM_piccRemainCredit", BigDecimal.ZERO);
			}
		}
		if (ShiroUtil.isPermitted(PermissionEnum.PERM_CTR_VIEWALL.getPermissionCode())) {
			searchVo.setAdmin(true);
		}
		if (ShiroUtil.isPermitted(PermissionEnum.PERM_CTR_VIEWPRESELL.getPermissionCode())) {
			searchVo.setSearchType("P");
		}
		if (ShiroUtil.isPermitted(PermissionEnum.APPROVE_VIEW_ALL.getPermissionCode())) {
			searchVo.setSearchType("A");
		}
		Page<ContractShowVo> page = ctrContractClient.findPageContract(searchVo);
		Page<ContractShowVo> pageVo = preContractData(page);
		String title = "赊销合同";

		String[] titles = new String[] { "业务类型", "合同编号", "货品", "我方抬头", "对方企业名称", "交货方式", "合同数量(吨)", "出库数量(吨)",
				"合同总价(元)", "已收金额(元)", "开票金额(元)", "合同状态", "收款日期", "收定金日期", "合同时间", "确认收货时间", "人保投保状态","人保可用额度(元)", "人保投保信息", "回款状态",
				"人保回款信息", "业务员", "事业部" };
		String[] attrs = new String[] { "businessType", "contractNo", "productsName", "ourCompanyName", "companyName",
				"deliveryMode", "totalNumber", "warehouseNumber", "totalAmount", "dealedAmount", "billedAmount",
				"contractStatus", "lastPayDate", "payBondTime", "contractTime", "confirmDate", "piccPushFlgStr","piccRemainCredit",
				"piccMessage", "piccReceiveFlgStr", "piccReceiveMessage", "matchUserName","deptName"};
		int[] widths = new int[] { 15, 15, 15, 20, 20, 20, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15,15, 40, 15, 40, 15,15 };
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
			PoiExcelUtil.createRows(sheet, pageVo.getContent(), attrs, start, cellStyle,
					DateOperator.FORMAT_STR_WITH_TIME);
			if (pageVo.hasNext()) {
				searchVo.setPage(searchVo.getPage() + 1);
				page = ctrContractClient.findPageContract(searchVo);
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
	
	private Page<ContractShowVo> preContractData(Page<ContractShowVo> pageVo){
		if(pageVo!=null && pageVo.getContent().size()>0){
			List<SysDeptSdk> deptList = webParamUtils.getDeptAll();
			for (ContractShowVo contractShowVo : pageVo.getContent()) {
				String deliveryMode = contractShowVo.getDeliveryMode();
				Boolean piccPushFlg = contractShowVo.getPiccPushFlg();
				Boolean piccReceiveFlg = contractShowVo.getPiccReceiveFlg();
				contractShowVo.setBusinessType(DictUtil.getValue(BasConstants.DICT_TYPE_BUSINESSTYPE, contractShowVo.getBusinessType()));
				contractShowVo.setSource(DictUtil.getValue(BasConstants.APPLY_TYPE, contractShowVo.getSource()));
				contractShowVo.setContractStatus(
						DictUtil.getValue(BasConstants.DICT_TYPE_CONTRACTSTATUS, contractShowVo.getContractStatus()));
				contractShowVo.setContractAttr(
						DictUtil.getValue(BasConstants.STOCK__CONTRACT_ATTR, contractShowVo.getContractAttr()));
				String value = BsDictUtil.getValue(ShiroUtil.getEnterpriseId(), BasConstants.DICT_TYPE_DELIVERYMODE, deliveryMode);
				contractShowVo.setDeliveryMode(value);
				if (StringUtils.isBlank(contractShowVo.getPiccMessage())) {
					contractShowVo.setPiccPushFlg(null);
				}else if (piccPushFlg != null && piccPushFlg) {
					contractShowVo.setPiccPushFlgStr("投保成功");
				}else {
					contractShowVo.setPiccPushFlgStr("投保失败");
				}
				if (StringUtils.isBlank(contractShowVo.getPiccReceiveMessage())) {
					contractShowVo.setPiccReceiveFlg(null);
				}else if (piccReceiveFlg != null && piccReceiveFlg) {
					contractShowVo.setPiccReceiveFlgStr("回款成功");
				}else {
					contractShowVo.setPiccReceiveFlgStr("回款失败");
				}
				if (contractShowVo.getDeptId() != null) {
					for (SysDeptSdk sysDept : deptList) {
						if (contractShowVo.getDeptId().equals(sysDept.getDeptId())) {
							contractShowVo.setDeptName(sysDept.getDeptName());
							break;
						}
					}
				}				
			}
		}
		return pageVo;
	}
}
