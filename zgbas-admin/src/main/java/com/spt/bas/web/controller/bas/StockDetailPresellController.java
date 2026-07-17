package com.spt.bas.web.controller.bas;

import com.google.common.collect.Maps;
import com.spt.auth.sdk.cache.DictUtil;
import com.spt.auth.sdk.entity.SysDeptSdk;
import com.spt.auth.sdk.open.IAuthOpenFacade;
import com.spt.auth.sdk.vo.DeptSearchVo;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.StockDetailPresell;
import com.spt.bas.client.remote.IBsProductTypeClient;
import com.spt.bas.client.remote.IStockDetailPresellClient;
import com.spt.bas.client.vo.StockDetailPresellVo;
import com.spt.bas.report.client.entity.RptStockDetailPresellReport;
import com.spt.bas.report.client.remote.IRptReportStockDetailPresellClient;
import com.spt.bas.report.client.vo.RptStockDetailPresellSearchVo;
import com.spt.bas.web.shiro.ShiroUtil;
import com.spt.bas.web.util.EasyTreeUtil2;
import com.spt.tools.core.json.JsonUtil;
import com.spt.tools.data.easyui.EasyTreeNode;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.data.vo.BaseVo;
import com.spt.tools.data.vo.PageDown;
import com.spt.tools.web.controller.SingleCrudControll;
import com.spt.tools.web.util.JsonEasyUI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * 库存盘点
 *
 */
@Controller
@RequestMapping("/stock/detailPresell")
public class StockDetailPresellController extends SingleCrudControll<StockDetailPresell, BaseVo>{

	@Autowired
	private IStockDetailPresellClient stockDetailPresellClient;
	@Autowired
	private IRptReportStockDetailPresellClient reportStockDetailPresellClient;
	@Autowired
	private IBsProductTypeClient bsProductTypeClient;
	@Autowired
	private IAuthOpenFacade authOpenFacade;

	@Override
	public BaseClient<StockDetailPresell> getService() {
		return stockDetailPresellClient;
	}

	@Override
	public Map<String, Object> getDefaultFilter() {
		Map<String, Object> map = Maps.newHashMap();
		map.put("EQL_enterpriseId", ShiroUtil.getEnterpriseId());
		return map;
	}
	@Override
	protected void preInsert(StockDetailPresell e) {
		e.setEnterpriseId(ShiroUtil.getEnterpriseId());
	}
	//跳转库存明细选择页面
	@RequestMapping(value = "choose")
	public String choose(Model model) {
		List<EasyTreeNode> productTree = bsProductTypeClient.findAllProductTree(ShiroUtil.getEnterpriseId());
		model.addAttribute("productTypeJson", JsonUtil.obj2Json(productTree));
		model.addAttribute("stockStatusJson", JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_STOCKSTATUS)));
		model.addAttribute("productType",
				JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_BUYPRODUCT)));
		//获取业务员树
		DeptSearchVo deptSearchVo = new DeptSearchVo( ShiroUtil.getEnterpriseId());
		List<SysDeptSdk> deptList = authOpenFacade.findDeptAll(deptSearchVo);
		EasyTreeNode nodes = EasyTreeUtil2.getDeptTree(deptList, true);
		model.addAttribute("matchUserNameTree",JsonUtil.obj2Json(nodes.getChildren()));
		return "bas/stockDetailPresell-choose";
	}

	@RequestMapping(value = "queryApplyPage")
	public void queryApplyPage(RptStockDetailPresellSearchVo searchVo, HttpServletRequest request, HttpServletResponse response) {
		initSearch(searchVo, request);
		searchVo.setEnterpriseId(ShiroUtil.getEnterpriseId());
		PageDown<RptStockDetailPresellReport> page = reportStockDetailPresellClient.findApplyPage(searchVo);
		JsonEasyUI.renderJson(response, page);
	}

	@RequestMapping(value = "findList")
	public void findList(@RequestParam("contractId") Long contractId,HttpServletRequest request, HttpServletResponse response) {
		if (contractId != null) {
			List<StockDetailPresellVo> findList = stockDetailPresellClient.findList(contractId);
			JsonEasyUI.renderListJson(response, findList);
		}
	}

	@RequestMapping(value = "findById", method = RequestMethod.POST)
	@ResponseBody
	public StockDetailPresell findById(@RequestParam("id") Long id){
		StockDetailPresell entity = stockDetailPresellClient.getEntity(id);
		return entity;
	}
}
