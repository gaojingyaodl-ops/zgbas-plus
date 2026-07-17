package com.spt.pm.api;

import com.spt.pm.entity.PmApprovePush;
import com.spt.pm.service.IPmApprovePushService;
import com.spt.tools.data.service.BaseApi;
import com.spt.tools.jpa.service.IBaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: gaojy
 * @create 2022/4/26 11:36
 * @version: 1.0
 * @description:
 */
@RestController
@RequestMapping(value = "pm/approvePush")
public class PmApprovePushApi extends BaseApi<PmApprovePush> {
    @Autowired
    private IPmApprovePushService pmApprovePushService;

    @Override
    public IBaseService<PmApprovePush> getService() {
        return pmApprovePushService;
    }
}
