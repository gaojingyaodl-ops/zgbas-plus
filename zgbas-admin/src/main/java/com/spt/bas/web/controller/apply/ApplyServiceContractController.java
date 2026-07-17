package com.spt.bas.web.controller.apply;

import com.spt.auth.sdk.cache.DictUtil;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.ApplyServiceContract;
import com.spt.bas.client.remote.IApplyServiceContractClient;
import com.spt.bas.client.vo.FileIdUpdateVo;
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
import javax.servlet.http.HttpServletResponse;

/**
 * 申请-服务合同
 * 
 * @author
 *
 */
@Controller
@RequestMapping(value = "/apply/serviceContract")
public class ApplyServiceContractController extends PageController<ApplyServiceContract, BaseVo> {

	@Autowired
	private IApplyServiceContractClient serviceContractClient;
	@Resource
	private WebParamUtils webParamUtils;


	@Override
	public BaseClient<ApplyServiceContract> getService() {
		return serviceContractClient;
	}

	@RequestMapping(value = "content/{id}", method = RequestMethod.GET)
	public String content(@PathVariable("id") Long id, PmPermissionVo permissionVo, Model model) {
		ApplyServiceContract entity = getEntity(id);
		if (entity.getId() == null) {
			entity.setId(0L);
		}
		// 服务费比例
		model.addAttribute("serviceContractRateJson",
				JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_SERVICE_CONTRACT_RATE)));
		// 处理审批中部分控件可编辑
		permissionVo = webParamUtils.verifyPermission(permissionVo, entity.getApproveId());
		model.addAttribute("entity", entity);
		model.addAttribute("psv", permissionVo);
		return "apply/service-contract";
	}

	@ModelAttribute("preload")
	public ApplyServiceContract getEntity(@RequestParam(value = "id", required = false) Long id) {
		if (id != null) {
			if (id > 0) {
				return getService().getEntity(id);
			} else {
				ApplyServiceContract entity = new ApplyServiceContract();
				entity.setId(0L);
				entity.setStatus(BasConstants.APPROVE_STATUS_N);
				return entity;
			}
		}
		return null;
	}

	@RequestMapping(value = "updateFileId", method = RequestMethod.POST)
	public void updateFileId(FileIdUpdateVo vo, HttpServletResponse response) {
		try {
			serviceContractClient.updateFileId(vo);
			RenderUtil.renderSuccess("success", response);
		} catch (Exception e) {
			logger.error("errorId:", e);
			RenderUtil.renderFailure("errorId:" + e.getMessage(), response);
		}
	}

}
