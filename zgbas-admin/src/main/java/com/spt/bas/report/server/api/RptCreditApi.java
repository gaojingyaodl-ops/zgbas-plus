package com.spt.bas.report.server.api;

import com.spt.bas.report.client.vo.RptCreditBusinessCommission;
import com.spt.bas.report.client.vo.RptCreditBusinessCommissionSearchVo;
import com.spt.bas.report.server.service.IRptCtrContractReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/rpt/creditContract")
public class RptCreditApi {
    @Autowired
    private IRptCtrContractReportService ctrContractReportService;

    @PostMapping("/findCommissionPage")
    public Page<RptCreditBusinessCommission> findCreditBusinessCommissionPage(@RequestBody RptCreditBusinessCommissionSearchVo searchVo){
        return ctrContractReportService.findCreditBusinessCommissionPage(searchVo);
    }
}
