package com.spt.bas.web.controller.dataScreen;

import cn.hutool.core.collection.CollectionUtil;
import com.spt.bas.client.cache.BsDictUtil;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.report.client.entity.RptProvinceCustomerSales;
import com.spt.bas.report.client.remote.IRptProvinceCustomerSalesClient;
import com.spt.bas.report.client.vo.RptProvinceCustomerSalesVo;
import com.spt.bas.web.util.StringUtils;
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
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Controller
@RequestMapping(value = "/province/customer/sales")
public class ProvinceCustomerSalesController {
    @Autowired
    private IRptProvinceCustomerSalesClient provinceCustomerSalesClient;

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
        // 数据字段获取部门
        model.addAttribute("deptJson", JsonUtil.obj2Json(BsDictUtil.getListByCategory(BasConstants.ZG_ENTERPRISE_ID, BasConstants.PROVINCE_CUSTOMER_SALES_AREA)));
        return "dataScreen/provinceCustomerSales";
    }

    /**
     * 获取省份统计客户销售额
     *
     * @param searchVo
     * @param response
     */
    @PostMapping("/getProvinceCustomerSales")
    public void getProvinceCustomerSales(RptProvinceCustomerSalesVo searchVo, HttpServletResponse response) {
        if (StringUtils.isNotEmpty(searchVo.getDeptId())) {
            // 置null,查全部
            if (searchVo.getDeptId().equals("all")) {
                searchVo.setDeptId(null);
            }
        }
        List<RptProvinceCustomerSales> provinceCustomerSales = provinceCustomerSalesClient.getProvinceCustomerSales(searchVo);
        List<Map<String, Object>> seriesData = new ArrayList<Map<String, Object>>();
        if (CollectionUtil.isNotEmpty(provinceCustomerSales)) {
            // 销售总额
            BigDecimal sumTotalAmount = provinceCustomerSales.stream().map(RptProvinceCustomerSales::getTotalAmount)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            // 创建一个占比小于1%的数据，展示地区为其他
            Map<String, Object> lowMap = new HashMap<>();
            lowMap.put("name", "其他");
            lowMap.put("value", BigDecimal.ZERO);
            // 处理数据
            provinceCustomerSales.stream().forEach(it -> {
                // 存储单个数据
                Map<String, Object> map = new HashMap<>();
                if (StringUtils.isEmpty(it.getProvinceName())) {
                    it.setProvinceName("未知省份");
                }
                BigDecimal proportion = it.getTotalAmount().
                        divide(sumTotalAmount, 10, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100))
                        .setScale(2, RoundingMode.HALF_UP);
                // 占比小于1%的存入lowMap 对象中，占比累加
                if (proportion.compareTo(BigDecimal.valueOf(1)) < 0) {
                    BigDecimal value = (BigDecimal) lowMap.get("value");
                    lowMap.put("value", value.add(proportion));
                } else {
                    map.put("name", it.getProvinceName());
                    map.put("value", proportion);
                    seriesData.add(map);
                }

            });
            BigDecimal value = (BigDecimal) lowMap.get("value");
            // 其他占比为0的，分布图就不展示了
            if (value.compareTo(BigDecimal.ZERO) > 0) {
                seriesData.add(lowMap);
            }
        }
        RenderUtil.renderJson(JsonUtil.obj2Json(seriesData), response);
    }
}
