package com.spt.bas.web.controller.pm;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.google.common.collect.Maps;
import com.spt.auth.sdk.cache.DictUtil;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.ApproveDeal;
import com.spt.bas.client.remote.IApproveDealClient;
import com.spt.bas.client.vo.ApproveDealQueryVo;
import com.spt.bas.client.vo.ApproveDealSerachVo;
import com.spt.bas.web.shiro.ShiroUtil;
import com.spt.tools.core.json.JsonUtil;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.data.vo.BaseVo;
import com.spt.tools.web.controller.PageController;
import com.spt.tools.web.util.JsonEasyUI;

/**
 * 待办事项
 * 
 * @author wlddh
 *
 */
@Controller
@RequestMapping("/approve/deal")
public class ApproveDealController extends PageController<ApproveDeal, BaseVo> {

	@Autowired
	private IApproveDealClient approveDealClient;

	@Override
	public BaseClient<ApproveDeal> getService() {
		return approveDealClient;
	}

	@RequestMapping("")
	public String index(Model model) {
		model.addAttribute("dealTypeJson",
				JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_APPLYTYPE)));

		return "pm/approveDeal";
	}

	@RequestMapping("approveDealList")
	public void approveDealList(ApproveDealSerachVo queryVo, HttpServletResponse response, HttpServletRequest request) {
		initSearch(queryVo, request);
		Long userId = ShiroUtil.getCurrentUserId();
		queryVo.setUserId(userId);
		Page<ApproveDealQueryVo> page = approveDealClient.findPageVo(queryVo);
		JsonEasyUI.renderJson(response, page);
	}
	
	@Override
	public Map<String, Object> getDefaultFilter() {
		Map<String, Object> map = Maps.newHashMap();
		map.put("EQL_enterpriseId", ShiroUtil.getEnterpriseId());
		return map;
	}

}
