package com.spt.bas.server.api;


import com.spt.bas.client.entity.ApplyVip;
import com.spt.bas.client.entity.ApplyVipReceive;
import com.spt.bas.server.service.IApplyVipReceiveService;
import com.spt.bas.server.service.IApplyVipService;
import com.spt.tools.data.service.BaseApi;
import com.spt.tools.data.service.IDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping(value = "apply/vipReceive")
public class ApplyVipReceiveApi extends BaseApi<ApplyVipReceive> {



    @Autowired
    private IApplyVipReceiveService applyVipReceiveService;

    @Override
    public IDataService<ApplyVipReceive> getService() {
        return applyVipReceiveService;
    }
}
