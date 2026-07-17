package com.spt.bas.server.api;

import com.spt.bas.client.entity.KpiActual;
import com.spt.bas.server.service.IKpiActualService;
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
@RequestMapping(value = "bs/kpiActual")
public class KpiActualApi extends BaseApi<KpiActual> {
    @Autowired
    private IKpiActualService kpiActualService;
    @Override
    public IDataService<KpiActual> getService() {
        return kpiActualService;
    }
}
