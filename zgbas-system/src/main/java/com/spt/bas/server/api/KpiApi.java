package com.spt.bas.server.api;

import com.spt.bas.client.entity.Kpi;
import com.spt.bas.server.service.IKpiService;
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
 * @Date: Created in 2021-05-10 13:51
 */
@RestController
@RequestMapping(value = "bs/kpi")
public class KpiApi extends BaseApi<Kpi> {
    @Autowired
    private IKpiService kpiService;
    @Override
    public IDataService<Kpi> getService() {
        return kpiService;
    }
}
