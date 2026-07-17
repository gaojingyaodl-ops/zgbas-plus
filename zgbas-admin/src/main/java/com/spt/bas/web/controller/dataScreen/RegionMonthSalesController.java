package com.spt.bas.web.controller.dataScreen;

import cn.hutool.core.collection.CollectionUtil;
import com.spt.auth.sdk.entity.SysDeptSdk;
import com.spt.auth.sdk.open.IAuthOpenFacade;
import com.spt.bas.client.cache.BsDictUtil;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.BsDictData;
import com.spt.bas.report.client.entity.RptRegionMonthSales;
import com.spt.bas.report.client.remote.IRptRegionMonthSalesClient;
import com.spt.bas.report.client.vo.RptRegionMonthSalesVo;
import com.spt.bas.web.shiro.ShiroUtil;
import com.spt.tools.core.json.JsonUtil;
import com.spt.tools.web.util.RenderUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping(value = "/region/month/sales")
public class RegionMonthSalesController {
    @Autowired
    private IRptRegionMonthSalesClient regionMonthSalesClient;
    @Autowired
    private IAuthOpenFacade authOpenFacade;
    @RequestMapping(value = "content", method = RequestMethod.GET)
    public String content(Long id, Model model) {
        // 获取最近12个月的开始月份和结束月份
        LocalDate endMonth = LocalDate.now().withDayOfMonth(1);
        LocalDate startMonth = endMonth.minusMonths(12);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
        String monthEnd = endMonth.format(formatter);
        String monthBegin = startMonth.format(formatter);
        model.addAttribute("monthBegin", monthBegin);
        model.addAttribute("monthEnd", monthEnd);
        return "dataScreen/regionMonthSales";
    }
    /**
     *
     */
    @PostMapping("/getSalesData")
    public void getPersonCostChartData(RptRegionMonthSalesVo searchVo, HttpServletResponse response) {
        HashMap<String, Object> resultMap = new HashMap<>();
        // 获取配置的区域字典
        List<BsDictData> listByCategory = BsDictUtil.getListByCategory(ShiroUtil.getEnterpriseId(), BasConstants.REGION_MONTH_SALES_AREA);
        List<String> deptIdList = listByCategory.stream().map(BsDictData::getDictCd).collect(Collectors.toList());
        searchVo.setDeptIdList(deptIdList);
        List<RptRegionMonthSales> regionMonthSalesList = regionMonthSalesClient.getRegionMonthSalesList(searchVo);
        if(CollectionUtil.isNotEmpty(regionMonthSalesList)){
            // 设置部门名称
            HashMap<Long, String> deptMap = new HashMap<>();
            regionMonthSalesList.forEach(it->{
                if (deptMap.get(it.getDeptId())==null){
                    SysDeptSdk sysDeptSdk = authOpenFacade.findDeptById(it.getDeptId());
                    it.setDeptName(sysDeptSdk.getDeptName());
                    deptMap.put(it.getDeptId(),sysDeptSdk.getDeptName());
                } else {
                    it.setDeptName(deptMap.get(it.getDeptId()));
                }
            });
            // 先获取所有的月份，因为可能有的部门月份不存在数据，要置0
            List<String> xAxisData = regionMonthSalesList.stream()
                    .map(RptRegionMonthSales::getContractTime)
                    .distinct()
                    .sorted()
                    .collect(Collectors.toList());
            // 部门分组
            Map<String, List<RptRegionMonthSales>> deptNameMap = regionMonthSalesList.stream().collect(Collectors.groupingBy(RptRegionMonthSales::getDeptName));
            List<Map<String,Object>> yAxisData = new ArrayList<>();
            List<String> legendData = new ArrayList<>();
            deptNameMap.forEach((deptName,salesList)->{
                List<BigDecimal> yData = new ArrayList();
                HashMap<String, Object> yMap = new HashMap<>();
                xAxisData.forEach(time -> {
                    BigDecimal totalAmount = salesList.stream()
                            .filter(it -> it.getContractTime().equals(time))
                            .map(RptRegionMonthSales::getSumTotalAmount)
                            .findFirst()
                            .orElse(BigDecimal.ZERO);
                    yData.add(totalAmount);
                });
                yMap.put("data",yData);
                yMap.put("name",deptName);
                legendData.add(deptName);
                yAxisData.add(yMap);
            });
            resultMap.put("xAxisData",xAxisData);
            resultMap.put("yAxisData",yAxisData);
            resultMap.put("legendData",legendData);
            resultMap.put("monthBegin",searchVo.getMonthBegin());
            resultMap.put("monthEnd",searchVo.getMonthEnd());
        }
        RenderUtil.renderJson(JsonUtil.obj2Json(resultMap), response);
    }
}
