package com.spt.bas.report.server.api;

import com.spt.bas.report.client.entity.RptInvoiceBill;
import com.spt.bas.report.client.vo.RptInvoiceBillSearchVo;
import com.spt.bas.report.server.service.IRptInvoiceBillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(value = "/rpt/invoiceBill")
public class RptInvoiceBillApi {

    @Autowired
    private IRptInvoiceBillService rptInvoiceBillService;

    /**
     * 开票信息分页查询
     * @param searchVo
     * @return
     */
    @PostMapping("findRptInvoiceBillPage")
    public Page<RptInvoiceBill> findRptInvoiceBillPage(@RequestBody RptInvoiceBillSearchVo searchVo){
        Page<RptInvoiceBill> page = rptInvoiceBillService.findContractFinancePage(searchVo);
        return page;
    }


}
