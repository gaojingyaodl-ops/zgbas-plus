package com.spt.bas.server.api;

import com.spt.bas.client.entity.CtrLogisticsDelivery;
import com.spt.bas.client.entity.CtrLogisticsFile;
import com.spt.bas.client.vo.CtrLogisticsDeliveryVo;
import com.spt.bas.client.vo.CtrLogisticsReqVo;
import com.spt.bas.server.logistics.service.ICtrLogisticsDeliveryService;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.data.service.BaseApi;
import com.spt.tools.data.service.IDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 */
@RestController
@RequestMapping(value = "ctr/logistics/delivery")
public class CtrLogisticsDeliveryApi extends BaseApi<CtrLogisticsDelivery> {
    @Autowired
    private ICtrLogisticsDeliveryService ctrLogisticsDeliveryService;

    @Override
    public IDataService<CtrLogisticsDelivery> getService() {
        return ctrLogisticsDeliveryService;
    }

    @PostMapping("findByLogisticsIdAndLogisticsCount")
    CtrLogisticsDelivery findByLogisticsIdAndLogisticsCount(@RequestBody CtrLogisticsDeliveryVo logisticsDeliveryVo){
        return ctrLogisticsDeliveryService.findByLogisticsIdAndLogisticsCount(logisticsDeliveryVo.getLogisticsId(),logisticsDeliveryVo.getLogisticsCount());
    }

    @PostMapping("findByLogisticsId")
    List<CtrLogisticsDelivery> findByLogisticsId(@RequestBody CtrLogisticsDeliveryVo logisticsDeliveryVo){
        return ctrLogisticsDeliveryService.findByLogisticsId(logisticsDeliveryVo.getLogisticsId());
    }

    @PostMapping("generateDeliveryFile")
    public CtrLogisticsFile generateDeliveryFile(@RequestBody CtrLogisticsReqVo reqVo) throws ApplicationException {
        return ctrLogisticsDeliveryService.generateDeliveryFile(reqVo);
    }

    @PostMapping("generateLogisticsSealUsage")
    public CtrLogisticsFile generateLogisticsSealUsage(@RequestBody CtrLogisticsReqVo reqVo) throws ApplicationException {
        return ctrLogisticsDeliveryService.generateLogisticsSealUsage(reqVo);
    }

    @PostMapping("exportExcelTemplate")
    public Map<String, String> exportExcelTemplate(@RequestBody CtrLogisticsReqVo reqVo) throws ApplicationException {
        return ctrLogisticsDeliveryService.exportExcelTemplate(reqVo);
    }
}
