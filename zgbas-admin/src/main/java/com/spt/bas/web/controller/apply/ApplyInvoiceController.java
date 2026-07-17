package com.spt.bas.web.controller.apply;

import com.spt.auth.sdk.cache.DictUtil;
import com.spt.bas.client.cache.BsCompanyOurUtil;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.*;
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
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 开票
 */
@Controller
@RequestMapping(value = "/apply/invoice")
public class ApplyInvoiceController extends PageController<ApplyInvoice, BaseVo>{

	@Autowired
	private IApplyInvoiceClient invoiceClient;
	@Autowired
	private ICtrContractClient ctrContractClient;
	@Resource
	private WebParamUtils webParamUtils;
	@Autowired
	private IBsProductTypeClient productTypeClient;
	@Autowired
	private ISealUsageClient sealUsageClient;
	@Value("${file.show.url}")
	private String fileUrl;
	@Autowired
	private IBasBrandClient brandClient;

	@Override
	public BaseClient<ApplyInvoice> getService() {
		return invoiceClient;
	}

	/** 审批模板内容 */
	@RequestMapping(value = "content/{id}", method = RequestMethod.GET)
	public String content(@PathVariable("id") Long id, PmPermissionVo permissionVo, Model model,HttpServletRequest request) {
		ApplyInvoice entity = getEntity(id);
		model.addAttribute("approveStatusJson",
				JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_APPROVESTATUS)));
		//我方抬头
		model.addAttribute("ourCompanyJson",
				JsonUtil.obj2Json(BsCompanyOurUtil.getCompanyOurToBsDictDataList()));
		//包装规格-全部
		model.addAttribute("packingSpecificaJson",
				JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_IMPORTBUYPACKING)));
		model.addAttribute("entity", entity);
		if(entity.getContractId() != null) {
			CtrContract contract = ctrContractClient.getEntity(entity.getContractId());
			model.addAttribute("productsName", contract.getProductsName());
			model.addAttribute("contractContent", contract);
		}
		//处理审批中部分控件可编辑
		permissionVo = webParamUtils.verifyPermission(permissionVo, entity.getApproveId());
		model.addAttribute("psv", permissionVo);

		model.addAttribute("isServiceInvoice", request.getParameter("isServiceInvoice"));

		// 货品树
		model.addAttribute("productTypeJson",
				JsonUtil.obj2Json(productTypeClient.findAllProductTree(ShiroUtil.getEnterpriseId())));
		// 双签展示路径
		model.addAttribute("fileServerUrl",fileUrl+"/view/show/");

		List<BasBrand> lstBrand = brandClient.findSafeBrand();
		model.addAttribute("brandJson", JsonUtil.obj2Json(lstBrand));

		return "apply/invoice-content";
	}

	@RequestMapping(value = "updateFileId", method = RequestMethod.POST)
	public void updateFileId(FileIdUpdateVo vo,
			HttpServletResponse response) {
		try {
			invoiceClient.updateFileId(vo);
			RenderUtil.renderSuccess("success", response);
		} catch (Exception e) {
			logger.error("errorId:", e);
			RenderUtil.renderFailure("errorId:" + e.getMessage(), response);
		}
	}

	@ModelAttribute("preload")
	public ApplyInvoice getEntity(@RequestParam(value = "id", required = false) Long id) {
		if (id != null) {
			if (id > 0){
				return getService().getEntity(id);
			}else {
				ApplyInvoice entity = new ApplyInvoice();
				entity.setId(0l);
				entity.setStatus(BasConstants.APPROVE_STATUS_N);
				entity.setInvoiceDate(new Date());
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
		Page<ApplyCancelDetail> page = invoiceClient.findPageDetail(searchVo);

		JsonEasyUI.renderJson(response, page);
	}

	// 获取双签文件
	@RequestMapping(value = "findSealByContractId", method = RequestMethod.POST)
	public void findSealByContractId(@RequestParam("contractId")Long contractId, HttpServletRequest request, HttpServletResponse response) {
		List<SealUsage> sealUsageList = sealUsageClient.findByContractId(contractId);
		List<String> sealFileId = new ArrayList<>();
		if(CollectionUtils.isNotEmpty(sealUsageList)){
			// 数据库中业务盖章附件合同有的关联多个，取第一个
			SealUsage sealUsage = sealUsageList.get(0);
			String fileId = sealUsage.getFileId();
			if(!Objects.isNull(fileId)){
				sealFileId = Arrays.stream(fileId.split(",")).filter(it -> !it.isEmpty()).collect(Collectors.toList());
			}
		}
        JsonEasyUI.renderListJson(response,sealFileId);
	}
}
