package com.spt.bas.web.controller.apply;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.beust.jcommander.internal.Maps;
import com.spt.bas.client.entity.SealUsageOphis;
import com.spt.bas.client.remote.ISealUsageOphisClient;
import com.spt.bas.web.shiro.ShiroUtil;
import com.spt.tools.core.bean.PageSearchVo;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.data.vo.BaseVo;
import com.spt.tools.web.controller.SingleCrudControll;
import com.spt.tools.web.util.JsonEasyUI;

/**
 * 印章使用加盖操作记录
 */
@Controller
@RequestMapping(value = "/apply/sealUsageOphis")
public class ApplySealUsageOphisController extends SingleCrudControll<SealUsageOphis, BaseVo> {
	@Autowired
	private ISealUsageOphisClient sealUsageOphisClient;
	
	@Override
	public BaseClient<SealUsageOphis> getService() {
		return sealUsageOphisClient;
	}
	
	@RequestMapping(value = "")
	public String index(@RequestParam(value="id") Long id,Model model,HttpServletRequest request) {
		model.addAttribute("sealUsageId", id);
		model.addAttribute("enterpriseId",ShiroUtil.getEnterpriseId());
		return "seal/seal_usage_ophis";		
	}
	
	@RequestMapping(value = "findUsageOphisPage")
	public void findUsageOphisPage(PageSearchVo searchVo, HttpServletRequest request, HttpServletResponse response) {
		initSearch(searchVo, request);
		Page<SealUsageOphis> page = sealUsageOphisClient.findPage(searchVo);
		JsonEasyUI.renderJson(response, page);
	}
	
	@Override
	public Map<String, Object> getDefaultFilter() {
		Map<String, Object> map = Maps.newHashMap();
		map.put("EQL_enterpriseId", ShiroUtil.getEnterpriseId());
		return map;
	}
	
	@Override
	protected void preInsert(SealUsageOphis e) {
		e.setEnterpriseId(ShiroUtil.getEnterpriseId());
	}
	
}
