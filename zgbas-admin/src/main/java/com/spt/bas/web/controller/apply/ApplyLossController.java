package com.spt.bas.web.controller.apply;

import com.beust.jcommander.internal.Maps;
import com.spt.auth.sdk.cache.DictUtil;
import com.spt.bas.client.cache.BsDictUtil;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.*;
import com.spt.bas.client.remote.IApplyLossClient;
import com.spt.bas.client.remote.IBsCompanyAccountClient;
import com.spt.bas.client.remote.ICtrContractClient;
import com.spt.bas.client.vo.BsCompanyVo;
import com.spt.bas.client.vo.CtrContractChooseVo;
import com.spt.bas.client.vo.FileIdUpdateVo;
import com.spt.bas.web.shiro.ShiroUtil;
import com.spt.bas.web.util.WebParamUtils;
import com.spt.pm.vo.PmPermissionVo;
import com.spt.tools.core.json.JsonUtil;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.data.vo.BaseVo;
import com.spt.tools.web.controller.PageController;
import com.spt.tools.web.util.RenderUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 申请-损耗申请
 */
@Controller
@RequestMapping(value = "/apply/loss")
public class ApplyLossController extends PageController<ApplyLoss, BaseVo> {
	@Autowired
	private IApplyLossClient applyLossClient;
	@Autowired
	private IBsCompanyAccountClient bsCompanyAccountClient;
	@Autowired
	private ICtrContractClient ctrContractClient;
	@Resource
	private WebParamUtils webParamUtils;

	@Override
	public BaseClient<ApplyLoss> getService() {
		return applyLossClient;
	}

	@RequestMapping(value = "content/{id}", method = RequestMethod.GET)
	public String content(@PathVariable("id") Long id,PmPermissionVo permissionVo, Model model, HttpServletRequest request) {
		ApplyLoss entity = getEntity(id);
		if (entity.getId() == null) {
			entity.setId(0L);
		}
		List<BsDictData> listByCategory = BsDictUtil.getListByCategory(ShiroUtil.getEnterpriseId(), BasConstants.DICT_TYPE_LOSS_TYPE);
		List<BsDictData> newListByCategory = new ArrayList<>();
		String contractId = request.getParameter("contractId");
		if(StringUtils.isNotBlank(contractId) && !StringUtils.equals("null",contractId) && !StringUtils.equals("undefined",contractId)){
			CtrContractChooseVo contractChooseVo = ctrContractClient.findByContractId(Long.valueOf(contractId));
			if(!Objects.isNull(contractChooseVo) && StringUtils.isNotBlank(contractChooseVo.getContractType())){
				String contractType = contractChooseVo.getContractType();
				if(listByCategory != null && listByCategory.size() > 0){
					for (BsDictData dictData:listByCategory) {
						if(StringUtils.equals(BasConstants.CONTRACTTYPE_BUY,contractType) && !StringUtils.equals(BasConstants.DICT_TYPE_LOSS_TYPE_4,dictData.getDictCd())){
							newListByCategory.add(dictData);
						} else if(StringUtils.equals(BasConstants.CONTRACTTYPE_SELL,contractType) && !StringUtils.equals(BasConstants.DICT_TYPE_LOSS_TYPE_1,dictData.getDictCd())){
							newListByCategory.add(dictData);
						}
					}
				}
			}

		}
		if(newListByCategory != null && newListByCategory.size() > 0){
			// 责任方
			model.addAttribute("lossTypeFromTypeJson",JsonUtil.obj2Json(newListByCategory));
			// 承担方
			model.addAttribute("lossTypeToTypeJson",JsonUtil.obj2Json(newListByCategory));
		} else {
			// 责任方
			model.addAttribute("lossTypeFromTypeJson",JsonUtil.obj2Json(listByCategory));
			// 承担方
			model.addAttribute("lossTypeToTypeJson",JsonUtil.obj2Json(listByCategory));
		}
				
		
		if(entity.getContractId()!=null){
			Long sellContractId = entity.getContractId();
			CtrContract contract = ctrContractClient.findByContractId(sellContractId);
			model.addAttribute("contractApproveId", contract.getApproveId());
		}
		String contractApproveId = request.getParameter("contractApproveId");
		if (StringUtils.isNotEmpty(contractApproveId)){
			model.addAttribute("contractApproveId", Long.parseLong(request.getParameter("contractApproveId")));
		}
		model.addAttribute("entity", entity);
		//处理审批中部分控件可编辑
		permissionVo = webParamUtils.verifyPermission(permissionVo, entity.getApproveId());

		model.addAttribute("psv", permissionVo);

		model.addAttribute("contractAttr",
				JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.STOCK__CONTRACT_ATTR)));
		//业务类型
		return "apply/contract-loss";
	}

	@RequestMapping(value = "updateFileId", method = RequestMethod.POST)
	public void updateFileId(FileIdUpdateVo vo,
							 HttpServletResponse response) {
		try {
			applyLossClient.updateFileId(vo);
			RenderUtil.renderSuccess("success", response);
		} catch (Exception e) {
			logger.error("errorId:", e);
			RenderUtil.renderFailure("errorId:" + e.getMessage(), response);
		}
	}

	@Override
	public Map<String, Object> getDefaultFilter() {
		Map<String, Object> map = Maps.newHashMap();
		map.put("EQL_enterpriseId", ShiroUtil.getEnterpriseId());
		return map;
	}

	@ModelAttribute("preload")
	public ApplyLoss getEntity(@RequestParam(value = "id", required = false) Long id) {
		ApplyLoss entity = new ApplyLoss();
		entity.setStatus(BasConstants.APPROVE_STATUS_N);
		if (id != null && id != 0L) {
			entity = getService().getEntity(id);
		}
		return entity;
	}

	private Boolean getDefaultFlg(ApplyBuy buy) {
		Boolean defaultFlg = false;
		if (buy != null && buy.getCompanyId() != null) {
			BsCompanyVo vo = new BsCompanyVo();
			vo.setId(buy.getCompanyId());
			vo.setEnterpriseId(buy.getEnterpriseId());
			BsCompanyAccount account = bsCompanyAccountClient.findDefaultAccount(vo);
			if (account != null && account.getBankAccount() != null) {
				String bankAccount = account.getBankAccount();
				String bankName = account.getBankName();
				if (StringUtils.equals(bankName, buy.getReceiveBank()) && StringUtils.equals(bankAccount, buy.getReceiveAccount())) {
					defaultFlg = true;
				}
			}
		}else {
			defaultFlg = true;
		}
		return defaultFlg;
	}
}
