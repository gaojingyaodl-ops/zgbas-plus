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
import com.spt.bas.client.entity.BsCompany;
import com.spt.bas.client.entity.BsCompanyContacts;
import com.spt.bas.client.remote.IBsCompanyClient;
import com.spt.bas.client.remote.IBsCompanyContactsClient;
import com.spt.bas.web.shiro.ShiroUtil;
import com.spt.tools.core.bean.PageSearchVo;
import com.spt.tools.core.json.JsonUtil;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.data.vo.BaseVo;
import com.spt.tools.data.vo.PageDown;
import com.spt.tools.web.controller.SingleCrudControll;
import com.spt.tools.web.util.JsonEasyUI;
import com.spt.tools.web.util.RenderUtil;

/**
 * 企业联系人管理
 * @author wanjie
 *
 */
@Controller
@RequestMapping(value = "/bs/companyContacts")
public class BsCompanyContactsController extends SingleCrudControll<BsCompanyContacts, BaseVo>{

	@Autowired
	private IBsCompanyContactsClient bsCompanyContactsClient;
	@Autowired
	private IBsCompanyClient bsCompanyClient;
	@Autowired
	private IBsCompanyClient companyClient;
	
	@Override
	public BaseClient<BsCompanyContacts> getService() {
		return bsCompanyContactsClient;
	}
	@Override
	protected void preInsert(BsCompanyContacts e) {
		e.setEnterpriseId(ShiroUtil.getEnterpriseId());
	}
	/**
	 * 查询所有
	 */
	@RequestMapping(value="", method = RequestMethod.GET)
	public String index(Long id,Model model){
		List<BsCompany> lstCompany = companyClient.findAll();
		model.addAttribute("companyJson", JsonUtil.obj2Json(lstCompany));
		model.addAttribute("id", id);
		return "bs/companyContacts";
	}
	
	/**
	 * 添加页面加载
	 */
	@RequestMapping(value = "detail/{id}", method = RequestMethod.GET)
	public String detail(@PathVariable("id") Long id, Model model) {
		BsCompanyContacts entity;
		if(id!=null && id!=0){
			entity=getService().getEntity(id);
		}else{
			entity=new BsCompanyContacts();
			entity.setId((long) 0);
		}
		model.addAttribute("entity", entity);
		List<BsCompany> bsCompany = bsCompanyClient.findAll();
	    model.addAttribute("bsCompany", JsonUtil.obj2Json(bsCompany));
		return "bs/companyContacts_detail";
	}	
	
	/**
	 * 删除
	 * @param id
	 * @param response
	 */
	@RequestMapping(value="delete/{id}",method = RequestMethod.GET)
	public void delete(@PathVariable("id") Long id,HttpServletResponse response){
		try {
			getService().delete(id);
		} catch (Exception e) {
			logger.info(e.getMessage(), e);
			RenderUtil.renderFailure("failure", response);	
		}	
		RenderUtil.renderSuccess("success", response);
		
	}
	
	
	@ModelAttribute("preload")
	public BsCompanyContacts getEntity(@RequestParam(value = "id", required = false) Long id) {
		if (id != null) {
			if (id > 0)
				return getService().getEntity(id);
			else {
				BsCompanyContacts entity = new BsCompanyContacts();
				entity.setId(0l);
				return entity;
			}
		}
		return null;
	}
	
	
	/**
	 * 保存
	 */
	@RequestMapping(value = "save", method = RequestMethod.POST)
	public void save(@Valid @ModelAttribute("preload") BsCompanyContacts companyContacts,HttpServletRequest request, HttpServletResponse response) {
		try{
			bsCompanyContactsClient.save(companyContacts);
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
		PageDown<BsCompanyContacts> page=bsCompanyContactsClient.findPage(queryVo);
		BsCompanyContacts e = sumPage(request, response);
		Map<String, Object> footer = null;
		if (e != null) {
			footer = entity2Footer(e);
		}
		
		JsonEasyUI.renderJson(response, page,footer);
	}
	
	@RequestMapping("/sav")
	public void saveCCompanyContact(@RequestParam("param") String param,HttpServletResponse response){
		try {
			List<BsCompanyContacts> list=JSON.parseArray(param, BsCompanyContacts.class);
			for(BsCompanyContacts entity:list){
				entity.setEnterpriseId(ShiroUtil.getEnterpriseId());
				bsCompanyContactsClient.save(entity);
			}
			RenderUtil.renderSuccess("success", response);
		} catch (Exception e) {
			e.printStackTrace();
			RenderUtil.renderFailure("fail", response);
		}
	}

}
