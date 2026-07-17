package com.spt.bas.web.controller.bs;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.google.common.collect.Maps;
import com.spt.bas.client.entity.BsCompanyOphis;
import com.spt.bas.client.remote.IBsCompanyOphisClient;
import com.spt.bas.web.shiro.ShiroUtil;
import com.spt.tools.core.bean.PageSearchVo;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.data.vo.BaseVo;
import com.spt.tools.data.vo.PageDown;
import com.spt.tools.web.controller.PageController;
import com.spt.tools.web.util.JsonEasyUI;

@Controller
@RequestMapping("/bs/companyOphis")
public class BsCompanyOphisController extends PageController<BsCompanyOphis, BaseVo>{
	@Autowired
	private IBsCompanyOphisClient companyOphisClient;
	@Override
	public BaseClient<BsCompanyOphis> getService() {
		return null;
	}
	
	@Override
	public Map<String, Object> getDefaultFilter() {
		Map<String, Object> map = Maps.newHashMap();
		map.put("EQL_enterpriseId", ShiroUtil.getEnterpriseId());
		return map;
	}
	
	/**
	 * 操作历史list
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "opreateHisList", method = RequestMethod.POST)
	public void opreateHisList(@RequestParam("companyId") Long companyId,PageSearchVo SearchVo, HttpServletResponse response) {
		Map<String,Object> searchParams= new HashMap<String,Object>();
		searchParams.put("EQL_companyId", companyId);
		SearchVo.setSearchParams(searchParams);
		PageDown<BsCompanyOphis> page = companyOphisClient.findPage(SearchVo);
		JsonEasyUI.renderJson(response, page);
	}
	


}
