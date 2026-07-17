package com.spt.bas.server.api;

import com.spt.bas.client.entity.ApplyLogisticsAdjust;
import com.spt.bas.server.service.IApplyLogisticsAdjustService;
import com.spt.tools.data.service.BaseApi;
import com.spt.tools.data.service.IDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/apply/logistics/adjust")
public class ApplyLogisticsAdjustApi extends BaseApi<ApplyLogisticsAdjust> {

    @Autowired
    private IApplyLogisticsAdjustService applyLogisticsAdjustService;
    
    @Override
    public IDataService<ApplyLogisticsAdjust> getService() {
        return applyLogisticsAdjustService;
    }
}
