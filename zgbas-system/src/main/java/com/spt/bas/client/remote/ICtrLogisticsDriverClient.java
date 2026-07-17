package com.spt.bas.client.remote;

import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.CtrLogisticsDelivery;
import com.spt.bas.client.entity.CtrLogisticsDriver;
import com.spt.bas.client.vo.CtrLogisticsDeliveryVo;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.http.feign.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;


@FeignClient(name = BasConstants.SERVER_NAME,path= BasConstants.SERVER_NAME+"/ctr/logistics/driver",url=BasConstants.SERVER_URL,configuration=FeignConfig.class)
public interface ICtrLogisticsDriverClient extends BaseClient<CtrLogisticsDriver> {
    
    
    @PostMapping("findByLogisticsIdAndLogisticsDeliveryId")
    List<CtrLogisticsDriver> findByLogisticsIdAndLogisticsDeliveryId(@RequestBody CtrLogisticsDriver driver);
}

