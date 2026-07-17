package com.spt.bas.web.controller.bs;

import com.spt.bas.client.cache.BsDictUtil;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.BsCompanyCreditFlow;
import com.spt.bas.client.remote.IBsCompanyCreditFlowClient;
import com.spt.bas.web.shiro.ShiroUtil;
import com.spt.tools.core.bean.PageSearchVo;
import com.spt.tools.core.json.JsonUtil;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.data.vo.BaseVo;
import com.spt.tools.data.vo.PageDown;
import com.spt.tools.web.controller.PageController;
import com.spt.tools.web.util.JsonEasyUI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * 客户授信额度变更流水记录
 */
@Controller
@RequestMapping(value = "/bs/creditFlow")
public class BsCompanyCreditFlowController extends PageController<BsCompanyCreditFlow, BaseVo>  {
	@Autowired
	private IBsCompanyCreditFlowClient bsCompanyCreditFlowClient;
	
	@Override
	public BaseClient<BsCompanyCreditFlow> getService() {
		return bsCompanyCreditFlowClient;
	}

	@RequestMapping(value = "")
	public String index(Model model) {
		//授信类别
		model.addAttribute("creditTypeJson",
				JsonUtil.obj2Json(BsDictUtil.getListByCategory(ShiroUtil.getEnterpriseId(), BasConstants.DICT_CREDIT_TYPE)));
		return "bs/companyCreditFlow";
	}

	@RequestMapping("/findCreditFlow")
	public void findListByCompanyId(@RequestParam("id") Long id, PageSearchVo queryVo, HttpServletResponse response, HttpServletRequest request) {
		Map<String, Object> map = new HashMap<>();
		map.put("EQL_companyId", id);
		initSearch2(queryVo, request, map);
		PageDown<BsCompanyCreditFlow> page = bsCompanyCreditFlowClient.findPage(queryVo);

		JsonEasyUI.renderJson(response, page);
	}

}
