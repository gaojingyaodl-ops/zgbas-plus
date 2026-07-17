package com.spt.bas.web.controller.apply;

import com.spt.bas.client.cache.BsCompanyOurUtil;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.ApplyCancelDetail;
import com.spt.bas.client.entity.ApplyInvoiceReceived;
import com.spt.bas.client.entity.CtrContract;
import com.spt.bas.client.entity.FileProcessRel;
import com.spt.bas.client.remote.IApplyInvoiceReceivedClient;
import com.spt.bas.client.remote.ICtrContractClient;
import com.spt.bas.client.remote.IFileProcessRelClient;
import com.spt.bas.client.vo.ApplyInvoiceReceivedVo;
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
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 收票
 */
@Controller
@RequestMapping(value = "/apply/invoiceReceived")
public class ApplyInvoiceReceivedController extends PageController<ApplyInvoiceReceived, BaseVo>{
	@Autowired
	private IApplyInvoiceReceivedClient invoiceReceivedClient;
	@Autowired
	private IFileProcessRelClient fileProcessRelClient;
	@Autowired
	private ICtrContractClient ctrContractClient;
	@Resource
	private WebParamUtils webParamUtils;

	@Override
	public BaseClient<ApplyInvoiceReceived> getService() {
		// TODO Auto-generated method stub
		return invoiceReceivedClient;
	}
	@RequestMapping(value = "updateFileId", method = RequestMethod.POST)
	public void updateFileId(FileIdUpdateVo vo,
			HttpServletResponse response) {
		try {
			invoiceReceivedClient.updateFileId(vo);
			RenderUtil.renderSuccess("success", response);
		} catch (Exception e) {
			logger.error("errorId:", e);
			RenderUtil.renderFailure("errorId:" + e.getMessage(), response);
		}
	}
	/** 审批模板内容 */
	@RequestMapping(value = "content/{id}", method = RequestMethod.GET)
	public String content(@PathVariable("id") Long id,PmPermissionVo permissionVo, Model model,HttpServletRequest request) {
		ApplyInvoiceReceived entity = getEntity(id);
		ApplyInvoiceReceivedVo vo=new ApplyInvoiceReceivedVo();
		if(entity.getContractId()!=null){
			CtrContract byContractNo = ctrContractClient.findByContractId(entity.getContractId());
			vo.setBilledAmount(byContractNo.getBilledAmount());
		}
		BeanUtils.copyProperties(entity, vo);

		model.addAttribute("entity", vo);
		model.addAttribute("ourCompanyJson",
				JsonUtil.obj2Json(BsCompanyOurUtil.getCompanyOurToBsDictDataList()));

		String processCode = request.getParameter("processCode");
		// 附件类型
		List<FileProcessRel> fileTypeList = fileProcessRelClient.findList(processCode);
		model.addAttribute("fileTypeJson", JsonUtil.obj2Json(fileTypeList));

		//处理审批中部分控件可编辑
		permissionVo = webParamUtils.verifyPermission(permissionVo, entity.getApproveId());
		model.addAttribute("psv", permissionVo);
		return "apply/invoiceReceived-content";
	}

	@ModelAttribute("preload")
	public ApplyInvoiceReceived getEntity(@RequestParam(value = "id", required = false) Long id) {
		if (id != null) {
			if (id > 0) {
				return getService().getEntity(id);
			} else {
				ApplyInvoiceReceived entity = new ApplyInvoiceReceived();
				entity.setId(0L);
				entity.setStatus(BasConstants.APPROVE_STATUS_N);
				entity.setInInvoiceDate(new Date());
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
		Page<ApplyCancelDetail> page = invoiceReceivedClient.findPageDetail(searchVo);

		JsonEasyUI.renderJson(response, page);
	}
}
