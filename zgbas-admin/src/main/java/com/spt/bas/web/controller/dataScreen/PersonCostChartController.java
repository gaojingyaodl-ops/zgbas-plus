package com.spt.bas.web.controller.dataScreen;

import com.spt.bas.client.cache.BsDictUtil;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.BsDictData;
import com.spt.bas.report.client.entity.RptPersonCostChart;
import com.spt.bas.report.client.remote.IRptPersonCostChartClient;
import com.spt.bas.report.client.vo.RptPersonCostChartSearchVo;
import com.spt.tools.core.json.JsonUtil;
import com.spt.tools.web.util.RenderUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;


@Controller
@RequestMapping(value = "/person/cost/chart")
public class PersonCostChartController {
	@Value("${basData.md5.secret.key}")
	private String basDataMd5SecretKey;
	@Value("${basData.server.url}")
	private String basDataLoginUrl;
	@Autowired
	private IRptPersonCostChartClient personCostChartClient;

	@RequestMapping(value = "content",method = RequestMethod.GET)
	public String content(Long id,Model model) {
		DateTimeFormatter pattern = DateTimeFormatter.ofPattern("yyyy-MM");
		LocalDate now = LocalDate.now();
		now = now.minusMonths(1);
		model.addAttribute("nowTargetMonth", now.format(pattern));
		return "dataScreen/personCostChart";
	}

	/**
	 * 获取人员成本图表数据
	 */
	@PostMapping("/getPersonCostChartData")
	public void getPersonCostChartData(RptPersonCostChartSearchVo searchVo, HttpServletResponse response) {
		List<BsDictData> dictDataList = BsDictUtil.getListByCategory(BasConstants.ZG_ENTERPRISE_ID, BasConstants.DICT_PERSON_COST_CHART_BRANCH_CD);
		searchVo.setPersonCostChartBranceCdList(dictDataList);
		List<RptPersonCostChart> personCostChartList = personCostChartClient.personCostChartDataList(searchVo);
		RenderUtil.renderJson(JsonUtil.obj2Json(personCostChartList), response);
	}



}
