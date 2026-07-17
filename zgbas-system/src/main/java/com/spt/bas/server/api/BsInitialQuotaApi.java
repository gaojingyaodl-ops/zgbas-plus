package com.spt.bas.server.api;

import com.spt.bas.client.entity.BsInitialQuota;
import com.spt.bas.server.service.IBsInitialQuotaService;
import com.spt.tools.data.service.BaseApi;
import com.spt.tools.data.service.IDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 *     企业初始额度
 * </p>
 *
 * @Author: shengong
 * @Date: Created in 2021-01-19 18:16
 */
@RestController
@RequestMapping(value = "bs/initialQuota")
public class BsInitialQuotaApi extends BaseApi<BsInitialQuota> {
    @Autowired
    private IBsInitialQuotaService bsInitialQuotaService;
    @Override
    public IDataService<BsInitialQuota> getService() {
        return bsInitialQuotaService;
    }
}
