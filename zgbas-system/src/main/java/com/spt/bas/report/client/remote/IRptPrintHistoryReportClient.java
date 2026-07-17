package com.spt.bas.report.client.remote;

import com.spt.bas.report.client.constant.ReportConstant;
import com.spt.bas.report.client.entity.RptPrintHistoryReport;
import com.spt.tools.http.feign.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author 杨英承
 * @version 1.0.0
 * @date 2022/6/16 10:37
 */
@FeignClient(name = ReportConstant.SERVER_NAME,path= ReportConstant.SERVER_NAME+"/print",url=ReportConstant.SERVER_URL,configuration= FeignConfig.class)
public interface IRptPrintHistoryReportClient {

    /**
     * 添加审批打印记录
     * @param printHistoryReport 打印信息
     * @return 打印次数
     */
    @PostMapping("/addPrintHistory")
    Integer addPrintHistory(@RequestBody RptPrintHistoryReport printHistoryReport);
}
