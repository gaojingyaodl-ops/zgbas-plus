package com.spt.bas.report.client.remote;

import com.spt.bas.client.vo.RptBaseCostVo;
import com.spt.bas.report.client.constant.ReportConstant;
import com.spt.bas.report.client.vo.RptBaseCostReportVo;
import com.spt.tools.data.vo.PageDown;
import com.spt.tools.http.feign.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

/**
 * @author lsj
 * @version 1.0.0
 * @date 2025/01/13 15:58
 */
@FeignClient(name = ReportConstant.SERVER_NAME, path = ReportConstant.SERVER_NAME + "/rpt/baseCost", url = ReportConstant.SERVER_URL, configuration = FeignConfig.class)
public interface IRptBaseCostClient {

    @PostMapping("/findPage")
    PageDown<RptBaseCostReportVo> findPage(@RequestBody RptBaseCostVo rptBaseCostVo);

    /**
     * 合计
     *
     * @return 合计
     */
    @PostMapping("/getTotal")
    Map<String, Object> getTotal(@RequestBody RptBaseCostVo rptBaseCostVo);

}
