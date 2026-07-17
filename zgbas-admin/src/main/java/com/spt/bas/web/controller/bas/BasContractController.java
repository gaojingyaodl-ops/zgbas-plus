/**
 * 
 */
package com.spt.bas.web.controller.bas;

import com.google.common.collect.Maps;
import com.spt.auth.sdk.cache.DictUtil;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.BasContract;
import com.spt.bas.client.entity.BsCompany;
import com.spt.bas.client.remote.IBasContractClient;
import com.spt.bas.client.remote.IBsCompanyClient;
import com.spt.bas.client.vo.BasContractExistVo;
import com.spt.bas.client.vo.BasContractVo;
import com.spt.bas.client.vo.ContractOpVo;
import com.spt.bas.client.vo.FileIdUpdateVo;
import com.spt.bas.web.shiro.ShiroUtil;
import com.spt.tools.core.bean.PageSearchVo;
import com.spt.tools.core.json.JsonUtil;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.data.vo.BaseVo;
import com.spt.tools.web.controller.PageController;
import com.spt.tools.web.util.JsonEasyUI;
import com.spt.tools.web.util.RenderUtil;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * 合同信息
 * @author wlddh
 *
 */
@Controller
@RequestMapping(value = "/bas/contract")
public class BasContractController extends PageController<BasContract, BaseVo> {


	@Autowired
	private IBasContractClient contractClient;
	@Autowired
	private IBsCompanyClient companyClient;
	@Autowired
	/*@Autowired
	private IBasContractOphisClient basContractOphisClient;*/
	
	@Override
	public BaseClient<BasContract> getService() {
		return contractClient;
	}

	
	@RequestMapping(value = "")
	public String index(Model model) {

		model.addAttribute("approveStatusJson", JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_APPROVESTATUS)));
		model.addAttribute("contractTypeJson",JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_CONTRACTTYPE)));
		model.addAttribute("contractStatusJson",JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_CONTRACTSTATUS)));
		List<BsCompany> lstCompany=	companyClient.findByEnterpriseId(ShiroUtil.getEnterpriseId());
		model.addAttribute("companyJson",JsonUtil.obj2Json(lstCompany));		
		return "bas/contract";
	}
	
	@RequestMapping(value = "choose")
	public String choose(Model model) {
		model.addAttribute("contractTypeJson",JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_CONTRACTTYPE)));
		model.addAttribute("contractStatusJson",JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_CONTRACTSTATUS)));
		return "bas/contract-choose";
	}
	@Override
	public Map<String, Object> getDefaultFilter() {
		Map<String, Object> map = Maps.newHashMap();
		map.put("EQL_enterpriseId", ShiroUtil.getEnterpriseId());
		return map;
	}
	@RequestMapping(value = "listChoose")
	public void listChoose(PageSearchVo searchVo, HttpServletRequest request, HttpServletResponse response) {
		
		initSearch(searchVo, request);
		Page<BasContractVo> page=	contractClient.findPageVo(searchVo);
		JsonEasyUI.renderJson(response, page);
	}
	/**
	 * 合同查询的显示list
	 * @param searchVo
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "queryContList")
	public void queryContList(PageSearchVo searchVo, HttpServletRequest request, HttpServletResponse response) {
		
		initSearch(searchVo, request);
		Page<BasContract> page= contractClient.findPageByContQuery(searchVo);
		
		JsonEasyUI.renderJson(response, page);
	}

	@RequestMapping(value = "detail/{id}", method = RequestMethod.GET)
	public String detail(@PathVariable("id") Long id, Model model) {
		BasContract entity = getEntity(id);
		model.addAttribute("entity", entity);
		return "bas/contract-detail";
	}
	
	@RequestMapping(value = "content/{id}", method = RequestMethod.GET)
	public String content(@PathVariable("id") Long id, Model model,HttpServletRequest req) {
		BasContract entity = getEntity(id);
		model.addAttribute("contractTypeJson",JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_CONTRACTTYPE)));
		model.addAttribute("contractStatusJson",JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_CONTRACTSTATUS)));
		List<BsCompany> lstCompany=	companyClient.findByEnterpriseId(ShiroUtil.getEnterpriseId());
		model.addAttribute("companyJson",JsonUtil.obj2Json(lstCompany));
		model.addAttribute("entity", entity);
		
		String canEditFile = req.getParameter("hasEditFile");
		boolean canEdit =BooleanUtils.toBoolean(canEditFile);
		model.addAttribute("hasEditFile", canEdit);
		
		return "bas/contract-content";
	}

	@RequestMapping(value = "save", method = RequestMethod.POST)
	public void save(@Valid @ModelAttribute("preload") BasContract entity, HttpServletRequest request,
			HttpServletResponse response) {
		try {
			entity = getService().save(entity);
			RenderUtil.renderSuccess(JsonUtil.obj2Json(entity), response);
		} catch (Exception e) {
			logger.error("errorId:", e);
			RenderUtil.renderFailure("errorId:" + e.getMessage(), response);
		}
	}

	
	@RequestMapping(value = "updateFileId", method = RequestMethod.POST)
	public void updateFileId(FileIdUpdateVo vo,
			HttpServletResponse response) {
		try {
			contractClient.updateFileId(vo);
			RenderUtil.renderSuccess("success", response);
		} catch (Exception e) {
			logger.error("errorId:", e);
			RenderUtil.renderFailure("errorId:" + e.getMessage(), response);
		}
	}
	
	
	/**
	 * Ajax请求校验
	 */
	@RequestMapping(value = "checkContractNo")
	public void checkContractNo(BasContractExistVo vo, HttpServletResponse response) {
		if (contractClient.existContractNo(vo)) {
			RenderUtil.renderText("false", response);
		} else {
			RenderUtil.renderText("true", response);
		}
	}
	
	/**
	 * 使用@ModelAttribute, 实现Struts2
	 * Preparable二次部分绑定的效果,先根据form的id从数据库查出Task对象,再把Form提交的内容绑定到该对象上。
	 * 因为仅update()方法的form中有id属性，因此本方法在该方法中执行.
	 */
	@ModelAttribute("preload")
	public BasContract getEntity(@RequestParam(value = "id", required = false) Long id) {
		if (id != null) {
			if (id > 0)
				return getService().getEntity(id);
			else {
				BasContract entity = new BasContract();
				entity.setId(0l);
				entity.setStatus(BasConstants.CONTRACTSTATUS_NEW);
				entity.setWarehouse("长江国际");
				entity.setNumberUnit("吨");
				return entity;
			}
		}
		return null;
	}
	/**
	 * 已收款
	 */
	@RequestMapping(value = "updateContractStatusByFond")
	public void updateContractStatusByFond(@RequestParam(value = "id", required = false) Long id,HttpServletResponse response) {
		ContractOpVo opVo=new ContractOpVo();
		opVo.setId(id);
		opVo.setContractStatus(BasConstants.CONTRACTSTATUS_F2);
		opVo.setFondFlg(true);
		opVo.setCreateUserName(ShiroUtil.getCurrentUserName());
		opVo.setCreateUserId(ShiroUtil.getCurrentUserId());
		contractClient.doContractOp(opVo);
		
	}
	/**
	 * 已收票
	 */
	@RequestMapping(value = "updateContractStatusByBill")
	public void updateContractStatusByBill(@RequestParam(value = "id", required = false) Long id,HttpServletResponse response) {
		
		ContractOpVo opVo=new ContractOpVo();
		opVo.setId(id);
		opVo.setContractStatus(BasConstants.CONTRACTSTATUS_V1);
		opVo.setBillFlg(true);
		opVo.setCreateUserName(ShiroUtil.getCurrentUserName());
		opVo.setCreateUserId(ShiroUtil.getCurrentUserId());
		contractClient.doContractOp(opVo);
	}
	//已收货
	@RequestMapping(value = "updateContractStatusToG1")
	public void updateContractStatusToG1(@RequestParam(value = "id", required = false) Long id,HttpServletResponse response) {
		ContractOpVo opVo=new ContractOpVo();
		opVo.setId(id);
		opVo.setContractStatus(BasConstants.CONTRACTSTATUS_G1);
		opVo.setPayFlg(true);
		opVo.setCreateUserName(ShiroUtil.getCurrentUserName());
		opVo.setCreateUserId(ShiroUtil.getCurrentUserId());
		contractClient.doContractOp(opVo);
	}
	//已发货
	@RequestMapping(value = "updateContractStatusToG2")
	public void updateContractStatusToG2(@RequestParam(value = "id", required = false) Long id,HttpServletResponse response) {
		ContractOpVo opVo=new ContractOpVo();
		opVo.setId(id);
		opVo.setContractStatus(BasConstants.CONTRACTSTATUS_G2);
		opVo.setPayFlg(true);
		opVo.setCreateUserName(ShiroUtil.getCurrentUserName());
		opVo.setCreateUserId(ShiroUtil.getCurrentUserId());
		contractClient.doContractOp(opVo);
	}
	
}
