package com.spt.bas.web.controller.apply;

import com.beust.jcommander.internal.Maps;
import com.spt.auth.sdk.cache.DictUtil;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.ApplyDiscuss;
import com.spt.bas.client.entity.BsCompany;
import com.spt.bas.client.entity.CtrContract;
import com.spt.bas.client.remote.IApplyDiscussClient;
import com.spt.bas.client.remote.IBsCompanyClient;
import com.spt.bas.client.remote.ICtrContractClient;
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
import java.util.List;
import java.util.Map;

/**
 * 申请-溢短装申请
 */
@Controller
@RequestMapping(value = "/apply/discuss")
public class ApplyDiscussController extends PageController<ApplyDiscuss, BaseVo> {
	@Autowired
	private IApplyDiscussClient applyDiscussClient;
	@Autowired
	private ICtrContractClient ctrContractClient;
	@Autowired
	private IBsCompanyClient bsCompanyClient;
	@Resource
	private WebParamUtils webParamUtils;

	@Override
	public BaseClient<ApplyDiscuss> getService() {
		return applyDiscussClient;
	}

	@RequestMapping(value = "content/{id}", method = RequestMethod.GET)
	public String content(@PathVariable("id") Long id,PmPermissionVo permissionVo, Model model, HttpServletRequest request) {
		ApplyDiscuss entity = getEntity(id);
		String contractId = request.getParameter("contractId");
		if (entity.getId() == null) {
			List<CtrContract> contracts = ctrContractClient.findContractsByContractId(Long.parseLong(contractId));
			CtrContract buyContract = null;
			CtrContract sellContract = null;
			for (CtrContract contract : contracts) {
				if (BasConstants.CONTRACT_TYPE_B.equals(contract.getContractType())) {
					buyContract = contract;
				} else if (BasConstants.CONTRACT_TYPE_S.equals(contract.getContractType())) {
					sellContract = contract;
				}
			}
			entity.setId(0L);
			entity.setDealNumberB(buyContract.getTotalNumber());
			entity.setDealNumber(buyContract.getTotalNumber());
			entity.setBuyUnitPrice(buyContract.getDealPrice());
			entity.setSellUnitPrice(sellContract.getDealPrice());
			entity.setBuyTotalAmount(buyContract.getTotalAmount());
			entity.setSellTotalAmount(sellContract.getTotalAmount());
			entity.setServiceAmount(sellContract.getServiceAmount());
			entity.setBuyContractId(buyContract.getId());
			entity.setBuyContractNo(buyContract.getContractNo());
			entity.setSellContractId(sellContract.getId());
			entity.setSellContractNo(sellContract.getContractNo());
			// 赊销时长
			model.addAttribute("creditDays", sellContract.getCreditCycle());
			// serverRate
			BsCompany company = bsCompanyClient.getEntity(sellContract.getCompanyId());
			model.addAttribute("serverRate", company.getRate());
			String settlementType = sellContract.getSettlementType();
			// 是否是两票制
			model.addAttribute("isTwo", "1".equals(settlementType));

		}
		if (entity.getBuyContractId() != null){
			Long buyContractId = entity.getBuyContractId();
			CtrContract contract = ctrContractClient.findByContractId(buyContractId);
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
		return "apply/contract-discuss";
	}

	@RequestMapping(value = "updateFileId", method = RequestMethod.POST)
	public void updateFileId(FileIdUpdateVo vo,
							 HttpServletResponse response) {
		try {
			applyDiscussClient.updateFileId(vo);
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
	public ApplyDiscuss getEntity(@RequestParam(value = "id", required = false) Long id) {
		ApplyDiscuss entity = new ApplyDiscuss();
		entity.setStatus(BasConstants.APPROVE_STATUS_N);
		if (id != null && id != 0L) {
			entity = getService().getEntity(id);
		}
		return entity;
	}

}
