package com.spt.bas.server.api;

import com.spt.bas.client.entity.SyncDataPicc;
import com.spt.bas.server.service.ISyncDataPiccService;
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
 * @Date: Created in 2021-03-03 10:48
 */
@RestController
@RequestMapping(value = "bs/syncDataPicc")
public class SyncDataPiccApi extends BaseApi<SyncDataPicc> {
    @Autowired
    private ISyncDataPiccService syncDataPiccService;

    @Override
    public IDataService<SyncDataPicc> getService() {
        return syncDataPiccService;
    }
}
