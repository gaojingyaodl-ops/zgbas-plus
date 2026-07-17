package com.spt.bas.client.remote;

import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.CtrLogisticsFile;
import com.spt.bas.client.vo.CtrLogisticsFileRespVo;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.http.feign.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;



@FeignClient(name = BasConstants.SERVER_NAME,path= BasConstants.SERVER_NAME+"/ctr/logistics/file",url=BasConstants.SERVER_URL,configuration=FeignConfig.class)
public interface ICtrLogisticsFileClient extends BaseClient<CtrLogisticsFile> {
    
    
    @PostMapping("findByLogisticsIdAndLogisticsDeliveryId")
    CtrLogisticsFileRespVo findByLogisticsIdAndLogisticsDeliveryId(@RequestBody CtrLogisticsFile logisticsFile);
}

