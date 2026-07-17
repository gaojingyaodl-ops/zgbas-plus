package com.spt.bas.report.client.remote;

import com.spt.bas.report.client.constant.ReportConstant;
import com.spt.bas.report.client.vo.RptCreditBusinessCommission;
import com.spt.bas.report.client.vo.RptCreditBusinessCommissionSearchVo;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.data.vo.PageDown;
import com.spt.tools.http.feign.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@FeignClient(name = ReportConstant.SERVER_NAME,path= ReportConstant.SERVER_NAME+"/rpt/creditContract",url=ReportConstant.SERVER_URL,configuration= FeignConfig.class)
public interface IRptCreditClient extends BaseClient<RptCreditBusinessCommission> {

    @PostMapping("/findCommissionPage")
    public PageDown<RptCreditBusinessCommission> findCreditBusinessCommissionPage(@RequestBody RptCreditBusinessCommissionSearchVo searchVo);
}
