package com.spt.bas.report.server.api;

import com.spt.bas.report.client.vo.RptCtrContractFinanceSearch;
import com.spt.bas.report.client.vo.*;
import com.spt.bas.report.server.service.IRptCtrContractFinanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * <p>
 *
 * </p>
 *
 * @Author: wm
 * @Date: Created in 2022-06-21 10:24
 */
@RestController
@RequestMapping(value = "/rpt/contractFinance")
public class RptCtrContractFinanceApi {

    @Autowired
    private IRptCtrContractFinanceService ctrContractFinanceService;

    /**
     * 合同列表
     * @param vo
     * @return
     */
    @PostMapping("findContractFinancePage")
    public Page<RptCtrContractFinanceVo> findContractFinancePage(@RequestBody RptCtrContractFinanceSearch vo){
        Page<RptCtrContractFinanceVo> page = ctrContractFinanceService.findContractFinancePage(vo);
        return page;
    }


}
