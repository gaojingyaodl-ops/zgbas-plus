package com.spt.bas.server.api;

import com.spt.bas.client.entity.BsCompanyQuotaV1;
import com.spt.bas.server.service.IBsCompanyQuotaV1Service;
import com.spt.tools.data.service.BaseApi;
import com.spt.tools.data.service.IDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "bs/companyQuotaV1")
public class BsCompanyQuotaV1Api extends BaseApi<BsCompanyQuotaV1> {
    @Autowired
    private IBsCompanyQuotaV1Service bsCompanyQuotaService;
    @Override
    public IDataService<BsCompanyQuotaV1> getService() {
        return bsCompanyQuotaService;
    }

}
