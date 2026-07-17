package com.spt.bas.server.api;

import com.spt.bas.client.entity.PerformanceCommissionDept;
import com.spt.bas.server.performance.service.IPerformanceCommissionDeptService;
import com.spt.tools.data.service.BaseApi;
import com.spt.tools.data.service.IDataService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.Date;

/**
 * @author MoonLight
 * @version 1.0
 * @description
 * @date 2026/2/28 16:19
 */
@RestController
@RequestMapping(value = "performance/dept")
public class PerformanceCommissionDeptApi extends BaseApi<PerformanceCommissionDept> {

    @Resource
    private IPerformanceCommissionDeptService performanceCommissionDeptService;

    @Override
    public IDataService<PerformanceCommissionDept> getService() {
        return performanceCommissionDeptService;
    }

    @RequestMapping(value = "initPerformanceCommissionDept")
    void initPerformanceCommissionUser(@RequestBody PerformanceCommissionDept performanceCommissionDept){
        YearMonth yearMonth = YearMonth.parse(performanceCommissionDept.getPerformanceDate());
        LocalDate firstDay = yearMonth.atDay(1);
        Date performanceDate = Date.from(firstDay.atStartOfDay(ZoneId.systemDefault()).toInstant());
        performanceCommissionDeptService.initPerformanceCommissionDept(performanceDate);
    }

    @RequestMapping(value = "findPerformanceCommissionDept")
    public PerformanceCommissionDept findPerformanceCommissionDept(@RequestBody PerformanceCommissionDept queryVo){
        return performanceCommissionDeptService.findPerformanceCommissionDept(queryVo);
    }
}
