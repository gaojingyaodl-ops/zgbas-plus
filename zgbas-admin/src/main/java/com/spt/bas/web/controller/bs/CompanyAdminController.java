package com.spt.bas.web.controller.bs;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.google.common.collect.Maps;
import com.spt.auth.sdk.cache.DictUtil;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.BsCompany;
import com.spt.bas.client.remote.IBsCompanyClient;
import com.spt.bas.web.shiro.ShiroUtil;
import com.spt.tools.core.json.JsonUtil;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.data.vo.BaseVo;
import com.spt.tools.web.controller.SingleCrudControll;

@Controller
@RequestMapping("/bs/companyAdmin")
public class CompanyAdminController extends SingleCrudControll<BsCompany, BaseVo> {
	@Autowired
	private IBsCompanyClient companyClient;

	@Override
	public BaseClient<BsCompany> getService() {
		// TODO Auto-generated method stub
		return companyClient;
	}

	
	@RequestMapping("/index")
	public String index(Model model,@RequestParam("id") Long id){
		model.addAttribute("id", id);
		return "bs/company_admin";
	}
	
	@RequestMapping("/detail/{id}")
	public String detail(@PathVariable("id") Long id,Model model){
		model.addAttribute("enableFlgs",
				JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DictType.COMM_ENABLE_BOOLEAN)));
		BsCompany entity;
		if(id!=null&&id!=0){
			entity = companyClient.getEntity(id);
			model.addAttribute("entity", entity);
		}else{
			entity = new BsCompany();
			entity.setId(0l);
		}
		model.addAttribute("entity", entity);
		return "bs/company_admin_detail";
	}

	@Override
	public Map<String, Object> getDefaultFilter() {
		Map<String, Object> map = Maps.newHashMap();
		map.put("EQL_enterpriseId", ShiroUtil.getEnterpriseId());
		return map;
	}
	@Override
	protected void preInsert(BsCompany e) {
		e.setEnterpriseId(ShiroUtil.getEnterpriseId());
	}
}
