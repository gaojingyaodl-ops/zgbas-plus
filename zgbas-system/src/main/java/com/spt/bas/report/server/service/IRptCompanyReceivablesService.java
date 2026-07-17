package com.spt.bas.report.server.service;

import com.spt.bas.report.client.entity.RptCompanyReceivables;
import com.spt.bas.report.client.entity.RptInvoiceBill;
import com.spt.bas.report.client.vo.RptCompanyReceivablesSearchVo;
import com.spt.bas.report.client.vo.RptInvoiceBillSearchVo;
import org.springframework.data.domain.Page;


public interface IRptCompanyReceivablesService {
    /**
     * 客户应收款分页查询
     *
     */
    Page<RptCompanyReceivables> findRptCompanyReceivablesPage(RptCompanyReceivablesSearchVo searchVo);
    
    Page<RptCompanyReceivables> findRptCompanyReceivablesDetailPage(RptCompanyReceivablesSearchVo searchVo);
    
    RptCompanyReceivables findRptCompanyReceivablesSum(RptCompanyReceivablesSearchVo searchVo);


}
