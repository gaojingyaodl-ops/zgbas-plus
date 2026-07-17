package com.spt.bas.report.client.remote;

import com.spt.bas.report.client.constant.ReportConstant;
import com.spt.bas.report.client.vo.RptSummaryResultVo;
import com.spt.bas.report.client.vo.RptUserRoiVo;
import com.spt.tools.http.feign.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author 杨英承
 * @version 1.0.0
 * @date 2023/3/21 15:52
 */
@FeignClient(name = ReportConstant.SERVER_NAME, path = ReportConstant.SERVER_NAME + "/summaryRoi", url = ReportConstant.SERVER_URL, configuration = FeignConfig.class)
public interface IRptSummaryRoiClient {

    @PostMapping("/findPage")
    RptSummaryResultVo findPage(@RequestBody RptUserRoiVo vo);

}
