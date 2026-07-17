package com.spt.bas.web.controller.report;

import com.spt.auth.sdk.cache.DictUtil;
import com.spt.auth.sdk.entity.SysDeptSdk;
import com.spt.bas.client.cache.BsCompanyOurUtil;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.CtrContract;
import com.spt.bas.client.remote.ICtrContractClient;
import com.spt.bas.report.client.entity.RptCtrContractReceiveDetailReport;
import com.spt.bas.report.client.remote.IRptCtrContractReceiveDetailClient;
import com.spt.bas.report.client.vo.RptReceiveDetailSearchVo;
import com.spt.bas.web.shiro.ShiroUtil;
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

/***
 * 收款明细
 * 
 *
 */

@Controller
@RequestMapping(value = "/rpt/receiveMoney")
public class RptReceiveMoneyDetailController extends PageController<CtrContract, BaseVo>{
	
	@Autowired
	private ICtrContractClient ctrContractClient;
	@Resource
	private WebParamUtils webParamUtils;
	
	
	@Autowired
	private IRptCtrContractReceiveDetailClient ctrContractReceiveDetailClient;

	@Override
	public BaseClient<CtrContract> getService() {
		
		return ctrContractClient;
	}
	
	@RequestMapping(value = "")
	public String index(Model model) {
		//获取业务员树
		EasyTreeNode nodes = webParamUtils.getDeptEasyTreeNode(true);
		model.addAttribute("matchUserNameTree",JsonUtil.obj2Json(nodes.getChildren()));
		model.addAttribute("approveStatusJson",
				JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_APPROVESTATUS))); //审批状态
		model.addAttribute("applyTypeJson",
				JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_APPLYTYPE))); //业务类型
		model.addAttribute("contractStatusJson",
				JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_CONTRACTSTATUS))); //合同状态
		model.addAttribute("ourCompanyJson", JsonUtil.obj2Json(
				BsCompanyOurUtil.getCompanyOurToBsDictDataList())); //我方企业名
		
		return "report/receiveMoneyDetail";
	}
	
	@RequestMapping(value = "findReceiveDetail")
	public void findReceiveDetail(RptReceiveDetailSearchVo searchVo, HttpServletRequest request, HttpServletResponse response) {
		searchVo.setEnterpriseId(ShiroUtil.getEnterpriseId());
		PageDown<RptCtrContractReceiveDetailReport> page = ctrContractReceiveDetailClient.findPageReceiveDetail(searchVo);
		RptCtrContractReceiveDetailReport total = ctrContractReceiveDetailClient.findPageReceiveDetailSum(searchVo);
		List<SysDeptSdk> deptList = webParamUtils.getDeptAll();
		if (page != null && page.getContent().size() > 0) {
			for (RptCtrContractReceiveDetailReport reportVo : page.getContent()) {
				for (SysDeptSdk sysDept : deptList) {
					if(reportVo.getDeptId().equals(sysDept.getDeptId())){
						reportVo.setDeptName(sysDept.getDeptName());
					}
				}
			}
		}
		
		Map<String, Object> footer = new HashMap<>();
		footer.put("companyName", "合计");
		footer.put("totalAmount", total.getSumTotalAmount());
		footer.put("billedAmount", total.getSumBilledAmount());
		footer.put("overdueAmount", total.getSumOverdueAmount());
		footer.put("receiveAmount", total.getSumReceiveAmount());
		
		JsonEasyUI.renderJson(response, page, null, footer);
	}
	
}
