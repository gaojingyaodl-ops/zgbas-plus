package com.spt.bas.report.server.api;

import com.spt.bas.client.vo.RptBaseCostVo;
import com.spt.bas.report.client.vo.RptBaseCostReportVo;
import com.spt.bas.report.server.service.IRptBaseCostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @author lsj
 * @version 1.0.0
 * @date 2025/01/13 15:58
 */
@RequestMapping("/rpt/baseCost")
@RestController("reportRptBaseCostApi")
public class RptBaseCostApi {

    @Autowired
    private IRptBaseCostService rptBaseCostService;


    @PostMapping("/findPage")
    public Page<RptBaseCostReportVo> findPage(@RequestBody RptBaseCostVo rptBaseCostVo){
        return rptBaseCostService.findPage(rptBaseCostVo);
    }

    @PostMapping("/getTotal")
    Map<String, Object> getTotal(@RequestBody RptBaseCostVo rptBaseCostVo){
        return rptBaseCostService.getTotal(rptBaseCostVo);
    }
}
