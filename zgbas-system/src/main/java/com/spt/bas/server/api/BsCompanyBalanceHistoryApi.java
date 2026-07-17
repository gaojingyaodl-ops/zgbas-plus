package com.spt.bas.server.api;


import com.spt.bas.client.entity.BsCompanyBalanceHistory;
import com.spt.bas.server.service.BsCompanyBalanceHistoryService;
import com.spt.tools.data.service.BaseApi;
import com.spt.tools.data.service.IDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

import java.math.BigDecimal;

@RestController
@RequestMapping(value = "bsCompanyBalance/history")
public class BsCompanyBalanceHistoryApi extends BaseApi<BsCompanyBalanceHistory> {

    @Autowired
    private BsCompanyBalanceHistoryService changeRecordService;

    @Override
    public IDataService<BsCompanyBalanceHistory> getService() {
        return changeRecordService;
    }

    @PostMapping("findByCompanyId")
    public List<BsCompanyBalanceHistory> findByCompanyId(@RequestParam("companyId") Long companyId){
        return changeRecordService.findByCompanyId(companyId);
    }
    @GetMapping("updateLastBalance")
    public void updateLastBalance(@RequestParam("companyId") Long companyId, @RequestParam("lastBalance") BigDecimal lastBalance){
        changeRecordService.updateLastBalance(companyId,lastBalance);
    }
    @GetMapping("updateLastBalanceStatus")
    public void updateLastBalanceStatus(@RequestParam("companyId") Long companyId,@RequestParam("changeType") String changeType){
        changeRecordService.updateLastBalanceStatus(companyId,changeType);
    }
}

