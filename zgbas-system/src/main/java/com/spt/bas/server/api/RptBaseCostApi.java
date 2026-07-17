package com.spt.bas.server.api;

import com.spt.bas.client.entity.RptBaseCost;
import com.spt.bas.server.service.IRptBaseCostService;
import com.spt.tools.data.service.IDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.spt.tools.data.service.BaseApi;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "rpt/baseCost")
public class RptBaseCostApi extends BaseApi<RptBaseCost> {

    @Autowired
    private IRptBaseCostService rptBaseCostService;

    @Override
    public IDataService<RptBaseCost> getService() {
        return rptBaseCostService;
    }

    @PostMapping(value = "importExcel")
    public List<String> importExcel(@RequestBody String fileId){
        return rptBaseCostService.initData(fileId);
    }

    /**
     * 通过导入的成本统计年月判断是否有数据
     * @param fileId
     * @return
     */
    @PostMapping(value = "getCostbaseByImportExcel")
    public String getCostbaseByImportExcel(@RequestBody String fileId){
        return rptBaseCostService.getCostbaseByImportExcel(fileId);
    }

    @PostMapping(value = "findSumPage")
    public RptBaseCost findSumPage(@RequestBody Map<String, Object> searchParams){
        return rptBaseCostService.findSumPage(searchParams);
    }

    @PostMapping(value = "refreshUserEvectionCost")
    public void refreshUserEvectionCost(@RequestBody String baseDate){
        rptBaseCostService.refreshUserEvectionCost(baseDate);
    }
}
