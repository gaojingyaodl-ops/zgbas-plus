package com.spt.bas.web.controller.report;

import com.spt.auth.sdk.cache.DictUtil;
import com.spt.auth.sdk.entity.SysDeptSdk;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.CtrContract;
import com.spt.bas.client.remote.ICtrContractClient;
import com.spt.bas.report.client.entity.RptCtrDailyStockReport;
import com.spt.bas.report.client.remote.IRptCtrDailySalesReportClient;
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
 * 采购合同库存日明细表
 */
@Controller
@RequestMapping(value = "/rpt/ctrDailyStock")
public class RptDailyStockController extends PageController<CtrContract, BaseVo>{
	
	@Autowired
	private ICtrContractClient ctrContractClient;
	@Resource
	private WebParamUtils webParamUtils;
	@Autowired
	private IRptCtrDailySalesReportClient ctrDailySallesReportClient;
	@Override
	public BaseClient<CtrContract> getService() {
		return ctrContractClient;
	}
	
	@RequestMapping(value = "")
	public String detail(Model model){
		model.addAttribute("contractAttrJson",
				JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_CONTRACTATTR)));
		List<SysDeptSdk> deptList = webParamUtils.getDeptAll();
		EasyTreeNode nodes = EasyTreeUtil2.getDeptTree(deptList, true);
		model.addAttribute("matchUserNameTree", JsonUtil.obj2Json(nodes.getChildren()));
		model.addAttribute("deptJson", JsonUtil.obj2Json(deptList));
		return "report/dailyStock";
	}
	
	/**
	 * 采购合同库存日明细表
	 * @param searchVo
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "findDailyStock")
	public void findDailyStock(RptCtrDailyStockReport searchVo, HttpServletRequest request, HttpServletResponse response){
		searchVo.setEnterpriseId(ShiroUtil.getEnterpriseId());
		PageDown<RptCtrDailyStockReport> page = ctrDailySallesReportClient.findDailyStock(searchVo);
		RptCtrDailyStockReport total = ctrDailySallesReportClient.findDailyStockTotal(searchVo);
		Map<String, Object> footer = new HashMap<>();
		footer.put("productName", "合计");
		footer.put("buyNumber", total.getBuyNumber());
		footer.put("dealNumber", total.getDealNumber());
		footer.put("remainNumber", total.getRemainNumber());
		JsonEasyUI.renderJson(response, page,footer);
	}
}
