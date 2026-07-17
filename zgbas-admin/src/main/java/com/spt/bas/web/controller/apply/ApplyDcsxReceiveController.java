package com.spt.bas.web.controller.apply;

import com.spt.auth.sdk.cache.DictUtil;
import com.spt.bas.client.cache.BsCompanyOurUtil;
import com.spt.bas.client.cache.BsDictUtil;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.constant.BsDictConstants;
import com.spt.bas.client.entity.ApplyReceive;
import com.spt.bas.client.entity.BsDictData;
import com.spt.bas.client.remote.IApplyReceiveClient;
import com.spt.bas.web.shiro.ShiroUtil;
import com.spt.bas.web.util.WebParamUtils;
import com.spt.pm.vo.PmPermissionVo;
import com.spt.tools.core.exception.WebApplicationException;
import com.spt.tools.core.json.JsonUtil;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.data.vo.BaseVo;
import com.spt.tools.web.controller.PageController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping(value = "/apply/dcsxReceive")
public class ApplyDcsxReceiveController extends PageController<ApplyReceive, BaseVo>{
	@Autowired
	private IApplyReceiveClient applyReceiveClient;
	@Resource
	private WebParamUtils webParamUtils;
	@Override
	public BaseClient<ApplyReceive> getService() {
		return applyReceiveClient;
	}
	
	
	/** 审批模板内容 */
	@RequestMapping(value = "content/{id}", method = RequestMethod.GET)
	public String content(@PathVariable("id") Long id, PmPermissionVo permissionVo, Model model) throws WebApplicationException {
		ApplyReceive entity = getEntity(id);
		model.addAttribute("approveStatusJson",
				JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_APPROVESTATUS)));
		List<BsDictData> lstPayType = BsDictUtil.getListByCategory(ShiroUtil.getEnterpriseId(),BsDictConstants.DICT_TYPE_RECEIVETYPE);
		String receiveTypeJson;
		if (lstPayType.isEmpty()) {
			//收款类型
			receiveTypeJson = JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.RECEIVE_TYPE));
		}else {
			receiveTypeJson = JsonUtil.obj2Json(lstPayType);
		}

		model.addAttribute("receiveTypeJson", receiveTypeJson);
		//收款方式
		model.addAttribute("receiveModeJson", JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_MODE_APPLYRECEIVE)));
		model.addAttribute("entity", entity);
		 //我方抬头
		model.addAttribute("ourCompanyJson",
				JsonUtil.obj2Json(BsCompanyOurUtil.getCompanyOurToBsDictDataList()));
		//处理审批中部分控件可编辑
		permissionVo = webParamUtils.verifyPermission(permissionVo, entity.getApproveId());
		model.addAttribute("psv", permissionVo);
		return "apply/receive-dcsx-content";
	}

	@RequestMapping(value = "content2/{id}", method = RequestMethod.GET)
	public String content2(@PathVariable("id") Long id, PmPermissionVo permissionVo, Model model) throws WebApplicationException {
		ApplyReceive entity = getEntity(id);
		model.addAttribute("approveStatusJson",
				JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_APPROVESTATUS)));
		List<BsDictData> lstPayType = BsDictUtil.getListByCategory(ShiroUtil.getEnterpriseId(),BsDictConstants.DICT_TYPE_RECEIVETYPE);
		String receiveTypeJson;
		if (lstPayType.isEmpty()) {
			//收款类型
			receiveTypeJson = JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.RECEIVE_TYPE));
		}else {
			receiveTypeJson = JsonUtil.obj2Json(lstPayType);
		}

		model.addAttribute("receiveTypeJson", receiveTypeJson);
		//收款方式
		model.addAttribute("receiveModeJson", JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_MODE_APPLYRECEIVE)));
		model.addAttribute("entity", entity);
		 //我方抬头
		model.addAttribute("ourCompanyJson",
				JsonUtil.obj2Json(BsCompanyOurUtil.getCompanyOurToBsDictDataList()));
		//处理审批中部分控件可编辑
		permissionVo = webParamUtils.verifyPermission(permissionVo, entity.getApproveId());
		model.addAttribute("psv", permissionVo);
		return "apply/receive-dcsx-content";
	}
	@ModelAttribute("preload")
	public ApplyReceive getEntity(@RequestParam(value = "id", required = false) Long id) {
		if (id != null) {
			if (id > 0)
				return getService().getEntity(id);
			else {
				ApplyReceive entity = new ApplyReceive();
				entity.setId(0l);
				entity.setStatus(BasConstants.APPROVE_STATUS_N);
				entity.setReceiveDate(new Date());
				return entity;
			}
		}
		return null;
	}

}
