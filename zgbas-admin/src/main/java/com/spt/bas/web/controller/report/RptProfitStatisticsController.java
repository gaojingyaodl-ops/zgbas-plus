package com.spt.bas.web.controller.report;

import com.spt.bas.client.cache.BsDictUtil;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.BsDictData;
import com.spt.bas.report.client.entity.RptProfitStatistics;
import com.spt.bas.report.client.remote.IRptProfitStatisticsClient;
import com.spt.bas.report.client.vo.RptProfitStatisticsSearchVo;
import com.spt.tools.web.util.JsonEasyUI;
import com.spt.tools.web.util.RenderUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 利润表统计
 */
@Slf4j
@Controller
@RequestMapping(value = "/rpt/profitStatistics")
public class RptProfitStatisticsController {
    
    @Autowired
    private IRptProfitStatisticsClient profitStatisticsClient;

    @RequestMapping(value = "content")
    public String content(Model model, HttpServletRequest request) {
        DateTimeFormatter pattern = DateTimeFormatter.ofPattern("yyyy-MM");
        LocalDate now = LocalDate.now();
        model.addAttribute("nowTargetMonth", now.format(pattern));
        return "report/profitStatistics";
    }
    
    /**
     * 获取利润表数据
     * @param searchVo
     * @return
     */
    @RequestMapping("getRptProfitStatistics")
    public void getRptProfitStatistics(RptProfitStatisticsSearchVo searchVo,HttpServletRequest request, HttpServletResponse response) {
        handelSearchPrams(searchVo,"C");
        RptProfitStatistics rptProfitStatistics = profitStatisticsClient.getRptProfitStatistics(searchVo);
        RenderUtil.renderJson(rptProfitStatistics, response);

    }

    /**
     * 根据销售付款日期获取利润表数据
     * @param searchVo
     * @return
     */
    @RequestMapping("getRptProfitStatisticsByRealPayFullTime")
    public void getRptProfitStatisticsByRealPayFullTime(RptProfitStatisticsSearchVo searchVo,HttpServletRequest request, HttpServletResponse response) {
        handelSearchPrams(searchVo,"P");
        RptProfitStatistics rptProfitStatistics = profitStatisticsClient.getRptProfitStatistics(searchVo);
        RenderUtil.renderJson(rptProfitStatistics, response);

    }

    /**
     * 处理查询参数
     * @param searchVo
     */
    public void handelSearchPrams(RptProfitStatisticsSearchVo searchVo, String type){
        List<Long> deptIdList = new ArrayList<>();
        List<BsDictData> dictDataList = BsDictUtil.getListByCategory(BasConstants.ZG_ENTERPRISE_ID, BasConstants.BRANCH_CD);
        if(CollectionUtils.isNotEmpty(dictDataList)) {

            for (BsDictData bsDictData : dictDataList) {
                String remark = bsDictData.getRemark();
                if(StringUtils.isNotBlank(remark)) {
                    String[] split = remark.split(",");
                    for (String s : split) {
                        if (NumberUtils.isCreatable(s)) {
                            deptIdList.add(Long.valueOf(s));
                        }
                    }
                }

            }
        }
        searchVo.setDeptIdList(deptIdList);
        searchVo.setType(type);

    }
    
}
