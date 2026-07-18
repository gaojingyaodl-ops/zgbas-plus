package com.spt.bas.report.server.api;

import com.spt.bas.report.client.entity.RptFundReceivableStatistics;
import com.spt.bas.report.client.vo.RptFundReceivableStatisticsVo;
import com.spt.bas.report.server.service.IRptFundReceivableStatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/rpt/fundReceivableStatistics")
public class RptFundReceivableStatisticsApi {
    @Autowired
    private IRptFundReceivableStatisticsService fundReceivableStatisticsService;

    @PostMapping("findPage")
    public Page<RptFundReceivableStatistics> findRptContractSettlementPage(@RequestBody RptFundReceivableStatisticsVo searchVo) {
        return fundReceivableStatisticsService.findPage(searchVo);
    }
}
