package com.spt.bas.server.api;

import com.spt.bas.client.entity.PerformanceCommissionUser;
import com.spt.bas.server.performance.service.IPerformanceCommissionUserService;
import com.spt.tools.data.service.BaseApi;
import com.spt.tools.jpa.service.IBaseService;
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
 * @date 2026/2/27 15:41
 */
@RestController
@RequestMapping(value = "performance/user")
public class PerformanceCommissionUserApi extends BaseApi<PerformanceCommissionUser> {
    @Resource
    private IPerformanceCommissionUserService performanceCommissionUserService;

    @Override
    public IBaseService<PerformanceCommissionUser> getService() {
        return performanceCommissionUserService;
    }

    @RequestMapping(value = "initPerformanceCommissionUser")
    void initPerformanceCommissionUser(@RequestBody PerformanceCommissionUser performanceCommissionUser){
        YearMonth yearMonth = YearMonth.parse(performanceCommissionUser.getPerformanceDate());
        LocalDate firstDay = yearMonth.atDay(1);
        Date performanceDate = Date.from(firstDay.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Long userId = performanceCommissionUser.getUserId();
        performanceCommissionUserService.initPerformanceCommissionUser(performanceDate, userId);
    }

    @RequestMapping(value = "findPerformanceCommissionUser")
    public PerformanceCommissionUser findPerformanceCommissionUser(@RequestBody PerformanceCommissionUser queryVo){
        return performanceCommissionUserService.findPerformanceCommissionUser(queryVo);
    }
}
