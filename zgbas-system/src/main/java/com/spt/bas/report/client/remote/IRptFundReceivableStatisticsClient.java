package com.spt.bas.report.client.remote;

import com.spt.bas.report.client.constant.ReportConstant;
import com.spt.bas.report.client.entity.RptFundReceivableStatistics;
import com.spt.bas.report.client.vo.RptFundReceivableStatisticsVo;
import com.spt.tools.data.vo.PageDown;
import com.spt.tools.http.feign.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = ReportConstant.SERVER_NAME, path = ReportConstant.SERVER_NAME + "/rpt/fundReceivableStatistics", url = ReportConstant.SERVER_URL, configuration = FeignConfig.class)
public interface IRptFundReceivableStatisticsClient {
    @PostMapping("findPage")
    public PageDown<RptFundReceivableStatistics> findPage(@RequestBody RptFundReceivableStatisticsVo searchVo);
}
