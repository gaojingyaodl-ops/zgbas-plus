package com.spt.bas.report.server.service;

import com.spt.bas.report.client.entity.RptInvoiceBill;
import com.spt.bas.report.client.vo.RptInvoiceBillSearchVo;
import org.springframework.data.domain.Page;


public interface IRptInvoiceBillService {
    /**
     * 开票分页查询
     *
     */
    Page<RptInvoiceBill> findContractFinancePage(RptInvoiceBillSearchVo searchVo);


}
