package com.spt.bas.web.controller.apply;

import com.beust.jcommander.internal.Maps;
import com.spt.auth.sdk.cache.DictUtil;
import com.spt.bas.client.cache.BsCompanyOurUtil;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.ApplyCompanyTitle;
import com.spt.bas.client.remote.IApplyCompanyTitleClient;
import com.spt.bas.client.vo.FileIdUpdateVo;
import com.spt.bas.web.shiro.ShiroUtil;
import com.spt.bas.web.util.WebParamUtils;
import com.spt.pm.vo.PmPermissionVo;
import com.spt.tools.core.json.JsonUtil;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.data.vo.BaseVo;
import com.spt.tools.web.controller.PageController;
import com.spt.tools.web.util.RenderUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * 申请-议短申请
 */
@Controller
@RequestMapping(value = "/apply/companyTitle")
public class ApplyCompanyTitleController extends PageController<ApplyCompanyTitle, BaseVo> {
	@Autowired
	private IApplyCompanyTitleClient applyCompanyTitleClient;
	@Resource
	private WebParamUtils webParamUtils;
	
	@Override
	public BaseClient<ApplyCompanyTitle> getService() {
		return applyCompanyTitleClient;
	}
	
	@RequestMapping(value = "content/{id}", method = RequestMethod.GET)
	public String content(@PathVariable("id") Long id,PmPermissionVo permissionVo, Model model, HttpServletRequest request) {
		ApplyCompanyTitle entity = getEntity(id);
		if (entity.getId() == null) {
			entity.setId(0L);
		}
		model.addAttribute("entity", entity);
		model.addAttribute("ourCompanyJson", JsonUtil.obj2Json(
				BsCompanyOurUtil.getCompanyOurToBsDictDataList()));
		//处理审批中部分控件可编辑
		permissionVo = webParamUtils.verifyPermission(permissionVo, entity.getApproveId());
				
		model.addAttribute("psv", permissionVo);

		model.addAttribute("contractAttr",
				JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.STOCK__CONTRACT_ATTR)));
		//业务类型
		return "apply/contract-companyTitle";
	}
	
	@RequestMapping(value = "updateFileId", method = RequestMethod.POST)
	public void updateFileId(FileIdUpdateVo vo,
			HttpServletResponse response) {
		try {
			applyCompanyTitleClient.updateFileId(vo);
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
	public ApplyCompanyTitle getEntity(@RequestParam(value = "id", required = false) Long id) {
		ApplyCompanyTitle entity = new ApplyCompanyTitle();
		entity.setStatus(BasConstants.APPROVE_STATUS_N);
		if (id != null && id != 0L) {
			entity = getService().getEntity(id);
		}
		return entity;
	}

}
