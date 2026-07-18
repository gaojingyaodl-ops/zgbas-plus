package com.spt.bas.report.server.api;

import com.spt.bas.report.client.entity.RptPrintHistoryReport;
import com.spt.bas.report.server.service.IRptPrintHistoryReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 杨英承
 * @version 1.0.0
 * @date 2022/6/16 10:50
 */
@RestController
@RequestMapping( "/print")
public class RptPrintHistoryReportApi {

    @Autowired
    private IRptPrintHistoryReportService printHistoryReportService;
    /**
     * 添加审批打印记录
     * @param printHistoryReport 打印信息
     * @return 打印次数
     */
    @PostMapping("/addPrintHistory")
    public Integer addPrintHistory(@RequestBody RptPrintHistoryReport printHistoryReport){
        return printHistoryReportService.addPrintHistory(printHistoryReport);
    }
}
