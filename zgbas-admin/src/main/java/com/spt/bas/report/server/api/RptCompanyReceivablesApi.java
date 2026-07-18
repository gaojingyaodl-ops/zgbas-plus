package com.spt.bas.report.server.api;

import com.spt.bas.report.client.entity.RptCompanyReceivables;
import com.spt.bas.report.client.vo.RptCompanyReceivablesSearchVo;
import com.spt.bas.report.server.service.IRptCompanyReceivablesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(value = "/rpt/companyReceivables")
public class RptCompanyReceivablesApi {

    @Autowired
    private IRptCompanyReceivablesService rptCompanyReceivablesService;

    /**
     * 客户应收款分页查询
     * @param searchVo
     * @return
     */
    @PostMapping("findRptCompanyReceivablesPage")
    public Page<RptCompanyReceivables> findRptCompanyReceivablesPage(@RequestBody RptCompanyReceivablesSearchVo searchVo){
        Page<RptCompanyReceivables> page = rptCompanyReceivablesService.findRptCompanyReceivablesPage(searchVo);
        return page;
    }
    
    /**
     * 客户应收款分页查询
     * @param searchVo
     * @return
     */
    @PostMapping("findRptCompanyReceivablesDetailPage")
    public Page<RptCompanyReceivables> findRptCompanyReceivablesDetailPage(@RequestBody RptCompanyReceivablesSearchVo searchVo){
        Page<RptCompanyReceivables> page = rptCompanyReceivablesService.findRptCompanyReceivablesDetailPage(searchVo);
        return page;
    }
    
    /**
     * 客户应收款合计查询
     * @param searchVo
     * @return
     */
    @PostMapping("findRptCompanyReceivablesSum")
    public RptCompanyReceivables findRptCompanyReceivablesSum(@RequestBody RptCompanyReceivablesSearchVo searchVo){
        return rptCompanyReceivablesService.findRptCompanyReceivablesSum(searchVo);
    }


}
