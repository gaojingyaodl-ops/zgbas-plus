package com.spt.bas.report.client.remote;

import com.spt.bas.report.client.constant.ReportConstant;
import com.spt.bas.report.client.entity.RptApplyBusinessPayVo;
import com.spt.tools.data.vo.PageDown;
import com.spt.tools.http.feign.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = ReportConstant.SERVER_NAME,path= ReportConstant.SERVER_NAME+"/rpt/businessPay",url=ReportConstant.SERVER_URL,configuration= FeignConfig.class)
public interface IRptApplyBusinessPayRepClient {

    @PostMapping("findPageContract")
    PageDown<RptApplyBusinessPayVo> findPageContract(@RequestBody RptApplyBusinessPayVo searchVo);

    @PostMapping("selectUserEvectionCost")
    List<RptApplyBusinessPayVo> selectUserEvectionCost(@RequestBody String baseDate);
}