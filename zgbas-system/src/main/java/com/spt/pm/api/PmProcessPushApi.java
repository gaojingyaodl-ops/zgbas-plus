package com.spt.pm.api;

import com.spt.pm.entity.PmProcessPush;
import com.spt.pm.service.IPmProcessPushService;
import com.spt.tools.data.service.BaseApi;
import com.spt.tools.data.service.IDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: gaojy
 * @create 2022/4/26 11:20
 * @version: 1.0
 * @description:
 */
@RestController
@RequestMapping(value = "pm/processPush")
public class PmProcessPushApi extends BaseApi<PmProcessPush> {
    @Autowired
    private IPmProcessPushService pmProcessPushService;

    @Override
    public IDataService<PmProcessPush> getService() {
        return pmProcessPushService;
    }
}
