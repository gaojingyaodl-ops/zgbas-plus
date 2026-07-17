package com.spt.bas.web.controller.apply;

import com.spt.auth.sdk.cache.DictUtil;
import com.spt.auth.sdk.entity.SysDeptSdk;
import com.spt.auth.sdk.open.IAuthOpenFacade;
import com.spt.auth.sdk.vo.DeptSearchVo;
import com.spt.bas.client.cache.BsCompanyOurUtil;
import com.spt.bas.client.cache.BsDictUtil;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.*;
import com.spt.bas.client.remote.*;
import com.spt.bas.client.vo.ApplySellPayModeVo;
import com.spt.bas.client.vo.FileIdUpdateVo;
import com.spt.bas.web.shiro.ShiroUtil;
import com.spt.bas.web.util.EasyTreeUtil2;
import com.spt.bas.web.util.ProcessControlUtil;
import com.spt.bas.web.util.WebParamUtils;
import com.spt.pm.vo.PmPermissionVo;
import com.spt.tools.core.json.JsonUtil;
import com.spt.tools.data.easyui.EasyTreeNode;
import com.spt.tools.web.util.RenderUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 争议
 *
 */
@Controller
@RequestMapping(value = "/apply/dispute")
public class ApplyDisputeController {
	Logger logger = LoggerFactory.getLogger(getClass());
	@Autowired
	private IApplyMatchClient applyMatchClient;
	@Autowired
	private IApplyMatchDetailClient applyMatchDetailClient;
	@Autowired
	private IBsProductTypeClient productTypeClient;
	@Autowired
	private IBsFactoryClient factoryClient;
	@Autowired
	private IBsProductTypeClient bsProductTypeClient;
	@Autowired
	private IAuthOpenFacade authOpenFacade;
	@Autowired
	private IBasBrandClient brandClient;
	@Autowired
	private IBsContractTemplateClient bsContractTemplateClient;
	@Autowired
	private ICtrServiceContractClient ctrServiceContractClient;
	@Autowired
	private ICtrContractClient ctrContractClient;
	@Autowired
	private IPmApproveContentsClient pmApproveContentsClient;
	@Resource
	private WebParamUtils webParamUtils;
	@Value("${file.show.url}")
	private String fileShowUrl;


	/**
	 * 审批模板内容
	 */
	@RequestMapping(value = "content/{id}", method = RequestMethod.GET)
	public String content(@PathVariable("id") Long id, PmPermissionVo permissionVo, Model model, HttpServletRequest request) {
		String processCode = request.getParameter("processCode");
		ApplyDispute dispute = getEntity(id, processCode);
		List<CtrContract> contracts;
		if (id > 0) {
			contracts = ctrContractClient.findContractsByContractId(dispute.getSellContractId());
		}else{
			String contractId = request.getParameter("contractId");
			contracts = ctrContractClient.findContractsByContractId(Long.parseLong(contractId));
		}
		CtrContract buyContract = null;
		CtrContract sellContract = null;
		for (CtrContract contract : contracts) {
			if (BasConstants.CONTRACT_TYPE_B.equals(contract.getContractType())) {
				buyContract = contract;
			} else if (BasConstants.CONTRACT_TYPE_S.equals(contract.getContractType())) {
				sellContract = contract;
			}
		}
		ApplyMatch applyMatch = applyMatchClient.findByApproveId(buyContract.getApproveId());
		dealWithModel(model, applyMatch);

		// 显示赋值
		initDispute(buyContract,sellContract,applyMatch,dispute);

		// 附件
		String sellContractFileId = StringUtils.isEmpty(sellContract.getSellContentFileId()) ? "" : fileShowUrl + "/view/show/"+sellContract.getSellContentFileId().split(",")[0];
		String serviceContractFileId = StringUtils.isEmpty(sellContract.getServiceContentFileId()) ? "" : fileShowUrl + "/view/show/"+sellContract.getServiceContentFileId().split(",")[0];
		model.addAttribute("sellContractFileId", sellContractFileId);
		model.addAttribute("serviceContractFileId", serviceContractFileId);
		String buyContractFileId = StringUtils.isEmpty(buyContract.getBuyContentFileId()) ? "" : fileShowUrl + "/view/show/"+buyContract.getBuyContentFileId().split(",")[0];
		model.addAttribute("buyContractFileId", buyContractFileId);

		//处理审批中部分控件可编辑
		permissionVo = webParamUtils.verifyPermission(permissionVo, dispute.getApproveId());

		model.addAttribute("psv", permissionVo);
		model.addAttribute("entity", dispute);

		return "apply/dispute";
	}

	/**
	 * 初始化赋值
	 * @param buyContract
	 * @param sellContract
	 * @param applyMatch
	 * @param dispute
	 */
	private void initDispute(CtrContract buyContract,CtrContract sellContract,ApplyMatch applyMatch,ApplyDispute dispute) {
		List<ApplyMatchDetail> applyMatchDetails = applyMatchDetailClient.findByApproveId(applyMatch.getApproveId());
		ApplyMatchDetail buyMatchDetail = null;
		ApplyMatchDetail sellMatchDetail = null;
		for (ApplyMatchDetail applyMatchDetail : applyMatchDetails) {
			if (BasConstants.CONTRACT_TYPE_S.equals(applyMatchDetail.getContractType())) {
				sellMatchDetail = applyMatchDetail;
			} else if (BasConstants.CONTRACT_TYPE_B.equals(applyMatchDetail.getContractType())) {
				buyMatchDetail = applyMatchDetail;
			}
		}
		dispute.setCurBuyContractStatus(buyContract.getContractStatus());
		dispute.setCurSellContractStatus(sellContract.getContractStatus());
		dispute.setOurCompanyName(sellContract.getOurCompanyName());
		dispute.setProductName(applyMatch.getProductName());
		dispute.setBrandNumber(applyMatch.getBrandNumber());
		dispute.setDealNumber(sellContract.getTotalNumber());
		dispute.setFactoryName(applyMatch.getFactoryName());
		dispute.setWrapSpecs(applyMatch.getWrapSpecs());
		dispute.setQualityStandard(applyMatch.getQualityStandard());
		dispute.setBuyContractId(buyContract.getId());
		dispute.setBuyContractNo(buyContract.getContractNo());
		dispute.setBuySource(buyMatchDetail.getBuySource());
		dispute.setBuyCompanyName(buyContract.getCompanyName());
		dispute.setBuyMatchUserName(buyContract.getMatchUserName());
		dispute.setDeliveryModeB(buyContract.getDeliveryMode());
		dispute.setPayType(buyContract.getPayType());
		dispute.setPayRate(buyContract.getBondRate());
		dispute.setPayRateAmount(buyContract.getBondAmount());
		dispute.setPayFullTime(buyContract.getPayFullTime());
		dispute.setBuyDeliveryType(buyContract.getDeliveryType());
		dispute.setBuyDeliveryDate(buyContract.getDeliveryDateFrom());
		dispute.setbArrivalTimeExt(buyMatchDetail.getArrivalTimeExt());
		dispute.setBdeliveryAddr(buyContract.getDeliveryAddr());
		dispute.setContactAddr(buyContract.getContactAddr());
		dispute.setBwarehouseCost(buyContract.getWarehouseAmount());
		dispute.setBtransportCost(buyContract.getTransportAmount());
		dispute.setBdealPrice(buyContract.getDealPrice());
		dispute.setBdealAmountNotax(buyMatchDetail.getDealAmountNotax());
		dispute.setBtotalAmount(buyContract.getTotalAmount());
		dispute.setPayRemark(buyContract.getRemark());
		dispute.setBuyContentTemplateId(buyContract.getBuyContentFileId());

		dispute.setSellContractId(sellContract.getId());
		dispute.setSellContractNo(sellContract.getContractNo());
		dispute.setSellCompanyName(sellContract.getCompanyName());
		dispute.setSellMatchUserName(sellContract.getMatchUserName());
		dispute.setDeliveryModeS(sellContract.getDeliveryMode());
		dispute.setReceiveType(sellContract.getPayType());
		dispute.setReceiveRate(sellContract.getBondRate());
		dispute.setReceiveRateAmount(sellContract.getBondAmount());
		dispute.setReceiveFullTime(sellContract.getPayFullTime());
		dispute.setSellDeliveryType(sellContract.getDeliveryType());
		dispute.setSellDeliveryDate(sellContract.getDeliveryDateTo());
		dispute.setsArrivalTimeExt(sellMatchDetail.getArrivalTimeExt());
		dispute.setDeliveryAddr(sellContract.getDeliveryAddr());
		dispute.setsContactAddr(sellContract.getContactAddr());
		dispute.setSwarehouseCost(sellContract.getWarehouseAmount());
		dispute.setStransportCost(sellContract.getTransportAmount());
		dispute.setPremium(sellContract.getPremium());
		dispute.setSdealPrice(sellContract.getDealPrice());
		dispute.setStotalAmount(sellContract.getTotalAmount());
		dispute.setsExtraTerm(sellMatchDetail.getExtraTerm());
		dispute.setReceiveRemark(sellContract.getRemark());
		dispute.setSellContentTemplateId(sellContract.getSellContentFileId());
		dispute.setCreditDays(sellContract.getCreditCycle().intValue());
		dispute.setSettlementType(sellContract.getSettlementType());
		dispute.setDeliveryModeS(sellContract.getSettlementType());

		dispute.setServiceFlg(sellContract.getServiceAmount().compareTo(BigDecimal.ZERO) > 0);
		CtrServiceContract serviceContract = ctrServiceContractClient.findByCtrContract(sellContract.getId());
		if (serviceContract != null) {
			dispute.setServiceOurCompanyName(serviceContract.getOurCompanyName());
			dispute.setServiceAmount(serviceContract.getTotalAmount());
			dispute.setServiceContractNo(serviceContract.getServiceContractNo());
		}
	}


	/**
	 * 处理返回model
	 * @param model
	 * @param entity
	 * @return
	 */
	private Model dealWithModel(Model model, ApplyMatch entity) {
		model.addAttribute("match", entity);
		// 采购来源
		model.addAttribute("buySourceJson", JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_BUY_SOURCE)));

		// 货品树
		model.addAttribute("productTypeJson",
				JsonUtil.obj2Json(productTypeClient.findAllProductTree(ShiroUtil.getEnterpriseId())));
		List<BasBrand> lstBrand = brandClient.findAll();
		model.addAttribute("brandJson", JsonUtil.obj2Json(lstBrand));
		// 包装规格
		model.addAttribute("packingSpecificaJson",
				JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_PACKINGSPECIFICA)));
		// 服务费收取方式
		model.addAttribute("serviceTypeJson",
				JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYP_SERVICE_TYPE)));

		List<EasyTreeNode> productTree = bsProductTypeClient.findAllProductTree(ShiroUtil.getEnterpriseId());
		model.addAttribute("productAllJson", JsonUtil.obj2Json(productTree));
		model.addAttribute("productChildrenJson", JsonUtil.obj2Json(productTypeClient.findAll()));
		// 支付方式
		model.addAttribute("payTypeJson",
				JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_APPLY_PAYMODE)));
		// 提货方式
		model.addAttribute("deliveryTypeJson",
				JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_BUYDELIVERY)));
		// 销售方式
		model.addAttribute("deliveryModeJson", JsonUtil.obj2Json(
				BsDictUtil.getListByCategory(ShiroUtil.getEnterpriseId(), BasConstants.DICT_TYPE_DELIVERYMODE)));
		// 采购结算方式
		model.addAttribute("buyDeliveryModeJson",
				JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_BUY_DELIVERYMODE)));
		// 销售结算方式
		model.addAttribute("sellDeliveryModeJson",
				JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_SELL_DELIVERYMODE)));
		// 企业抬头
		model.addAttribute("ourCompanyNameJson", JsonUtil.obj2Json(
				BsCompanyOurUtil.getCompanyOurToBsDictDataList()));
		model.addAttribute("productDeliveryJson",
				JsonUtil.obj2Json(productTypeClient.findAllProductTree(ShiroUtil.getEnterpriseId())));
		// 交货时间的补充字段
		model.addAttribute("arrivalTimeExtJson",
				JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_ATTACHDELIVERYTIME)));
		// 质量标准
		model.addAttribute("qualityStandardJson",
				JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_QUALITYSTANDDARD)));
		// 厂商
		List<BsFactory> lstFactory = factoryClient.findByEnterpriseId(ShiroUtil.getEnterpriseId());
		model.addAttribute("factoryJson", JsonUtil.obj2Json(lstFactory));
		// 合同类型
		model.addAttribute("contractTypeJson",
				JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_CONTRACTTYPE)));
		// 合同属性
		model.addAttribute("contractAttr",
				JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.STOCK__CONTRACT_ATTR)));
		// 默认交货地址
		model.addAttribute("defaultDeliveryAddr", JsonUtil.obj2Json(BsDictUtil
				.getListByCategory(ShiroUtil.getEnterpriseId(), BasConstants.DICT_TYPE_DEFAULT_DELIVERYADDR)));
		// 业务类型
		String businessType = entity.getBusinessType();
		model.addAttribute("business", getBusiness(businessType));
		// 合同状态
		model.addAttribute("contractStatusJson",
				JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_CONTRACTSTATUS)));
		// 采购付款方式
		List<ApplySellPayModeVo> buyModeList = getPayModeB();
		if (!buyModeList.isEmpty()) {
			model.addAttribute("buyPayModeJson", JsonUtil.obj2Json(buyModeList));
		}
		// 销售付款方式
		List<ApplySellPayModeVo> sellModeList = getPayModeS();
		if (!sellModeList.isEmpty()) {
			model.addAttribute("sellPayModeJson", JsonUtil.obj2Json(sellModeList));
		}
		//采购合同模板
		BsContractTemplate template = new BsContractTemplate();
		template.setEnterpriseId(ShiroUtil.getEnterpriseId());
		template.setContractType(BasConstants.CONTRACT_TYPE_B);
		List<BsContractTemplate> buyTemplateList = bsContractTemplateClient.findByContractTypeAndEnterpriseId(template);
		model.addAttribute("buyTemplateList", JsonUtil.obj2Json(buyTemplateList));
		//销售合同模板
		template.setContractType(BasConstants.CONTRACT_TYPE_S);
		List<BsContractTemplate> sellTemplateList = bsContractTemplateClient.findByContractTypeAndEnterpriseId(template);
		model.addAttribute("sellTemplateList", JsonUtil.obj2Json(sellTemplateList));
		//服务合同模板
		template.setContractType(BasConstants.CONTRACT_TYPE_F);
		List<BsContractTemplate> serviceTemplateList = bsContractTemplateClient.findByContractTypeAndEnterpriseId(template);
		model.addAttribute("serviceTemplateList", JsonUtil.obj2Json(serviceTemplateList));
		// 定金比例
		model.addAttribute("contractBondRateJson",
				JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_CONTRACT_BOND_RATE)));
		DeptSearchVo deptSearchVo = new DeptSearchVo( ShiroUtil.getEnterpriseId());
		List<SysDeptSdk> deptList = authOpenFacade.findDeptAll(deptSearchVo);
		EasyTreeNode nodes = EasyTreeUtil2.getDeptTree(deptList, true);
		model.addAttribute("matchUserNameTree", JsonUtil.obj2Json(nodes.getChildren()));

		// 审批状态
		model.addAttribute("approveStatusJson",
				JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_APPROVESTATUS)));

		return model;
	}

	@RequestMapping(value = "updateFileId", method = RequestMethod.POST)
	public void updateFileId(FileIdUpdateVo vo,
			HttpServletResponse response) {
		try {
			pmApproveContentsClient.updateFileId(vo);
			RenderUtil.renderSuccess("success", response);
		} catch (Exception e) {
			logger.error("errorId:", e);
			RenderUtil.renderFailure("errorId:" + e.getMessage(), response);
		}
	}


	/**
	 * 使用@ModelAttribute, 实现Struts2
	 * Preparable二次部分绑定的效果,先根据form的id从数据库查出Task对象,再把Form提交的内容绑定到该对象上。
	 * 因为仅update()方法的form中有id属性，因此本方法在该方法中执行.
	 */
	@ModelAttribute("preload")
	public ApplyDispute getEntity(@RequestParam(value = "id", required = false) Long id, @RequestParam(value = "processCode", required = false) String processCode) {
		ApplyDispute enObject = null;
		if (id != null) {
			if (id > 0 && StringUtils.isNotBlank(processCode)) {
				enObject = (ApplyDispute) ProcessControlUtil.getEntity(id, processCode);
				return enObject;
			} else {
				ApplyDispute entity = new ApplyDispute();
				entity.setId(0L);
				return entity;
			}
		}
		return null;
	}

	private String getBusiness(String businessType) {
		String business = DictUtil.getValue(BasConstants.DICT_TYPE_BUSINESS, BasConstants.DICT_TYPE_BUSINESS_DC);
		return business;
	}

	private List<ApplySellPayModeVo> getPayModeB() {
		List<ApplySellPayModeVo> modeList = new ArrayList<>();
		List<BsDictData> bsDictData = BsDictUtil.getListByCategory(ShiroUtil.getEnterpriseId(),
				BasConstants.BSDICT_BUY_PAYMODE);
		if (!bsDictData.isEmpty()) {
			String[] serialNumber = { BasConstants.PAYMODE_XHHK, BasConstants.PAYMODE_QKZF, BasConstants.PAYMODE_DJZF };
			for (int i = 0; i < bsDictData.size(); i++) {
				String mode = "";
				String dictName = bsDictData.get(i).getDictName();
				if (i == 0) {
					mode = MessageFormat.format(dictName, new Object[] { "()" });
				} else if (i == 1) {
					mode = MessageFormat.format(dictName, new Object[] { dataFormat(), "()" });
				} else if (i == 2) {
					mode = MessageFormat.format(dictName, new Object[] { dataFormat(), "****", "()" });
				}
				ApplySellPayModeVo modeVo = new ApplySellPayModeVo(serialNumber[i], mode);
				modeList.add(modeVo);
			}
		}
		return modeList;
	}

	private List<ApplySellPayModeVo> getPayModeS() {
		List<ApplySellPayModeVo> modeList = new ArrayList<>();
		List<BsDictData> bsDictData = BsDictUtil.getListByCategory(ShiroUtil.getEnterpriseId(),
				BasConstants.BSDICT_MATCH_PAYMODE);
		if (!bsDictData.isEmpty()) {
			String[] serialNumber = { BasConstants.PAYMODE_QKZF, BasConstants.PAYMODE_HDFK };
			for (int i = 0; i < bsDictData.size(); i++) {
				String mode = "";
				String dictName = bsDictData.get(i).getDictName();
				if (i == 0) {
					mode = MessageFormat.format(dictName, new Object[] { dataFormat(), "()" });
				} else {
					mode = MessageFormat.format(dictName, new Object[] { dataFormat(), "****", "()" });
				}
				ApplySellPayModeVo modeVo = new ApplySellPayModeVo(serialNumber[i], mode);
				modeList.add(modeVo);
			}
		}
		return modeList;
	}

	private static String dataFormat() {
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
		return sdf.format(date);
	}

}
