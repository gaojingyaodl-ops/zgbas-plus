package com.spt.bas.web.controller.apply;

import com.spt.bas.client.cache.BsCompanyOurUtil;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.ApplyCancelDetail;
import com.spt.bas.client.entity.ApplyPayRefundDcsx;
import com.spt.bas.client.entity.BsCompanyDcsx;
import com.spt.bas.client.entity.BsCompanyOur;
import com.spt.bas.client.remote.IApplyPayRefundDcsxClient;
import com.spt.bas.client.remote.IBsCompanyDcsxClient;
import com.spt.bas.client.remote.IBsCompanyOurClient;
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
 * 代采赊销 付退款申请
 *
 */
@Controller
@RequestMapping(value = "/apply/payRefundDcsx")
public class ApplyPayRefundDcsxController extends PageController<ApplyPayRefundDcsx, BaseVo> {
	@Autowired
	private IApplyPayRefundDcsxClient payRefundDcsxClient;
	@Resource
	private WebParamUtils webParamUtils;
	@Autowired
	private IBsCompanyOurClient bsCompanyOurClient;
	@Autowired
	private IBsCompanyDcsxClient bsCompanyDcsxClient;
	@Override
	public BaseClient<ApplyPayRefundDcsx> getService() {
		return payRefundDcsxClient;
	}


	/** 审批模板内容 */
	@RequestMapping(value = "content/{id}", method = RequestMethod.GET)
	public String content(@PathVariable("id") Long id, PmPermissionVo permissionVo, Model model) {
		ApplyPayRefundDcsx entity = getEntity(id);
		model.addAttribute("entity", entity);
		//我方抬头
		model.addAttribute("ourCompanyJson", JsonUtil.obj2Json(BsCompanyOurUtil.getCompanyOurToBsDictDataList()));
		//处理审批中部分控件可编辑
		permissionVo = webParamUtils.verifyPermission(permissionVo, entity.getApproveId());
		model.addAttribute("psv", permissionVo);
		return "apply/payRefundDcsx-content";
	}

	@RequestMapping(value = "content2/{id}", method = RequestMethod.GET)
	public String content2(@PathVariable("id") Long id, PmPermissionVo permissionVo, Model model) {
		ApplyPayRefundDcsx entity = getEntity(id);
		model.addAttribute("entity", entity);
		//我方抬头
		model.addAttribute("ourCompanyJson", JsonUtil.obj2Json(BsCompanyOurUtil.getCompanyOurToBsDictDataList()));
		//处理审批中部分控件可编辑
		permissionVo = webParamUtils.verifyPermission(permissionVo, entity.getApproveId());
		model.addAttribute("psv", permissionVo);
		return "apply/payRefundDcsx-content";
	}
	@RequestMapping(value = "updateFileId", method = RequestMethod.POST)
	public void updateFileId(FileIdUpdateVo vo,
			HttpServletResponse response) {
		try {
			payRefundDcsxClient.updateFileId(vo);
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
	public ApplyPayRefundDcsx getEntity(@RequestParam(value = "id", required = false) Long id) {
		if (id != null) {
			if (id > 0) {
				return getService().getEntity(id);
			} else {
				ApplyPayRefundDcsx entity = new ApplyPayRefundDcsx();
				entity.setId(0L);
				entity.setStatus(BasConstants.APPROVE_STATUS_N);
				entity.setRefundDate(new Date());
				return entity;
			}
		}
		return null;
	}

	@RequestMapping(value = "queryCancelList", method = RequestMethod.POST)
	public void queryCancelList(PageSearchVo searchVo, HttpServletRequest request, HttpServletResponse response) {
		initSearch(searchVo, request);
		Map<String, Object> map = searchVo.getSearchParams();
		map.put("NEQS_status", BasConstants.APPROVE_STATUS_C);
		map.put("EQL_enterpriseId", ShiroUtil.getEnterpriseId());
		Page<ApplyCancelDetail> page = payRefundDcsxClient.findPageDetail(searchVo);

		JsonEasyUI.renderJson(response, page);
	}

	/**
	 * 查询我方公司默认账号
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
