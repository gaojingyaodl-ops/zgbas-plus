package com.spt.bas.server.api;

import com.spt.bas.client.entity.ApplyCreditCycle;
import com.spt.bas.client.entity.ApplyRate;
import com.spt.bas.server.service.IApplyCreditCycleService;
import com.spt.bas.server.service.IApplyRateService;
import com.spt.tools.data.service.BaseApi;
import com.spt.tools.data.service.IDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 *
 * </p>
 *
 * @Author: shengong
 * @Date: Created in 2021-01-12 14:32
 */
@RestController
@RequestMapping(value = "/apply/creditCycle")
public class ApplyCreditCycleApi extends BaseApi<ApplyCreditCycle> {

    @Autowired
    private IApplyCreditCycleService applyCreditCycleService;

    @Override
    public IDataService<ApplyCreditCycle> getService() {
        return applyCreditCycleService;
    }
}
