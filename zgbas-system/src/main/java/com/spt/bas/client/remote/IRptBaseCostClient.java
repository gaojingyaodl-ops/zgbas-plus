package com.spt.bas.client.remote;

import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.RptBaseCost;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.http.feign.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;

@FeignClient(name = BasConstants.SERVER_NAME,path= BasConstants.SERVER_NAME+"/rpt/baseCost",url=BasConstants.SERVER_URL,configuration=FeignConfig.class)
public interface IRptBaseCostClient extends BaseClient<RptBaseCost> {
    @PostMapping(value = "getCostbaseByImportExcel")
    String getCostbaseByImportExcel(@RequestBody String fileId);

    @PostMapping(value = "importExcel")
    List<String> importExcel(@RequestBody String fileId);

    @PostMapping(value = "findSumPage")
    RptBaseCost findSumPage(@RequestBody Map<String, Object> searchParams);

    @PostMapping(value = "refreshUserEvectionCost")
    void refreshUserEvectionCost(@RequestBody String baseDate);
}
