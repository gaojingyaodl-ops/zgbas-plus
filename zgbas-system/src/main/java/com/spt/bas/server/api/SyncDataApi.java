package com.spt.bas.server.api;


import com.spt.bas.client.entity.SyncData;
import com.spt.bas.server.service.ISyncDataService;
import com.spt.tools.data.service.BaseApi;
import com.spt.tools.data.service.IDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "sync/syncData")
public class SyncDataApi extends BaseApi<SyncData> {
    @Autowired
    private ISyncDataService syncDataService;


    @Override
    public IDataService<SyncData> getService() {
        return syncDataService;
    }
}
