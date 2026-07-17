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
import com.google.common.collect.Maps;
import com.spt.auth.sdk.cache.DictUtil;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.BsCompany;
import com.spt.bas.client.entity.BsCompanyFollow;
import com.spt.bas.client.remote.IBsCompanyClient;
import com.spt.bas.client.remote.IBsCompanyFollowClient;
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
@RequestMapping(value = "/bs/companyFollow")
public class BsCompanyFollowController extends PageController<BsCompanyFollow, BaseVo>{
	
	@Autowired
	private IBsCompanyFollowClient companyFollowClient;
	@Autowired
	private IBsCompanyClient companyClient;
	
	@Override
	public BaseClient<BsCompanyFollow> getService() {
		// TODO Auto-generated method stub
		return companyFollowClient;
	}
	
	@Override
	public Map<String, Object> getDefaultFilter() {
		Map<String, Object> map = Maps.newHashMap();
		map.put("EQL_enterpriseId", ShiroUtil.getEnterpriseId());
		return map;
	}
	
	@RequestMapping(value = "")
	public String index(Model model,Long id) {
		
		if(null!=id){
			BsCompany entity = companyClient.getEntity(id);			
			Long userId=ShiroUtil.getCurrentUserId();
			String status=entity.getStatus();
			boolean isFollow=false;
			//判断是否是私海 同时是私海用户
			if(BasConstants.COMPANY_STATUS_F.equals(status) && userId==entity.getMatchUserId()){
				isFollow=true;
			}
			
			model.addAttribute("isFollow", isFollow);
		}
		
		
		model.addAttribute("id", id);
		
		model.addAttribute("followTypeJson", 
				JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_FOLLOWTYPE)));
		List<BsCompany> lstCompany = companyClient.findAll();
		model.addAttribute("companyJson", JsonUtil.obj2Json(lstCompany));
		return "bs/companyFollow";
	}
	
	@RequestMapping(value = "detail/{id}", method = RequestMethod.GET)
	public String detail(@PathVariable("id") Long id,@PathVariable("companyId") Long companyId, Model model) {
		BsCompanyFollow entity;
		if(id!=null&&id!=0){
			entity = getEntity(id);
			model.addAttribute("entity", entity);
		}else{
			entity = new BsCompanyFollow();
			entity.setId(0l);
		}
		model.addAttribute("followTypeJson", 
				JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_FOLLOWTYPE)));
		List<BsCompany> lstCompany = companyClient.findAll();
		model.addAttribute("companyJson", JsonUtil.obj2Json(lstCompany));
		model.addAttribute("entity", entity);
		return "bs/companyFollowDetail";
	}

	@RequestMapping(value = "/toDetail")
	public String toDetail(Model model,HttpServletRequest request) {
		BsCompanyFollow entity;
		String idStr = request.getParameter("id");
		String companyIdStr = request.getParameter("companyId");
		Long id = Long.valueOf(idStr);
		if(id!=null&&id!=0){
			entity = getEntity(id);
			model.addAttribute("entity", entity);
		}else{
			entity = new BsCompanyFollow();
			entity.setId(0l);
		}
		model.addAttribute("followTypeJson",
				JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_FOLLOWTYPE)));
		List<BsCompany> lstCompany = companyClient.findAll();
		model.addAttribute("companyJson", JsonUtil.obj2Json(lstCompany));
		model.addAttribute("entity", entity);
		model.addAttribute("companyId", Long.valueOf(companyIdStr));
		return "bs/companyFollowDetail";
	}
	
	@ModelAttribute("preload")
	public BsCompanyFollow getEntity(@RequestParam(value = "id", required = false) Long id) {
		if (id != null) {
			if (id > 0)
				return getService().getEntity(id);
			else {
				BsCompanyFollow entity = new BsCompanyFollow();
				entity.setId(0l);
				return entity;
			}
		}
		return null;
	}
	
	@RequestMapping(value = "save", method = RequestMethod.POST)
	public void save(@Valid @ModelAttribute("preload") BsCompanyFollow companyFollow,HttpServletRequest request, HttpServletResponse response) {
		try{
			BsCompanyFollow old = companyFollowClient.getEntity(companyFollow.getId());
			LogUtil.saveOrUpdate(request, old, companyFollow, companyFollow.getId());//记录日志
			companyFollow.setCreateUserId(ShiroUtil.getCurrentUserId());
			companyFollow.setCreateUserName(ShiroUtil.getCurrentUserName());
			companyFollow.setEnterpriseId(ShiroUtil.getEnterpriseId());
			companyFollowClient.save(companyFollow);
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
		PageDown<BsCompanyFollow> page=companyFollowClient.findPage(queryVo);
		BsCompanyFollow e = sumPage(request, response);
		Map<String, Object> footer = null;
		if (e != null) {
			footer = entity2Footer(e);
		}
		
		JsonEasyUI.renderJson(response, page,footer);
	}
	
	@RequestMapping("/sav1")
	public void sav1(@RequestParam("param") String param,HttpServletResponse response){
		try {
			Long userId=ShiroUtil.getCurrentUserId();
			String userName=ShiroUtil.getCurrentUserName(); 
			
			List<BsCompanyFollow> list=JSON.parseArray(param, BsCompanyFollow.class);
			for(BsCompanyFollow entity:list){
				if(entity.getCompanyId()==null||entity.getCompanyId()==0l){
					RenderUtil.renderFailure("fail", response);
					return;
				}
				entity.setCreateUserId(userId);
				entity.setEnterpriseId(ShiroUtil.getEnterpriseId());
				entity.setCreateUserName(userName);
				companyFollowClient.save(entity);
			}
			RenderUtil.renderSuccess("success", response);
		} catch (Exception e) {
			e.printStackTrace();
			RenderUtil.renderFailure("fail", response);
			
		}
	}
	@RequestMapping(value = "saveFollow", method = RequestMethod.POST)
	public void saveFollow(BsCompanyFollow companyFollow,HttpServletRequest request, HttpServletResponse response) {
		try{
			BsCompanyFollow old = companyFollowClient.getEntity(companyFollow.getId());
			LogUtil.saveOrUpdate(request, old, companyFollow, companyFollow.getId());//记录日志
			companyFollow.setCreateUserId(ShiroUtil.getCurrentUserId());
			companyFollow.setCreateUserName(ShiroUtil.getCurrentUserName());
			companyFollow.setEnterpriseId(ShiroUtil.getEnterpriseId());
			companyFollowClient.save(companyFollow);
			RenderUtil.renderText("success", response);
		}catch(Exception e){
			e.printStackTrace();
			RenderUtil.renderText("fail", response);
		}

	}

	@RequestMapping(value = "findBsCompanyFollowById/{id}", method = RequestMethod.GET)
	public void findBsCompanyFollowById(@PathVariable("id") Long id,HttpServletResponse response,HttpServletRequest request){
		BsCompanyFollow entity = companyFollowClient.getEntity(id);
		RenderUtil.renderJson(entity,response);
	}

}
