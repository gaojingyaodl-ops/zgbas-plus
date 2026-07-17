package com.spt.bas.web.controller.bs;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.google.common.collect.Maps;
import com.spt.auth.sdk.cache.DictUtil;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.BsContractTemplate;
import com.spt.bas.client.remote.IBsContractTemplateClient;
import com.spt.bas.client.vo.FileIdUpdateVo;
import com.spt.bas.web.shiro.ShiroUtil;
import com.spt.bas.web.util.LogUtil;
import com.spt.tools.core.bean.PageSearchVo;
import com.spt.tools.core.json.JsonUtil;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.data.vo.BaseVo;
import com.spt.tools.data.vo.PageDown;
import com.spt.tools.web.controller.PageController;
import com.spt.tools.web.util.JsonEasyUI;
import com.spt.tools.web.util.RenderUtil;

/**
 * 合同模板配置
 * @author zhouzihang
 */
@Controller
@RequestMapping(value = "/bs/contractTemplate")
public class BsContractTemplateController extends PageController<BsContractTemplate, BaseVo>{
	@Autowired
	private IBsContractTemplateClient contractTemplateClient;
	@Autowired
	private IBsContractTemplateClient bsContractTemplateClient;
	@Override
	public BaseClient<BsContractTemplate> getService() {
		return contractTemplateClient;
	}
	// 转发合同模板配置页面
	@RequestMapping(value = "")
	public String index(Model model) {
		model.addAttribute("contractType",
				JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_CONTRACTTYPE)));
		return "bs/contractTemplate";
	}
	// 展示合同模板信息分页
	@RequestMapping(value = "findTemplateList")
	public void findTemplateList(PageSearchVo searchVo, HttpServletRequest request, HttpServletResponse response) {
		initSearch(searchVo, request);
		Map<String, Object> params = searchVo.getSearchParams();
		params.put("EQL_enterpriseId", ShiroUtil.getEnterpriseId());
		searchVo.setSearchParams(params);
		PageDown<BsContractTemplate> page = getService().findPage(searchVo);
		JsonEasyUI.renderJson(response, page);
	}
	// 查看合同模板详情
	@RequestMapping(value = "detail/{id}", method = RequestMethod.GET)
	public String detail(@PathVariable("id") Long id, Model model) {
		BsContractTemplate contractTemplate = getEntity(id);
		model.addAttribute("entity", contractTemplate);
		return "bs/contractTemplate-detail";
	}
	// 更改附件ID
	@RequestMapping(value = "updateFileId", method = RequestMethod.POST)
	public void updateFileId(FileIdUpdateVo vo, HttpServletResponse response) {
		try {
			contractTemplateClient.updateFileId(vo);
			RenderUtil.renderSuccess("success", response);
		} catch (Exception e) {
			logger.error("errorId:", e);
			RenderUtil.renderFailure("errorId:" + e.getMessage(), response);
		}
	}
	// 保存/更新合同模板
	@RequestMapping(value = "saveTemplate")
	public void saveTemplate(@Valid @ModelAttribute("preload")BsContractTemplate contractTemplate, HttpServletRequest request, HttpServletResponse response) {
		try {
			BsContractTemplate oldContractTemplate = contractTemplateClient.getEntity(contractTemplate.getId());
			contractTemplate.setEnterpriseId(ShiroUtil.getEnterpriseId());
			contractTemplateClient.save(contractTemplate);
			LogUtil.saveOrUpdate(request, oldContractTemplate, contractTemplate, contractTemplate.getId());// 记录日志
			RenderUtil.renderSuccess("success", response);
		} catch (Exception e) {
			logger.error("saveTemplate:", e);
			RenderUtil.renderFailure("saveTemplate:" + e.getMessage(), response);
		}
	}
	@ModelAttribute("preload")
	public BsContractTemplate getEntity(@RequestParam(value = "id", required = false) Long id) {
		BsContractTemplate entity = null;
		if (id != null) {
			if (id > 0L)
				entity = getService().getEntity(id);
			else {
				entity = new BsContractTemplate();
				entity.setId(0L);
			}
		}
		return entity;
	}
	
	@Override
	public Map<String, Object> getDefaultFilter() {
		Map<String, Object> map = Maps.newHashMap();
		map.put("EQL_enterpriseId", ShiroUtil.getEnterpriseId());
		return map;
	}

	@RequestMapping("viewContractTemplate/{id}")
	public String viewContractTemplate(@PathVariable("id") Long templateId,Model model) {
		//采购合同模板
		BsContractTemplate template = new BsContractTemplate();
		template.setEnterpriseId(ShiroUtil.getEnterpriseId());
		template.setId(templateId);
		BsContractTemplate bsContractTemplate = bsContractTemplateClient.findByIdAndEnterpriseId(template);
		if (bsContractTemplate != null && !StringUtils.isEmpty(bsContractTemplate.getContent())) {
			model.addAttribute("contractText", bsContractTemplate.getContent());
		}else{
			model.addAttribute("contractText", "未找到对应电子合同！");
		}
		return  "ctr/contractText";
	}

}
