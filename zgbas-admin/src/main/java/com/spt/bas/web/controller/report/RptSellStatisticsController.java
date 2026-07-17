package com.spt.bas.web.controller.report;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.spt.bas.client.remote.IBsProductTypeClient;
import com.spt.bas.report.client.entity.RptCtrContractStatistics;
import com.spt.bas.report.client.remote.IRptCtrContractStatisticsClient;
import com.spt.bas.report.client.vo.RptStatisticsVo;
import com.spt.bas.web.shiro.ShiroUtil;
import com.spt.tools.core.json.JsonUtil;
import com.spt.tools.data.easyui.EasyTreeNode;
import com.spt.tools.data.vo.PageDown;
import com.spt.tools.web.util.JsonEasyUI;

/**
 * 销售统计
 * @author zhouzihang
 */
@Controller
@RequestMapping(value = "/rpt/sellstatistics")
public class RptSellStatisticsController {
	@Autowired
	private IRptCtrContractStatisticsClient ctrContractStatisticsClient;
	@Autowired
	private IBsProductTypeClient bsProductTypeClient;

	// 销售统计页
	@RequestMapping(value = "")
	public String index(Model model) {
		// 货品树
		List<EasyTreeNode> productTree = bsProductTypeClient.findAllProductTree(ShiroUtil.getEnterpriseId());
		model.addAttribute("productTypeJson", JsonUtil.obj2Json(productTree));
		model.addAttribute("contractType", "S");
		return "report/buyOrSellStatistics";
	}

	// 展示销售统计
	@RequestMapping(value = "showStatistics")
	public void showStatistics(RptStatisticsVo vo, HttpServletRequest request, HttpServletResponse response) {
		vo.setEnterpriseId(ShiroUtil.getEnterpriseId());
		PageDown<RptCtrContractStatistics> page = ctrContractStatisticsClient.showStatistics(vo);
		JsonEasyUI.renderJson(response, page);
	}
}
