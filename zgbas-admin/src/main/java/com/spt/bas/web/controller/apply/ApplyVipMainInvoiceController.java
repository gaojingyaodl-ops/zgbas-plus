package com.spt.bas.web.controller.apply;

import com.spt.auth.sdk.cache.DictUtil;
import com.spt.bas.client.cache.BsCompanyOurUtil;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.ApplyVipMainInvoice;
import com.spt.bas.client.entity.CtrContract;
import com.spt.bas.client.entity.FileProcessRel;
import com.spt.bas.client.remote.IApplyVipMainInvoiceClient;
import com.spt.bas.client.remote.ICtrContractClient;
import com.spt.bas.client.remote.IFileProcessRelClient;
import com.spt.bas.client.remote.IPmApproveContentsClient;
import com.spt.bas.client.vo.FileIdUpdateVo;
import com.spt.bas.web.util.ProcessControlUtil;
import com.spt.bas.web.util.WebParamUtils;
import com.spt.pm.entity.PmApproveContents;
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

/**
 * vip开票
 */
@Controller
@RequestMapping(value = "/apply/vipMainInvoice")
public class ApplyVipMainInvoiceController extends PageController<ApplyVipMainInvoice, BaseVo>{

	@Autowired
	private IApplyVipMainInvoiceClient applyVipMainInvoiceClient;
	@Autowired
	private ICtrContractClient ctrContractClient;
	@Autowired
	private IFileProcessRelClient fileProcessRelClient;
	@Autowired
	private IPmApproveContentsClient pmApproveContentsClient;
	@Resource
	private WebParamUtils webParamUtils;
	@Override
	public BaseClient<ApplyVipMainInvoice> getService() {
		return applyVipMainInvoiceClient;
	}

	/** 审批模板内容 */
	@RequestMapping(value = "content/{id}", method = RequestMethod.GET)
	public String content(@PathVariable("id") Long id, PmPermissionVo permissionVo, Model model,HttpServletRequest request) {
		String processCode = request.getParameter("processCode");
		ApplyVipMainInvoice entity = getEntity(id,processCode);
		model.addAttribute("approveStatusJson",
				JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_APPROVESTATUS)));
		//我方抬头
		model.addAttribute("ourCompanyJson",
				JsonUtil.obj2Json(BsCompanyOurUtil.getCompanyOurToBsDictDataList()));

			model.addAttribute("entity", entity);
			model.addAttribute("status", getStatus(id));
		if(entity.getContractId() != null) {
			CtrContract contract = ctrContractClient.getEntity(entity.getContractId());
			model.addAttribute("productsName", contract.getProductsName());
			model.addAttribute("contractContent", contract);
		}


		// 附件类型
		List<FileProcessRel> fileTypeList = fileProcessRelClient.findList(processCode);
		model.addAttribute("fileTypeJson", JsonUtil.obj2Json(fileTypeList));

		//处理审批中部分控件可编辑
		permissionVo = webParamUtils.verifyPermission(permissionVo, entity.getApproveId());
		model.addAttribute("psv", permissionVo);

		model.addAttribute("isServiceInvoice", request.getParameter("isServiceInvoice"));

		return "apply/vip-main-invoice-content";
	}

	@RequestMapping(value = "updateFileId", method = RequestMethod.POST)
	public void updateFileId(FileIdUpdateVo vo,
			HttpServletResponse response) {
		try {
			applyVipMainInvoiceClient.updateFileId(vo);
			RenderUtil.renderSuccess("success", response);
		} catch (Exception e) {
			logger.error("errorId:", e);
			RenderUtil.renderFailure("errorId:" + e.getMessage(), response);
		}
	}

	@ModelAttribute("preload")
	public ApplyVipMainInvoice getEntity(@RequestParam(value = "id", required = false) Long id, @RequestParam(value = "processCode", required = false) String processCode) {
		ApplyVipMainInvoice enObject = null;
		if (id != null) {
			if (id > 0 && StringUtils.isNotBlank(processCode)) {
				enObject = (ApplyVipMainInvoice) ProcessControlUtil.getEntity(id, processCode);
				if (enObject != null) {
					enObject.setId(id);
				}
				return enObject;
			} else {
				ApplyVipMainInvoice applyVipInvoice = new ApplyVipMainInvoice();
				applyVipInvoice.setId(0L);
				return applyVipInvoice;
			}
		}
		return enObject;
	}

	private String getStatus(Long id) {
		if (id != null && id > 0L) {
			PmApproveContents entity = pmApproveContentsClient.getEntity(id);
			if (entity != null) {
				return entity.getStatus();
			}
		}
		return BasConstants.APPROVE_STATUS_N;
	}

}
