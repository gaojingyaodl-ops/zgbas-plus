package com.spt.bas.server.api;

import com.spt.bas.server.service.IBudgetSettlementService;
import com.spt.tools.core.exception.ApplicationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 *  临时手动执行
 * </p>
 *
 * @Author: shengong
 * @Date: Created in 2021-02-19 10:45
 */
@RestController
public class TempManualApi {
    @Autowired
    private IBudgetSettlementService budgetSettlementService;
    @GetMapping("taskStart")
    public void taskStart() throws ApplicationException {
        budgetSettlementService.doTask();
    }
}
