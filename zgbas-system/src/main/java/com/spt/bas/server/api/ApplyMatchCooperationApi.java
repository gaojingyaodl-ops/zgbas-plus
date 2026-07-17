package com.spt.bas.server.api;

import com.spt.bas.client.entity.ApplyMatch;
import com.spt.bas.server.service.IApplyMatchCooperationService;
import com.spt.tools.data.service.BaseApi;
import com.spt.tools.jpa.service.IBaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(value = "apply/matchCooperation")
public class ApplyMatchCooperationApi extends BaseApi<ApplyMatch> {
    @Autowired
    private IApplyMatchCooperationService applyMatchCooperationService;

    @Override
    public IBaseService<ApplyMatch> getService() {
        return applyMatchCooperationService;
    }
}

