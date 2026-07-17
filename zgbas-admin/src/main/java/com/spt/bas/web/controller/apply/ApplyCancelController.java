package com.spt.bas.web.controller.apply;

import com.spt.auth.sdk.cache.DictUtil;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.ApplyCancel;
import com.spt.bas.client.entity.ApplyCancelDetail;
import com.spt.bas.client.entity.CtrContract;
import com.spt.bas.client.remote.IApplyCancelClient;
import com.spt.bas.client.remote.ICtrContractClient;
import com.spt.bas.client.vo.FileIdUpdateVo;
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

@Controller
@RequestMapping(value = "/apply/cancel")
public class ApplyCancelController extends PageController<ApplyCancel, BaseVo> {
	@Autowired
	private IApplyCancelClient applyCancelClient;
	@Autowired
	private ICtrContractClient ctrContractClient;
	@Resource
	private WebParamUtils webParamUtils;
	
	@Override
	public BaseClient<ApplyCancel> getService() {
		return applyCancelClient;
	}

	@RequestMapping(value = "content/{id}", method = RequestMethod.GET)
	public String content(@PathVariable("id") Long id,PmPermissionVo permissionVo, Model model, HttpServletRequest request) {
		ApplyCancel entity = getEntity(id);
		model.addAttribute("entity", entity);
		CtrContract contract = null;
		if(entity!=null&&id.intValue()>0){
			contract = ctrContractClient.getEntity(Long.parseLong(entity.getContractId()));
			model.addAttribute("contract", contract);
		}
		//处理审批中部分控件可编辑
		permissionVo = webParamUtils.verifyPermission(permissionVo, entity.getApproveId());
				
		model.addAttribute("psv", permissionVo);
		model.addAttribute("contractAttr",
				JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.STOCK__CONTRACT_ATTR)));
		return "apply/cancel-content";
	}
	
	@ModelAttribute("preload")
	public ApplyCancel getEntity(@RequestParam(value = "id", required = false) Long id) {
		if (id != null) {
			if (id > 0)
				return getService().getEntity(id);
			else {
				ApplyCancel entity = new ApplyCancel();
				entity.setId(0l);
				entity.setStatus(BasConstants.APPROVE_STATUS_N);
				return entity;
			}
		}
		return null;
	}
	
	@RequestMapping(value = "detailList")
	public void detailList(PageSearchVo searchVo, HttpServletRequest request, HttpServletResponse response) {
		initSearch(searchVo, request);
		Page<ApplyCancelDetail> page = applyCancelClient.queryDetailPage(searchVo);
		JsonEasyUI.renderJson(response, page);
	}	
	@RequestMapping(value = "deleteDetail/{id}")
	public void deleteDetail(@PathVariable("id") Long id,HttpServletResponse response) {
		applyCancelClient.deleteDetail(id);
		RenderUtil.renderSuccess("success", response);
	}
	
	// 更改附件ID
	@RequestMapping(value = "updateFileId", method = RequestMethod.POST)
	public void updateFileId(FileIdUpdateVo vo,HttpServletResponse response) {
		try {
			applyCancelClient.updateFileId(vo);
			RenderUtil.renderSuccess("success", response);
		} catch (Exception e) {
			logger.error("errorId:", e);
			RenderUtil.renderFailure("errorId:" + e.getMessage(), response);
		}
	}
}
