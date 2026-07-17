package com.spt.bas.web.controller.apply;

import cn.hutool.core.collection.CollUtil;
import com.spt.auth.sdk.cache.DictUtil;
import com.spt.bas.client.cache.BsCompanyOurUtil;
import com.spt.bas.client.cache.BsDictUtil;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.constant.BsDictConstants;
import com.spt.bas.client.entity.*;
import com.spt.bas.client.remote.IApplyPayClient;
import com.spt.bas.client.remote.IBsCompanyDcsxClient;
import com.spt.bas.client.remote.ICtrContractClient;
import com.spt.bas.client.remote.IFileProcessRelClient;
import com.spt.bas.client.vo.FileIdUpdateVo;
import com.spt.bas.web.shiro.ShiroUtil;
import com.spt.bas.web.util.WebParamUtils;
import com.spt.pm.vo.PmPermissionVo;
import com.spt.tools.core.bean.PageSearchVo;
import com.spt.tools.core.json.JsonUtil;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.data.vo.BaseVo;
import com.spt.tools.web.controller.PageController;
import com.spt.tools.web.util.JsonEasyUI;
import com.spt.tools.web.util.RenderUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

@Controller
@RequestMapping(value = "/apply/pay")
public class  ApplyPayController extends PageController<ApplyPay, BaseVo> {
	@Autowired
	private IApplyPayClient payClient;
	@Autowired
	private ICtrContractClient ctrContractClient;
	@Autowired
	private IFileProcessRelClient fileProcessRelClient;
	@Resource
	private WebParamUtils webParamUtils;
	@Resource
	private IBsCompanyDcsxClient bsCompanyDcsxClient;

	@Override
	public BaseClient<ApplyPay> getService() {
		return payClient;
	}


	/** 审批模板内容 */
	@RequestMapping(value = "content/{id}", method = RequestMethod.GET)
	public String content(@PathVariable("id") Long id, PmPermissionVo permissionVo, Model model,HttpServletRequest request) {
		ApplyPay entity = getEntity(id);
		model.addAttribute("approveStatusJson",
				JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_APPROVESTATUS)));
		List<BsDictData> lstPayType = BsDictUtil.getListByCategory(ShiroUtil.getEnterpriseId(),BsDictConstants.DICT_TYPE_PAYTYPE);
		String applyPayTypeJson;
		if (lstPayType.isEmpty()) {
			applyPayTypeJson = JsonUtil.obj2Json(DictUtil.getListByCategory(BsDictConstants.DICT_TYPE_PAYTYPE));
		}else {
			applyPayTypeJson = JsonUtil.obj2Json(lstPayType);
		}
		//付款类型
		model.addAttribute("applyPayTypeJson", applyPayTypeJson);
		//付款方式
		model.addAttribute("payModeJson", JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_APPLY_PAYMODE)));
		model.addAttribute("entity", entity);
		 //我方抬头
		model.addAttribute("ourCompanyJson", JsonUtil.obj2Json(
				BsCompanyOurUtil.getCompanyOurToBsDictDataList()));

		String processCode = request.getParameter("processCode");
		// 附件类型
		List<FileProcessRel> fileTypeList = fileProcessRelClient.findList(processCode);
		model.addAttribute("fileTypeJson", JsonUtil.obj2Json(fileTypeList));

		// 处理审批中部分控件可编辑
		permissionVo = webParamUtils.verifyPermission(permissionVo, entity.getApproveId(), true);
		model.addAttribute("psv", permissionVo);
		// 业务类型
		model.addAttribute("business", getBusiness(entity));
		Map<String, PmPermissionVo.Options> mapEdit = permissionVo.getMapEdit();
		Boolean payDateEditFlg = false;
		if (CollUtil.isNotEmpty(mapEdit)) {
			Boolean canApproveEdit = permissionVo.getCanApproveEdit();
			PmPermissionVo.Options payDate = mapEdit.get("payDate");
			if (Objects.nonNull(payDate)) {
				Boolean edit = payDate.isEdit();
				if (edit && canApproveEdit) {
					payDateEditFlg = true;
				}
			}
		}
		model.addAttribute("payDateEditFlg", payDateEditFlg);
		return "apply/pay-content";
	}
	@RequestMapping(value = "updateFileId", method = RequestMethod.POST)
	public void updateFileId(FileIdUpdateVo vo,
			HttpServletResponse response) {
		try {
			payClient.updateFileId(vo);
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
	public ApplyPay getEntity(@RequestParam(value = "id", required = false) Long id) {
		if (id != null) {
			if (id > 0) {
				return getService().getEntity(id);
			} else {
				ApplyPay entity = new ApplyPay();
				entity.setId(0L);
				entity.setStatus(BasConstants.APPROVE_STATUS_N);
				entity.setPayDate(new Date());
				return entity;
			}
		}
		return null;
	}

	@RequestMapping(value = "listVo")
	public void listVo(PageSearchVo searchVo, HttpServletRequest request, HttpServletResponse response) {
		Page<ApplyPay> page = findPage(searchVo, request, response);
		Map<String, Object> searchParams = searchVo.getSearchParams();
		searchParams.put("EQS_status", BasConstants.APPROVE_STATUS_D);
		searchVo.setSearchParams(searchParams);
		ApplyPay sum = payClient.findPageSum(searchVo);
		Map<String, Object> footer = new HashMap<>();
		footer.put("payDate", "合计");
		footer.put("payAmount", sum.getPayAmount());
		JsonEasyUI.renderJson(response, page,footer);
	}

	@RequestMapping(value = "queryCancelList", method = RequestMethod.POST)
	public void queryCancelList(PageSearchVo searchVo, HttpServletRequest request, HttpServletResponse response) {
		initSearch(searchVo, request);
		Map<String, Object> map = searchVo.getSearchParams();
		map.put("NEQS_status", BasConstants.APPROVE_STATUS_C);
		map.put("EQL_enterpriseId", ShiroUtil.getEnterpriseId());
		Page<ApplyCancelDetail> page = payClient.findPageDetail(searchVo);

		JsonEasyUI.renderJson(response, page);
	}

	private String getBusiness(ApplyPay pay) {
		String business = "";
		if (pay != null && pay.getContractId() != null && pay.getContractId() != 0L) {
			CtrContract ctrContract = ctrContractClient.getEntity(pay.getContractId());
			String businessType = ctrContract.getBusinessType();
			if (StringUtils.equals(BasConstants.BUSINESS_TYPE_KC_CG, businessType)){
				return "库存采购";
			}
			if (businessType != null) {
                if (StringUtils.equals(BasConstants.BUSINESS_TYPE_ZY_TP, businessType)) {
                    business = "代采托盘";
                } else if (StringUtils.equals(BasConstants.BUSINESS_TYPE_ZY_BB, businessType)) {
					if (ctrContract.getMatchCreditFlg()) {
						business = "赊销";
					} else {
						business = "代采";
					}
				} else {
					business = DictUtil.getValue(BasConstants.DICT_TYPE_BUSINESS, BasConstants.DICT_TYPE_BUSINESS_ZY);
				}
			}
		}
		return business;
	}

	@ResponseBody
	@RequestMapping(value = "getBrushBrushAmount", method = RequestMethod.POST)
	public ApplyPay getBrushBrushAmount(@RequestParam("contractId") Long contractId){
		ApplyPay applyPay = new ApplyPay();
		applyPay.setContractId(contractId);
		applyPay.setEnterpriseId(ShiroUtil.getEnterpriseId());
		return payClient.getBrushBrushAmount(applyPay);
	}

	@RequestMapping(value = "getCompanyConfig")
	public void getCompanyConfig(HttpServletResponse response, HttpServletRequest request) {
		BsCompanyDcsx config = new BsCompanyDcsx();
		Long contractId = Long.valueOf(request.getParameter("contractId"));
		CtrContract entity = ctrContractClient.getEntity(contractId);
		if (Objects.nonNull(entity)) {
			BsCompanyDcsx fundCompanyConfig = bsCompanyDcsxClient.findByCompanyName(entity.getOurCompanyName());
			String companyName = entity.getCompanyName();
			if (!StringUtils.equals(BasConstants.COMPANY_NAME_QDZG, companyName) && !StringUtils.equals(BasConstants.COMPANY_NAME_WSNB, companyName)) {
				config.setFundFlg(false);
			} else {
				if (Objects.nonNull(fundCompanyConfig)) {
					config.setFundFlg(fundCompanyConfig.getFundFlg());
				}
			}
		}
		RenderUtil.renderJson(config, response);
	}
}
