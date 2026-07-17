package com.spt.bas.web.controller.bas;

import com.google.common.collect.Maps;
import com.spt.auth.sdk.cache.DictUtil;
import com.spt.auth.sdk.entity.SysDeptSdk;
import com.spt.auth.sdk.open.IAuthOpenFacade;
import com.spt.auth.sdk.vo.DeptSearchVo;
import com.spt.bas.client.cache.BsCompanyOurUtil;
import com.spt.bas.client.cache.BsDictUtil;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.BasContractRela;
import com.spt.bas.client.entity.BsCompany;
import com.spt.bas.client.remote.IBasContractRelaClient;
import com.spt.bas.client.remote.IBsCompanyClient;
import com.spt.bas.client.remote.IBsProductTypeClient;
import com.spt.bas.client.remote.IBsTemplateConfigClient;
import com.spt.bas.client.vo.*;
import com.spt.bas.web.shiro.ShiroUtil;
import com.spt.bas.web.util.EasyTreeUtil2;
import com.spt.tools.core.json.JsonUtil;
import com.spt.tools.data.easyui.EasyTreeNode;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.data.vo.BaseVo;
import com.spt.tools.data.vo.PageDown;
import com.spt.tools.web.controller.PageController;
import com.spt.tools.web.util.RenderUtil;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping(value = "/bas/contractRela")
public class BasContractRelaController extends PageController<BasContractRela, BaseVo> {
	@Autowired
	private IBasContractRelaClient contractRelaClient;
	@Autowired
	private IBsCompanyClient companyClient;
	@Autowired
	private IBsProductTypeClient productTypeClient;
	@Autowired
	private IBsTemplateConfigClient templateConfigClient;
	@Autowired
	private IAuthOpenFacade authOpenFacade;
	@Override
	public BaseClient<BasContractRela> getService() {
		// TODO Auto-generated method stub
		return contractRelaClient;
	}

	@RequestMapping(value = "")
	public String index(Model model) {
		return "bas/contractRela";
	}
	@Override
	public Map<String, Object> getDefaultFilter() {
		Map<String, Object> map = Maps.newHashMap();
		map.put("EQL_enterpriseId", ShiroUtil.getEnterpriseId());
		return map;
	}

	/** 审批模板内容 */
	@RequestMapping(value = "content/{id}", method = RequestMethod.GET)
	public String content(@PathVariable("id") Long id, Model model) {
		BasContractRela entity = getEntity(id);
		model.addAttribute("approveStatusJson",
				JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_APPROVESTATUS)));
		model.addAttribute("payTypeJson", JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_PAYTYPE)));
		BsCompanySearchVo queryVo =new BsCompanySearchVo();
		queryVo.setRows(1000);
		queryVo.setEnterpriseId(ShiroUtil.getEnterpriseId());
		queryVo.setUserId(ShiroUtil.getCurrentUserId());
//		queryVo.setMode(BasConstants.COMPANY_SEARCH_MODE_MY);
		PageDown<BsCompany> pageCompany= companyClient.findPageCompnay(queryVo);
		model.addAttribute("companyJson", JsonUtil.obj2Json(pageCompany.getContent()));
		model.addAttribute("contractTypeJson",JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_CONTRACTTYPE)));
		model.addAttribute("ourCompanyJson",JsonUtil.obj2Json(BsCompanyOurUtil.getCompanyOurToBsDictDataList()));
		model.addAttribute("entity", entity);
		//获取品名树
		model.addAttribute("productDeliveryJson",
				JsonUtil.obj2Json(productTypeClient.findAllProductTree(ShiroUtil.getEnterpriseId())));
		model.addAttribute("productJson",
				JsonUtil.obj2Json(productTypeClient.findAll()));
		DeptSearchVo deptSearchVo = new DeptSearchVo( ShiroUtil.getEnterpriseId());
		List<SysDeptSdk> deptList = authOpenFacade.findDeptAll(deptSearchVo);
		EasyTreeNode nodes = EasyTreeUtil2.getDeptTree(deptList, true);
		model.addAttribute("matchUserNameTree",JsonUtil.obj2Json(nodes.getChildren()));
		return "bas/contractRela-content";
	}

	//
	@RequestMapping(value = "template/{typeCd}", method = RequestMethod.GET)
	public void getTemplate(@PathVariable("typeCd") String typeCd,HttpServletResponse response) throws JSONException{
		TemplateQueryVo queryVo = new TemplateQueryVo();
		queryVo.setTypeCd(typeCd);
		List<String> dictCdList = new ArrayList<String>();
		dictCdList.add(BasConstants.TEMPLATE_CONTENT_DELIVERYMODE);
		dictCdList.add(BasConstants.TEMPLATE_CONTENT_WAREHOUSE);
		queryVo.setDictCdList(dictCdList);
		Map<String, List<DictDataVo>> map = this.templateConfigClient.getTemplateMap(queryVo);
		if(map!=null){
			RenderUtil.renderSuccess(JsonUtil.obj2Json(map), response);
		}else{
			RenderUtil.renderFailure("fail", response);;
		}

	}

	@ModelAttribute("preload")
	public BasContractRela getEntity(@RequestParam(value = "id", required = false) Long id) {

		if (id != null) {
			if (id > 0){
				BasContractRela rela = contractRelaClient.getEntity(id);
				return rela;
			}else {
				BasContractRelaVo entity = new BasContractRelaVo();
				entity.setId(0l);
				entity.setStatus(BasConstants.APPROVE_STATUS_N);
				return entity;
			}

		}
		return null;
	}

	@RequestMapping(value = "updateFileId", method = RequestMethod.POST)
	public void updateFileId(FileIdUpdateVo vo,
			HttpServletResponse response) {
		try {
			contractRelaClient.updateFileId(vo);
			RenderUtil.renderSuccess("success", response);
		} catch (Exception e) {
			logger.error("errorId:", e);
			RenderUtil.renderFailure("errorId:" + e.getMessage(), response);
		}
	}


}
