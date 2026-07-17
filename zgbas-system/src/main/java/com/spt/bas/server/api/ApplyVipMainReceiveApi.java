package com.spt.bas.server.api;


import com.spt.bas.client.entity.ApplyVipMainReceive;
import com.spt.bas.client.entity.ApplyVipReceive;
import com.spt.bas.server.service.IApplyVipMainReceiveService;
import com.spt.bas.server.service.IApplyVipReceiveService;
import com.spt.tools.data.service.BaseApi;
import com.spt.tools.data.service.IDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "apply/vipMainReceive")
public class ApplyVipMainReceiveApi extends BaseApi<ApplyVipMainReceive> {



    @Autowired
    private IApplyVipMainReceiveService applyVipMainReceiveService;

    @Override
    public IDataService<ApplyVipMainReceive> getService() {
        return applyVipMainReceiveService;
    }
}
