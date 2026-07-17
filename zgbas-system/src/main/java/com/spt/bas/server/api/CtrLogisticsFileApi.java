package com.spt.bas.server.api;

import com.spt.bas.client.entity.CtrLogisticsFile;
import com.spt.bas.client.vo.CtrLogisticsFileRespVo;
import com.spt.bas.server.logistics.service.ICtrLogisticsFileService;
import com.spt.tools.data.service.BaseApi;
import com.spt.tools.data.service.IDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * 物流单据附件
 */
@RestController
@RequestMapping(value = "ctr/logistics/file")
public class CtrLogisticsFileApi extends BaseApi<CtrLogisticsFile> {
    @Autowired
    private ICtrLogisticsFileService ctrLogisticsFileService;

    @Override
    public IDataService<CtrLogisticsFile> getService() {
        return ctrLogisticsFileService;
    }

    @PostMapping("findByLogisticsIdAndLogisticsDeliveryId")
    CtrLogisticsFileRespVo findByLogisticsIdAndLogisticsDeliveryId(@RequestBody CtrLogisticsFile logisticsFile){
        return ctrLogisticsFileService.findByLogisticsIdAndLogisticsDeliveryId(logisticsFile.getLogisticsId(),logisticsFile.getLogisticsDeliveryId());
    }
    
    
}
