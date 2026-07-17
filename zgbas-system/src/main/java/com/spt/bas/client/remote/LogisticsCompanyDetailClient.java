package com.spt.bas.client.remote;

import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.LogisticsCompanyConfig;
import com.spt.bas.client.entity.LogisticsCompanyDetail;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.http.feign.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.math.BigDecimal;
import java.util.List;


@FeignClient(name = BasConstants.SERVER_NAME,path= BasConstants.SERVER_NAME+"/ctr/logisticsCompanyDetail",url=BasConstants.SERVER_URL,configuration=FeignConfig.class)
public interface LogisticsCompanyDetailClient extends BaseClient<LogisticsCompanyDetail> {

    @PostMapping("findByCarrierScoreAVG")
    BigDecimal findByCarrierScoreAVG(@RequestBody Long id) ;

    @PostMapping("findByLogisticsCompanyId")
    List<LogisticsCompanyDetail> findByLogisticsCompanyId(@RequestBody Long id) ;

}

