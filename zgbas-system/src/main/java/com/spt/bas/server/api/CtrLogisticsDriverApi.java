package com.spt.bas.server.api;

import com.spt.bas.client.entity.CtrLogisticsDelivery;
import com.spt.bas.client.entity.CtrLogisticsDriver;
import com.spt.bas.client.vo.CtrLogisticsDeliveryVo;
import com.spt.bas.server.logistics.service.ICtrLogisticsDeliveryService;
import com.spt.bas.server.logistics.service.ICtrLogisticsDriverService;
import com.spt.tools.data.service.BaseApi;
import com.spt.tools.data.service.IDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 */
@RestController
@RequestMapping(value = "ctr/logistics/driver")
public class CtrLogisticsDriverApi extends BaseApi<CtrLogisticsDriver> {
    @Autowired
    private ICtrLogisticsDriverService ctrLogisticsDriverService;

    @Override
    public IDataService<CtrLogisticsDriver> getService() {
        return ctrLogisticsDriverService;
    }

    @PostMapping("findByLogisticsIdAndLogisticsDeliveryId")
    List<CtrLogisticsDriver> findByLogisticsIdAndLogisticsDeliveryId(@RequestBody CtrLogisticsDriver driver){
        return ctrLogisticsDriverService.findByLogisticsIdAndLogisticsDeliveryId(driver.getLogisticsId(),driver.getLogisticsDeliveryId());
    }
    
    
}
