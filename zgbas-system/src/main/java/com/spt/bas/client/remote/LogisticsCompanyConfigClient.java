package com.spt.bas.client.remote;

import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.LogisticsCompanyConfig;
import com.spt.bas.client.vo.BusinessDeliveryExcelVo;
import com.spt.bas.client.vo.CtrContractOphisRequest;
import com.spt.bas.client.vo.CtrContractOphisVo;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.http.feign.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;


@FeignClient(name = BasConstants.SERVER_NAME,path= BasConstants.SERVER_NAME+"/ctr/logisticsCompanyConfig",url=BasConstants.SERVER_URL,configuration=FeignConfig.class)
public interface LogisticsCompanyConfigClient extends BaseClient<LogisticsCompanyConfig> {


//    @PostMapping(value = "addHis")
//    void addHis(@RequestBody CtrContractOphisRequest request);
//
        @PostMapping(value = "getByCarrier")
        LogisticsCompanyConfig getByCarrier(@RequestBody LogisticsCompanyConfig logisticsCompanyConfig);

        @PostMapping(value = "findByOurCompanyNames")
        List<LogisticsCompanyConfig> findByOurCompanyNames(@RequestBody String ourCompanyName);
}

