package com.spt.bas.web.controller.bs;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.alibaba.fastjson.JSON;
import com.spt.auth.sdk.cache.DictUtil;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.BsCompany;
import com.spt.bas.client.entity.BsCompanyEvaluate;
import com.spt.bas.client.remote.IBsCompanyClient;
import com.spt.bas.client.remote.IBsCompanyEvaluateClient;
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

@Controller
@RequestMapping(value = "/bs/companyEvaluate")
public class BsCompanyEvaluateController extends PageController<BsCompanyEvaluate, BaseVo>{
	
	@Autowired
	private IBsCompanyEvaluateClient companyEvaluateClient;
	@Autowired
	private IBsCompanyClient companyClient;
	@Override
	public BaseClient<BsCompanyEvaluate> getService() {
		// TODO Auto-generated method stub
		return companyEvaluateClient;
	}
	
	@RequestMapping(value = "")
	public String index(Model model,Long id) {
		model.addAttribute("followTypeJson", 
				JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_FOLLOWTYPE)));
		List<BsCompany> lstCompany = companyClient.findAll();
		model.addAttribute("companyJson", JsonUtil.obj2Json(lstCompany));
		model.addAttribute("id", id);
		return "bs/companyEvaluate";
	}
	
	@RequestMapping(value = "detail/{id}", method = RequestMethod.GET)
	public String detail(@PathVariable("id") Long id, Model model) {
		BsCompanyEvaluate entity;
		if(id!=null&&id!=0){
			entity = getEntity(id);
			model.addAttribute("entity", entity);
		}else{
			entity = new BsCompanyEvaluate();
			entity.setId(0l);
		}
		/*model.addAttribute("followTypeJson", 
				JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_FOLLOWTYPE)));*/
		List<BsCompany> lstCompany = companyClient.findAll();
		model.addAttribute("companyJson", JsonUtil.obj2Json(lstCompany));
		model.addAttribute("entity", entity);
		return "bs/companyEvaluate-detail";
	}
	
	@ModelAttribute("preload")
	public BsCompanyEvaluate getEntity(@RequestParam(value = "id", required = false) Long id) {
		if (id != null) {
			if (id > 0)
				return getService().getEntity(id);
			else {
				BsCompanyEvaluate entity = new BsCompanyEvaluate();
				entity.setId(0l);
				return entity;
			}
		}
		return null;
	}
	
	@RequestMapping(value = "save", method = RequestMethod.POST)
	public void save(@Valid @ModelAttribute("preload") BsCompanyEvaluate companyEvaluate,HttpServletRequest request, HttpServletResponse response) {
		try{
			BsCompanyEvaluate old = companyEvaluateClient.getEntity(companyEvaluate.getId());
			LogUtil.saveOrUpdate(request, old, companyEvaluate, companyEvaluate.getId());//记录日志
			companyEvaluate.setEnterpriseId(ShiroUtil.getEnterpriseId());
			companyEvaluate.setCreateUserId(ShiroUtil.getCurrentUserId());
			companyEvaluate.setCreateUserName(ShiroUtil.getCurrentUserName());
			companyEvaluateClient.save(companyEvaluate);
			RenderUtil.renderText("success", response);
		}catch(Exception e){
			e.printStackTrace();
			RenderUtil.renderText("fail", response);
		}
		
	}
	
	@RequestMapping("/findListByCompanyId")
	public void findListByCompanyId(@RequestParam("id") Long id,PageSearchVo queryVo,HttpServletResponse response,HttpServletRequest request){
		Map<String,Object> map=new HashMap<String,Object>();
		map.put("EQL_companyId", id);
		initSearch2(queryVo, request, map);
		PageDown<BsCompanyEvaluate> page=companyEvaluateClient.findPage(queryVo);
		BsCompanyEvaluate e = sumPage(request, response);
		Map<String, Object> footer = null;
		if (e != null) {
			footer = entity2Footer(e);
		}
		
		JsonEasyUI.renderJson(response, page,footer);
	}
	
	@RequestMapping("/sav")
	public void sav (@RequestParam("param") String param,HttpServletResponse response){
		Long userId=ShiroUtil.getCurrentUserId();
		String userName=ShiroUtil.getCurrentUserName(); 
		List<BsCompanyEvaluate> list=JSON.parseArray(param, BsCompanyEvaluate.class);
		try {
			for(BsCompanyEvaluate entity:list){
				entity.setCreateUserId(userId);
				entity.setEnterpriseId(ShiroUtil.getEnterpriseId());
				entity.setCreateUserName(userName);
				companyEvaluateClient.save(entity);
			}
			RenderUtil.renderSuccess("success", response);
		} catch (Exception e) {
			e.printStackTrace();
			RenderUtil.renderFailure("fail", response);
		}
		
	}

}
