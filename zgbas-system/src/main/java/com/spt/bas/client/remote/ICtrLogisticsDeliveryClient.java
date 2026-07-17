package com.spt.bas.client.remote;

import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.CtrLogisticsDelivery;
import com.spt.bas.client.entity.CtrLogisticsFile;
import com.spt.bas.client.vo.CtrLogisticsDeliveryVo;
import com.spt.bas.client.vo.CtrLogisticsReqVo;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.http.feign.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;


@FeignClient(name = BasConstants.SERVER_NAME,path= BasConstants.SERVER_NAME+"/ctr/logistics/delivery",url=BasConstants.SERVER_URL,configuration=FeignConfig.class)
public interface ICtrLogisticsDeliveryClient extends BaseClient<CtrLogisticsDelivery> {

    @PostMapping("findByLogisticsIdAndLogisticsCount")
    CtrLogisticsDelivery findByLogisticsIdAndLogisticsCount(@RequestBody CtrLogisticsDeliveryVo logisticsDeliveryVo);

    @PostMapping("findByLogisticsId")
    List<CtrLogisticsDelivery> findByLogisticsId(@RequestBody CtrLogisticsDeliveryVo logisticsDeliveryVo);

    @PostMapping("generateDeliveryFile")
    CtrLogisticsFile generateDeliveryFile(@RequestBody CtrLogisticsReqVo reqVo) throws ApplicationException;

    @PostMapping("generateLogisticsSealUsage")
    CtrLogisticsFile generateLogisticsSealUsage(@RequestBody CtrLogisticsReqVo reqVo) throws ApplicationException;

    @PostMapping("exportExcelTemplate")
    Map<String, String> exportExcelTemplate(@RequestBody CtrLogisticsReqVo reqVo) throws ApplicationException;

}

