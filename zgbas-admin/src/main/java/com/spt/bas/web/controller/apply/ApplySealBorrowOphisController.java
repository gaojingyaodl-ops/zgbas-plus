package com.spt.bas.web.controller.apply;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.beust.jcommander.internal.Maps;
import com.spt.auth.sdk.cache.DictUtil;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.SealBorrowOphis;
import com.spt.bas.client.remote.ISealBorrowOphisClient;
import com.spt.bas.web.shiro.ShiroUtil;
import com.spt.tools.core.bean.PageSearchVo;
import com.spt.tools.core.json.JsonUtil;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.data.vo.BaseVo;
import com.spt.tools.web.controller.PageController;
import com.spt.tools.web.util.JsonEasyUI;

/**
 * 印章外借申请单
 */
@Controller
@RequestMapping(value = "/apply/sealBorrowOphis")
public class ApplySealBorrowOphisController extends PageController<SealBorrowOphis, BaseVo> {
	@Autowired
	private ISealBorrowOphisClient sealBorrowOphisClient;
	
	@Override
	public BaseClient<SealBorrowOphis> getService() {
		return sealBorrowOphisClient;
	}
	
	@RequestMapping(value = "")
	public String index(@RequestParam(value="id") Long id,Model model,HttpServletRequest request) {
		// 印章外借-物品类型
		model.addAttribute("itemTypeJson",
				JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_ITEM_TYPE)));
		// 印章外借-印章状态
		model.addAttribute("sealStatusJson",
				JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_SEAL_STATUS)));
		model.addAttribute("sealBorrowId", id);
		return "seal/seal_borrow_ophis";		
	}
	
	@RequestMapping(value = "findBorrowOphisPage")
	public void findBorrowPage(PageSearchVo searchVo, HttpServletRequest request, HttpServletResponse response) {
		initSearch(searchVo, request);
		Page<SealBorrowOphis> page = sealBorrowOphisClient.findPage(searchVo);
		JsonEasyUI.renderJson(response, page);
	}
	
	
	@Override
	public Map<String, Object> getDefaultFilter() {
		Map<String, Object> map = Maps.newHashMap();
		map.put("EQL_enterpriseId", ShiroUtil.getEnterpriseId());
		return map;
	}
	
	
}
