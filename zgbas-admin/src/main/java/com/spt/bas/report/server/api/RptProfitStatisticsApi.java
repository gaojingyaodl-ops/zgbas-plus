package com.spt.bas.report.server.api;

import com.spt.bas.report.client.entity.*;
import com.spt.bas.report.client.vo.RptProfitStatisticsSearchVo;
import com.spt.bas.report.server.service.IRptProfitStatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 *  利润表
 */
@RestController
@RequestMapping(value = "/profit/statistics")
public class RptProfitStatisticsApi {

    @Autowired
    private IRptProfitStatisticsService profitStatisticsService;

    /**
     * 获取利润表数据
     * @param searchVo
     * @return
     */
    @PostMapping("getRptProfitStatistics")
    public RptProfitStatistics getRptProfitStatistics(@RequestBody RptProfitStatisticsSearchVo searchVo) {
       return profitStatisticsService.getRptProfitStatistics(searchVo);
    }

}
