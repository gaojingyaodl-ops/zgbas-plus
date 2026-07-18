package com.spt.bas.report.server.api;

import com.spt.bas.report.client.entity.RptNotBillStatistics;
import com.spt.bas.report.client.vo.RptNotBillStatisticsSearchVo;
import com.spt.bas.report.server.service.IRptNotBillStatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(value = "/rpt/notBillStatistics")
public class RptNotBillStatisticsApi {

    @Autowired
    private IRptNotBillStatisticsService rptNotBillStatisticsService;

    /**
     * 未收票明细分页查询
     * @param searchVo
     * @return
     */
    @PostMapping("findRptNotBillStatisticsPage")
    public Page<RptNotBillStatistics> findRptNotBillStatisticsPage(@RequestBody RptNotBillStatisticsSearchVo searchVo){
        Page<RptNotBillStatistics> page = rptNotBillStatisticsService.findRptNotBillStatisticsPage(searchVo);
        return page;
    }
    
    /**
     * 未收票明细合计查询
     * @param searchVo
     * @return
     */
    @PostMapping("findRptNotBillStatisticsSum")
    public RptNotBillStatistics findRptNotBillStatisticsSum(@RequestBody RptNotBillStatisticsSearchVo searchVo){
        return rptNotBillStatisticsService.findRptNotBillStatisticsSum(searchVo);
    }


}
