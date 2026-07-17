package com.spt.bas.server.api;


import com.spt.bas.client.entity.ApplyCompanyOnline;
import com.spt.bas.server.service.IApplyCompanyOnlineService;
import com.spt.tools.data.service.IDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.spt.tools.data.service.BaseApi;

@RestController
@RequestMapping(value = "apply/Companyonline")
public class ApplyCompanyApi extends BaseApi<ApplyCompanyOnline> {


    @Autowired
    private IApplyCompanyOnlineService applyCompanyOnlineService;

    @Override
    public IDataService<ApplyCompanyOnline> getService() {
        return applyCompanyOnlineService;
    }
}
