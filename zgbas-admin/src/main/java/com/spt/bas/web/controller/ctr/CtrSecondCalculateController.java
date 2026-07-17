//package com.spt.bas.web.controller.ctr;
//
//import com.alibaba.fastjson.JSON;
//import com.beust.jcommander.internal.Maps;
//import com.spt.auth.sdk.cache.DictUtil;
//import com.spt.auth.sdk.entity.SysDeptSdk;
//import com.spt.auth.sdk.open.IAuthOpenFacade;
//import com.spt.auth.sdk.vo.DeptSearchVo;
//import com.spt.bas.client.cache.BsCompanyOurUtil;
//import com.spt.bas.client.cache.BsDictUtil;
//import com.spt.bas.client.constant.BasConstants;
//import com.spt.bas.client.entity.*;
//import com.spt.bas.client.remote.*;
//import com.spt.bas.client.vo.*;
//import com.spt.bas.web.shiro.ShiroUtil;
//import com.spt.bas.web.util.EasyTreeUtil2;
//import com.spt.pm.entity.PmApprove;
//import com.spt.pm.vo.PmPermissionVo;
//import com.spt.tools.core.exception.InvalidParamException;
//import com.spt.tools.core.json.JsonUtil;
//import com.spt.tools.data.easyui.EasyTreeNode;
//import com.spt.tools.data.service.BaseClient;
//import com.spt.tools.data.vo.BaseVo;
//import com.spt.tools.web.controller.PageController;
//import com.spt.tools.web.util.JsonEasyUI;
//import com.spt.tools.web.util.RenderUtil;
//import org.apache.commons.lang3.StringUtils;
//import org.springframework.beans.BeanUtils;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestMethod;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.util.List;
//import java.util.Map;
///**
// * 二次结算
// */
//@Controller
//@RequestMapping(value = "/ctr/secondCalculate")
//public class CtrSecondCalculateController extends PageController<CtrContract, BaseVo>{
//	@Autowired
//	private ICtrContractClient ctrContractClient;
//	@Autowired
//	private IApplyImportClient applyImportClient;
//	@Autowired
//	private IApplyImportDetailClient applyImportDetailClient;
//	@Autowired
//	private IApplyMatchClient applyMatchClient;
//	@Autowired
//	private IApplyMatchDetailClient applyMatchDetailClient;
//	@Autowired
//	private IPmApproveClient pmApproveClient;
//	@Autowired
//	private IBsProductTypeClient bsProductTypeClient;
//	@Autowired
//	private IAuthOpenFacade authOpenFacade;
//	@Autowired
//	private IBsFactoryClient factoryClient;
//	@Autowired
//	private IApplyCalculateClient applyCalculateClient;
//	@Override
//	public BaseClient<CtrContract> getService() {
//		return ctrContractClient;
//	}
//
//	@RequestMapping("")
//	public String Index(Model model) {
//		//业务小类
//		model.addAttribute("businessTypeJson",
//				JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_BUSINESSTYPE)));
//		model.addAttribute("contractTypeJson",
//				JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_CONTRACTTYPE)));
//		model.addAttribute("contractStatusJson",
//				JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_CONTRACTSTATUS)));
//		model.addAttribute("productTypeJson",
//				JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_BUYPRODUCT)));
//		model.addAttribute("applyTypeJson",
//				JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_APPLYTYPE)));
//		model.addAttribute("deliveryModeJson", JsonUtil.obj2Json(
//				BsDictUtil.getListByCategory(ShiroUtil.getEnterpriseId(), BasConstants.DICT_TYPE_DELIVERYMODE)));
//		// 货品树
//		List<EasyTreeNode> productTree = bsProductTypeClient.findAllProductTree(ShiroUtil.getEnterpriseId());
//		model.addAttribute("productTypeJson", JsonUtil.obj2Json(productTree));
//		// 获取业务员树
//		DeptSearchVo deptSearchVo = new DeptSearchVo( ShiroUtil.getEnterpriseId());
//		List<SysDeptSdk> deptList = authOpenFacade.findDeptAll(deptSearchVo);
//		EasyTreeNode nodes = EasyTreeUtil2.getDeptTree(deptList, true);
//		model.addAttribute("matchUserNameTree", JsonUtil.obj2Json(nodes.getChildren()));
//		boolean bkbAuditFlg = ShiroUtil.isPermitted(BasConstants.PERM_SECOND_BKB_AUDIT);
//		boolean dlkzAuditFlg = ShiroUtil.isPermitted(BasConstants.PERM_SECOND_DLKZ_AUDIT);
//		boolean dldcAuditFLg = ShiroUtil.isPermitted(BasConstants.PERM_SECOND_DLDC_AUDIT);
//		boolean dlkzApplyFLg = ShiroUtil.isPermitted(BasConstants.PERM_SECOND_DLKZ_APPLY);
//		boolean dldcApplyFLg = ShiroUtil.isPermitted(BasConstants.PERM_SECOND_DLDC_APPLY);
//		model.addAttribute("bkbAuditFlg", bkbAuditFlg);		//背靠背审核
//		model.addAttribute("dlkzAuditFlg", dlkzAuditFlg);	//代理开证审核
//		model.addAttribute("dldcAuditFLg", dldcAuditFLg);	//国企代采审核
//		model.addAttribute("dlkzApplyFLg", dlkzApplyFLg);	//代理开证申请
//		model.addAttribute("dldcApplyFLg", dldcApplyFLg);	//国企代采申请
//		model.addAttribute("bizUserId", ShiroUtil.getCurrentUserId());
//		return "ctr/calculate";
//	}
//
//	@RequestMapping("detail")
//	public String detail(Model model,HttpServletRequest request) {
//		String contractNo = request.getParameter("contractNo");
//		String type = request.getParameter("type");
//		String calculateNo = request.getParameter("calculateNo");
//		//货品树
//		List<EasyTreeNode> productTree = bsProductTypeClient.findAllProductTree(ShiroUtil.getEnterpriseId());
//		model.addAttribute("productAllJson", JsonUtil.obj2Json(productTree));
//		model.addAttribute("productChildrenJson",
//				JsonUtil.obj2Json(bsProductTypeClient.findAll()));
//		//支付方式
//		model.addAttribute("payTypeJson",
//				JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_APPLY_PAYMODE)));
//		//提货方式
//		model.addAttribute("deliveryTypeJson",
//				JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_BUYDELIVERY)));
//		//销售方式
//		model.addAttribute("deliveryModeJson", JsonUtil.obj2Json(
//				BsDictUtil.getListByCategory(ShiroUtil.getEnterpriseId(), BasConstants.DICT_TYPE_DELIVERYMODE)));
//		//企业抬头
//		model.addAttribute("ourCompanyNameJson",
//				JsonUtil.obj2Json(BsCompanyOurUtil.getCompanyOurToBsDictDataList()));
//		//厂商
//		List<BsFactory> lstFactory = factoryClient.findByEnterpriseId(ShiroUtil.getEnterpriseId());
//		model.addAttribute("factoryJson", JsonUtil.obj2Json(lstFactory));
//		//合同类型
//		model.addAttribute("contractTypeJson",JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_CONTRACTTYPE)));
//		//合同属性
//		model.addAttribute("contractAttr",
//				JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.STOCK__CONTRACT_ATTR)));
//		//包装规格
//		model.addAttribute("packingSpecificaJson",
//				JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_IMPORTBUYPACKING)));
//		// 质量标准
//		model.addAttribute("qualityStandardJson",
//				JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_QUALITYSTANDDARD)));
//		// 开票时间
//		model.addAttribute("invoiceDateJson",
//				JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_INVOICEDATE)));
//		// 交货时间的补充字段
//		model.addAttribute("arrivalTimeExtJson",
//				JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_ATTACHDELIVERYTIME)));
//		//处理审批中部分控件可编辑
//		PmPermissionVo pmPermissionVo = new PmPermissionVo();
//		pmPermissionVo.setCanApproveEdit(false);
//		pmPermissionVo.setHasApprove(false);
//		pmPermissionVo.setHasEdit(false);
//		model.addAttribute("psv", pmPermissionVo);
//		CtrContract contract = new CtrContract();
//		contract.setContractNo(contractNo);
//		contract = ctrContractClient.findByContractNo(contract);
//		PmApprove pmApprove = pmApproveClient.getEntity(contract.getApproveId());
//		String businessType = contract.getBusinessType();
//		if (StringUtils.equals(BasConstants.APPLY_TYPE_CC, type)) {
//			model.addAttribute("culculateType", type);
//			model.addAttribute("calculateNo", calculateNo);
//		}
//		//国企代采
//		if (StringUtils.equals(BasConstants.BUSINESS_TYPE_DL_DC ,businessType) || StringUtils.equals(BasConstants.BUSINESS_TYPE_ZY_BB ,businessType)) {
//			ApplyMatch applyMatch = applyMatchClient.getEntity(pmApprove.getBizId());
//			ApplyMatchQueryVo vo = new ApplyMatchQueryVo();
//			vo.setApplyMatchId(applyMatch.getId());
//			vo.setContractType(BasConstants.CONTRACTTYPE_SELL);
//			List<ApplyMatchDetail> sellList= applyMatchDetailClient.findByApplyQueryVo(vo);
//			vo.setContractType(BasConstants.CONTRACTTYPE_BUY);
//			List<ApplyMatchDetail> buyList= applyMatchDetailClient.findByApplyQueryVo(vo);
//			model.addAttribute("buyProductDetailList", buyList);
//			model.addAttribute("sellProductDetailList", sellList);
//			model.addAttribute("entity", applyMatch);
//			return "ctr/calculateMatch-detail";
//		}else {
//			//代理开证
//			ApplyImport applyImport = applyImportClient.getEntity(pmApprove.getBizId());
//			ApplyImportQueryVo vo = new ApplyImportQueryVo();
//			vo.setApplyImportId(applyImport.getId());
//			vo.setContractType(BasConstants.CONTRACTTYPE_SELL);
//			List<ApplyImportDetail> sellList = applyImportDetailClient.findByApplyQueryVo(vo);
//			sellList = getImportCalculateDetail(sellList,calculateNo);
//			vo.setContractType(BasConstants.CONTRACTTYPE_BUY);
//			List<ApplyImportDetail> buyList = applyImportDetailClient.findByApplyQueryVo(vo);
//			buyList = getImportCalculateDetail(buyList, calculateNo);
//			model.addAttribute("buyProductDetailList", buyList);
//			model.addAttribute("sellProductDetailList", sellList);
//			model.addAttribute("entity", applyImport);
//			return "ctr/calculateImport-detail";
//		}
//	}
//
//	/**
//	 * 保存二次结算申请
//	 * @param applyCalculate
//	 * @param request
//	 * @param response
//	 */
//	@RequestMapping(value = "saveDetail", method = RequestMethod.POST)
//	public void saveDetail(ApplyCalculateVo applyCalculate,HttpServletRequest request, HttpServletResponse response) {
//		List<ApplyProductDetail> lstUpdate = null;
//		String data = applyCalculate.getContentStr();
//		List<ApplyCalculateDetailVo> list = JSON.parseArray(data, ApplyCalculateDetailVo.class);
//		for(ApplyCalculateDetailVo calculateDetailVo : list){
//			String randomNumber = calculateDetailVo.getRandomNumber();
//			lstUpdate = JsonEasyUI.getUpdatedRecords(ApplyProductDetail.class, randomNumber, request);
//			calculateDetailVo.setLstUpdate(lstUpdate);
//			calculateDetailVo.setBizUserId(ShiroUtil.getCurrentUserId());
//			calculateDetailVo.setBizUserName(ShiroUtil.getCurrentUserName());
//			calculateDetailVo.setEnterpriseId(ShiroUtil.getEnterpriseId());
//		}
//		try {
//			applyCalculateClient.saveDetail(list);
//			RenderUtil.renderJson("success", response);
//		} catch (Exception e) {
//			logger.error("errorId:", e);
//			String msg =e.getMessage();
//			if (e.getCause()!=null) {
//				msg = e.getCause().getMessage().toString();
//			}
//			RenderUtil.renderJson(msg, response);
//		}
//	}
//
//	/**
//	 * 审核二次结算 修改 数量及单价
//	 * @param calculate
//	 * @param request
//	 * @param response
//	 * @throws InvalidParamException
//	 */
//	@RequestMapping(value = "doCalculate", method = RequestMethod.POST)
//	public void doCalculate(ApplyCalculate calculate,HttpServletRequest request, HttpServletResponse response) throws Exception {
//		String calculateNo = calculate.getCalculateNo();
//		String status = calculate.getStatus();
//		if (StringUtils.isBlank(calculateNo)) {
//			throw new InvalidParamException("calculateNo");
//		}
//		if (StringUtils.isBlank(status)) {
//			throw new InvalidParamException("status");
//		}
//		ApplyCalculateFlowVo flowVo = new ApplyCalculateFlowVo();
//		BeanUtils.copyProperties(calculate, flowVo);
//		flowVo.setApproveCurUserId(ShiroUtil.getCurrentUserId());
//		flowVo.setApproveCurUserName(ShiroUtil.getCurrentUserName());
//		applyCalculateClient.doCalculate(flowVo);
//		RenderUtil.renderSuccess("success", response);
//	}
//
//	@Override
//	public Map<String, Object> getDefaultFilter(){
//		Map<String, Object> map = Maps.newHashMap();
//		map.put("EQL_enterpriseId", ShiroUtil.getEnterpriseId());
//		return map;
//	}
//
//	private List<ApplyImportDetail> getImportCalculateDetail(List<ApplyImportDetail> list,String calculateNo){
//		for (ApplyImportDetail detail : list) {
//			ApplyCalculate calculate = new ApplyCalculate();
//			calculate.setImportDetailId(detail.getId());
//			calculate.setCalculateNo(calculateNo);
//			ApplyCalculate applyCalculate = applyCalculateClient.findByImportId(calculate);
//			if (applyCalculate != null) {
//				detail.setWarehouseCost(applyCalculate.getWarehouseCost());
//				detail.setTransportCost(applyCalculate.getTransportCost());
//				detail.setQingguanFee(applyCalculate.getQingguanFee());
//				detail.setArrivalTime(applyCalculate.getArrivalTime());
//				if (StringUtils.equals(BasConstants.CONTRACTTYPE_BUY, detail.getContractType())) {
//					detail.setPayBondAmount(applyCalculate.getPayBondAmount());
//					detail.setKaizhengFee(applyCalculate.getKaizhengFee());
//					detail.setChengduiFee(applyCalculate.getChengduiFee());
//					detail.setPayBondTime(applyCalculate.getPayBondTime());
//					detail.setPayFullTime(applyCalculate.getPayFullTime());
//				}else {
//					detail.setReceiveBondTime(applyCalculate.getPayBondTime());
//					detail.setReceiveFullTime(applyCalculate.getPayFullTime());
//					detail.setDailiFee(applyCalculate.getDailiFee());
//				}
//			}
//		}
//		return list;
//	}
//
//}
