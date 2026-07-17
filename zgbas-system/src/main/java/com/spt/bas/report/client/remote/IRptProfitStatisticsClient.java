package com.spt.bas.report.client.remote;

import com.spt.bas.report.client.constant.ReportConstant;
import com.spt.bas.report.client.entity.*;
import com.spt.bas.report.client.vo.RptProfitStatisticsSearchVo;
import com.spt.tools.http.feign.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


/**
 * 利润表
 */
@FeignClient(name = ReportConstant.SERVER_NAME,path= ReportConstant.SERVER_NAME+"/profit/statistics",url=ReportConstant.SERVER_URL,configuration= FeignConfig.class)
public interface IRptProfitStatisticsClient {

    /**
     * 获取利润表数据
     * @param searchVo
     * @return
     */
    @PostMapping("getRptProfitStatistics")
    RptProfitStatistics getRptProfitStatistics(@RequestBody RptProfitStatisticsSearchVo searchVo);

}

