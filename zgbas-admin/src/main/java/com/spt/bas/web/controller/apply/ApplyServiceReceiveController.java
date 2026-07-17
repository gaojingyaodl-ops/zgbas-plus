package com.spt.bas.web.controller.apply;

import com.spt.auth.sdk.cache.DictUtil;
import com.spt.bas.client.cache.BsCompanyOurUtil;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.ApplyServiceReceive;
import com.spt.bas.client.entity.CtrContract;
import com.spt.bas.client.remote.*;
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
import java.util.Date;

@Controller
@RequestMapping(value = "/apply/serviceReceive")
public class ApplyServiceReceiveController extends PageController<ApplyServiceReceive, BaseVo>{
	@Autowired
	private IApplyServiceReceiveClient applyServiceReceiveClient;
	@Autowired
	private  ICtrContractClient contractClient;
	@Resource
	private WebParamUtils webParamUtils;
	@Override
	public BaseClient<ApplyServiceReceive> getService() {
		return applyServiceReceiveClient;
	}
	
	
	/** 审批模板内容 */
	@RequestMapping(value = "content/{id}", method = RequestMethod.GET)
	public String content(@PathVariable("id") Long id, PmPermissionVo permissionVo, Model model) {
		ApplyServiceReceive entity = getEntity(id);
		model.addAttribute("approveStatusJson",
				JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_APPROVESTATUS)));
		// 收款方式
		model.addAttribute("receiveModeJson", JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_MODE_APPLYRECEIVE)));
		model.addAttribute("entity", entity);
		 if(entity.getContractId()!=null && entity.getContractId()>0) {
			CtrContract contract = contractClient.getEntity(entity.getContractId());
			model.addAttribute("contract", contract);
		}
		 //我方抬头
		model.addAttribute("ourCompanyJson",
				JsonUtil.obj2Json(BsCompanyOurUtil.getCompanyOurToBsDictDataList()));
		// 处理审批中部分控件可编辑
		permissionVo = webParamUtils.verifyPermission(permissionVo, entity.getApproveId());
		model.addAttribute("psv", permissionVo);
		return "apply/serviceReceive-content";
	}
	@ModelAttribute("preload")
	public ApplyServiceReceive getEntity(@RequestParam(value = "id", required = false) Long id) {
		if (id != null) {
			if (id > 0) {
				return getService().getEntity(id);
			} else {
				ApplyServiceReceive entity = new ApplyServiceReceive();
				entity.setId(0L);
				entity.setStatus(BasConstants.APPROVE_STATUS_N);
				entity.setReceiveDate(new Date());
				return entity;
			}
		}
		return null;
	}
	
	
	@RequestMapping(value = "updateFileId", method = RequestMethod.POST)
	public void updateFileId(FileIdUpdateVo vo,
			HttpServletResponse response) {
		try {
			applyServiceReceiveClient.updateFileId(vo);
			RenderUtil.renderSuccess("success", response);
		} catch (Exception e) {
			logger.error("errorId:", e);
			RenderUtil.renderFailure("errorId:" + e.getMessage(), response);
		}
	}
}
