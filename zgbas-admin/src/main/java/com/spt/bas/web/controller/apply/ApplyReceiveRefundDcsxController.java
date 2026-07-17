package com.spt.bas.web.controller.apply;

import com.spt.bas.client.cache.BsCompanyOurUtil;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.*;
import com.spt.bas.client.remote.*;
import com.spt.bas.client.vo.BsCompanyOurSearchVo;
import com.spt.bas.client.vo.FileIdUpdateVo;
import com.spt.bas.web.shiro.ShiroUtil;
import com.spt.bas.web.util.StringUtils;
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
import java.util.Objects;

/**
 * 代采赊销 收退款申请
 */
@Controller
@RequestMapping(value = "/apply/receiveRefundDcsx")
public class ApplyReceiveRefundDcsxController extends PageController<ApplyReceiveRefundDcsx, BaseVo>{
	@Autowired
	private IApplyReceiveRefundDcsxClient applyReceiveRefundDcsxClient;
	@Autowired
	private IBsCompanyOurClient bsCompanyOurClient;
	@Autowired
	private IBsCompanyDcsxClient bsCompanyDcsxClient;
	@Resource
	private WebParamUtils webParamUtils;
	@Override
	public BaseClient<ApplyReceiveRefundDcsx> getService() {
		return applyReceiveRefundDcsxClient;
	}


	/** 审批模板内容 */
	@RequestMapping(value = "content/{id}", method = RequestMethod.GET)
	public String content(@PathVariable("id") Long id, PmPermissionVo permissionVo, Model model) {
		ApplyReceiveRefundDcsx entity = getEntity(id);
		model.addAttribute("entity", entity);
		 //我方抬头
		model.addAttribute("ourCompanyJson", JsonUtil.obj2Json(BsCompanyOurUtil.getCompanyOurToBsDictDataList()));
		//处理审批中部分控件可编辑
		permissionVo = webParamUtils.verifyPermission(permissionVo, entity.getApproveId());
		model.addAttribute("psv", permissionVo);
		return "apply/receiveRefundDcsx-content";
	}

	@RequestMapping(value = "content2/{id}", method = RequestMethod.GET)
	public String content2(@PathVariable("id") Long id, PmPermissionVo permissionVo, Model model) {
		ApplyReceiveRefundDcsx entity = getEntity(id);
		model.addAttribute("entity", entity);
		 //我方抬头
		model.addAttribute("ourCompanyJson", JsonUtil.obj2Json(BsCompanyOurUtil.getCompanyOurToBsDictDataList()));
		//处理审批中部分控件可编辑
		permissionVo = webParamUtils.verifyPermission(permissionVo, entity.getApproveId());
		model.addAttribute("psv", permissionVo);
		return "apply/receiveRefundDcsx-content";
	}
	@ModelAttribute("preload")
	public ApplyReceiveRefundDcsx getEntity(@RequestParam(value = "id", required = false) Long id) {
		if (id != null) {
			if (id > 0) {
				return getService().getEntity(id);
			} else {
				ApplyReceiveRefundDcsx entity = new ApplyReceiveRefundDcsx();
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
			applyReceiveRefundDcsxClient.updateFileId(vo);
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
		Page<ApplyCancelDetail> page = applyReceiveRefundDcsxClient.findPageDetail(searchVo);

		JsonEasyUI.renderJson(response, page);
	}

	/**
	 * 查询公司默认账号
	 *
	 * @param response
	 * @param request
	 */
	@RequestMapping(value = "findOurCompany", method = RequestMethod.POST)
	public void findOurCompany(BsCompanyOurSearchVo searchVo, HttpServletResponse response, HttpServletRequest request) {
		if (StringUtils.isNotEmpty(searchVo.getCompanyName())) {
			BsCompanyOur companyOur = bsCompanyOurClient.getCompanyOurDetail(searchVo);
			if(Objects.nonNull(companyOur)){
				RenderUtil.renderJson(companyOur, response);
			} else {
				BsCompanyDcsx bsCompanyDcsx = bsCompanyDcsxClient.findByCompanyName(searchVo.getCompanyName());
				if(Objects.nonNull(bsCompanyDcsx)) {
					RenderUtil.renderJson(bsCompanyDcsx, response);
				} else {
					RenderUtil.renderFailure("fail", response);
				}
			}

		}
	}

}
