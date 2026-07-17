package com.spt.bas.web.controller.report;

import com.spt.auth.sdk.cache.DictUtil;
import com.spt.auth.sdk.entity.SysDeptSdk;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.CtrContract;
import com.spt.bas.client.remote.ICtrContractClient;
import com.spt.bas.report.client.entity.RptBusinessAccountReport;
import com.spt.bas.report.client.remote.IRptBusinessAccountClient;
import com.spt.bas.report.client.vo.RptBusinessSearchVo;
import com.spt.bas.web.shiro.ShiroUtil;
import com.spt.bas.web.util.EasyTreeUtil2;
import com.spt.bas.web.util.WebParamUtils;
import com.spt.tools.core.json.JsonUtil;
import com.spt.tools.data.easyui.EasyTreeNode;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.data.vo.BaseVo;
import com.spt.tools.data.vo.PageDown;
import com.spt.tools.web.controller.PageController;
import com.spt.tools.web.util.JsonEasyUI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 代采代销明细
 */
@Controller
@RequestMapping(value = "/rpt/business")
public class RptBusinessController extends PageController<CtrContract, BaseVo> {
	@Autowired
	private ICtrContractClient ctrContractClient;
	@Autowired
	private IRptBusinessAccountClient businessAccountClient;
	@Resource
	private WebParamUtils webParamUtils;
	@Override
	public BaseClient<CtrContract> getService() {
		return ctrContractClient;
	}
	
	//代理
	@RequestMapping(value = "typeDL")
	public String typeDL(Model model) {
		model = initData(model);
		model.addAttribute("searchType", "DL");
		return "report/business";
	}
	
	//自营
	@RequestMapping(value = "typeZY")
	public String typeZY(Model model) {
		model = initData(model);
		model.addAttribute("searchType", "ZY");
		return "report/business";
	}
	
	//赊销
	@RequestMapping(value = "typeSX")
	public String typeSX(Model model) {
		model = initData(model);
		model.addAttribute("searchType", "SX");
		return "report/business";
	}
	
	//质押
	@RequestMapping(value = "typeSY")
	public String typeSY(Model model) {
		model = initData(model);
		model.addAttribute("searchType", "SY");
		return "report/business";
	}
	
	public Model initData(Model model) {
		//业务大类
		model.addAttribute("businessJson", JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_BUSINESS)));
		//业务小类
		model.addAttribute("businessTypeJson", JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_BUSINESSTYPE)));
		// 获取业务员树
		List<SysDeptSdk> deptList = webParamUtils.getDeptAll();
		EasyTreeNode nodes = EasyTreeUtil2.getDeptTree(deptList, true, true);
		model.addAttribute("deptJson", JsonUtil.obj2Json(deptList));
		model.addAttribute("matchUserNameTree", JsonUtil.obj2Json(nodes.getChildren()));
		return model;
	}

	@RequestMapping(value = "findBusiness")
	public void findBusiness(RptBusinessSearchVo searchVo, HttpServletRequest request, HttpServletResponse response) {
		searchVo.setEnterpriseId(ShiroUtil.getEnterpriseId());
		PageDown<RptBusinessAccountReport> page = businessAccountClient.findPage(searchVo);
		RptBusinessAccountReport total = businessAccountClient.findPageSum(searchVo);
		Map<String, Object> footer = new HashMap<>();
		footer.put("sellContractNo", "合计");
		footer.put("sellAmount", total.getSellAmount());
		footer.put("profit", total.getProfit());
		footer.put("vatAmount", total.getVatAmount());
		footer.put("extraAmount", total.getExtraAmount());
		footer.put("printAmount", total.getPrintAmount());
		footer.put("taxAmount", total.getTaxAmount());
		footer.put("costAmount", total.getCostAmount());
		footer.put("buyOtherWarehouseAmount", total.getBuyOtherWarehouseAmount());
		footer.put("sellOtherWarehouseAmount", total.getSellOtherWarehouseAmount());
		footer.put("buyAmount", total.getBuyAmount());
		footer.put("margin", total.getMargin());
		footer.put("dealedAmount", total.getDealedAmount());
		footer.put("notReceive", total.getNotReceive());
		footer.put("grossMargin", total.getGrossMargin());
		footer.put("realMargin", total.getRealMargin());
		JsonEasyUI.renderJson(response, page, null, footer);
	}
}
