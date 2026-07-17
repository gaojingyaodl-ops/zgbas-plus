package com.spt.bas.report.client.remote;

import com.spt.bas.report.client.constant.ReportConstant;
import com.spt.bas.report.client.entity.RptEvaluateTotalSearch;
import com.spt.bas.report.client.vo.RptEvaluateTotalVo;
import com.spt.tools.data.vo.PageDown;
import com.spt.tools.http.feign.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = ReportConstant.SERVER_NAME,path= ReportConstant.SERVER_NAME+"/evaluate/total",url=ReportConstant.SERVER_URL,configuration= FeignConfig.class)
public interface IRptEvaluateTotalClient {
    @PostMapping("findPageEvaluateTotal")
    public PageDown<RptEvaluateTotalVo> findPageEvaluateTotal(@RequestBody RptEvaluateTotalSearch vo);
}
