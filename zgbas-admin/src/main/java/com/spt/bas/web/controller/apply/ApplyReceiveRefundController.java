package com.spt.bas.web.controller.apply;

import com.spt.auth.sdk.cache.DictUtil;
import com.spt.bas.client.cache.BsCompanyOurUtil;
import com.spt.bas.client.cache.BsDictUtil;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.ApplyCancelDetail;
import com.spt.bas.client.entity.ApplyReceiveRefund;
import com.spt.bas.client.entity.CtrContract;
import com.spt.bas.client.remote.*;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.Map;

/**
 * 销售退款申请
 */
@Controller
@RequestMapping(value = "/apply/receiveRefund")
public class ApplyReceiveRefundController  extends PageController<ApplyReceiveRefund, BaseVo>{
	@Autowired
	private IApplyReceiveRefundClient applyReceiveRefundClient;
	@Autowired
	private  ICtrContractClient contractClient;
	@Resource
	private WebParamUtils webParamUtils;
	@Override
	public BaseClient<ApplyReceiveRefund> getService() {
		return applyReceiveRefundClient;
	}


	/** 审批模板内容 */
	@RequestMapping(value = "content/{id}", method = RequestMethod.GET)
	public String content(@PathVariable("id") Long id, PmPermissionVo permissionVo, Model model) {
		ApplyReceiveRefund entity = getEntity(id);
		model.addAttribute("approveStatusJson", JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_APPROVESTATUS)));
		model.addAttribute("entity", entity);
		 if(entity.getContractId()!=null && entity.getContractId()>0) {
			CtrContract contract = contractClient.getEntity(entity.getContractId());
			model.addAttribute("contract", contract);
		}
		 //我方抬头
		model.addAttribute("ourCompanyJson", JsonUtil.obj2Json(BsCompanyOurUtil.getCompanyOurToBsDictDataList()));
		model.addAttribute("ownRegionType", JsonUtil.obj2Json(BsDictUtil.getListByCategory(ShiroUtil.getEnterpriseId(), BasConstants.DICT_OWN_REGION)));
		//处理审批中部分控件可编辑
		permissionVo = webParamUtils.verifyPermission(permissionVo, entity.getApproveId());
		model.addAttribute("psv", permissionVo);
		return "apply/receiveRefund-content";
	}
	@ModelAttribute("preload")
	public ApplyReceiveRefund getEntity(@RequestParam(value = "id", required = false) Long id) {
		if (id != null) {
			if (id > 0) {
				return getService().getEntity(id);
			} else {
				ApplyReceiveRefund entity = new ApplyReceiveRefund();
				entity.setId(0L);
				entity.setStatus(BasConstants.APPROVE_STATUS_N);
				entity.setRefundDate(new Date());
				return entity;
			}
		}
		return null;
	}


	@RequestMapping(value = "updateFileId", method = RequestMethod.POST)
	public void updateFileId(FileIdUpdateVo vo,
			HttpServletResponse response) {
		try {
			applyReceiveRefundClient.updateFileId(vo);
			RenderUtil.renderSuccess("success", response);
		} catch (Exception e) {
			logger.error("errorId:", e);
			RenderUtil.renderFailure("errorId:" + e.getMessage(), response);
		}
	}

	@RequestMapping(value = "queryCancelList", method = RequestMethod.POST)
	public void queryCancelList(PageSearchVo searchVo, HttpServletRequest request, HttpServletResponse response) {
		initSearch(searchVo, request);
		Map<String, Object> map = searchVo.getSearchParams();
		map.put("NEQS_status", BasConstants.APPROVE_STATUS_C);
		map.put("EQL_enterpriseId", ShiroUtil.getEnterpriseId());
		Page<ApplyCancelDetail> page = applyReceiveRefundClient.findPageDetail(searchVo);

		JsonEasyUI.renderJson(response, page);
	}

}
