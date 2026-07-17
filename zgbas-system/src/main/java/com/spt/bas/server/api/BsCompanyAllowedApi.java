package com.spt.bas.server.api;

import com.spt.bas.client.entity.BsCompanyAllowed;
import com.spt.bas.client.entity.SealUsage;
import com.spt.bas.server.service.IBsCompanyAllowedService;
import com.spt.tools.data.service.BaseApi;
import com.spt.tools.data.service.IDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "bs/companyAllowed")
public class BsCompanyAllowedApi extends BaseApi<BsCompanyAllowed> {
    @Autowired
    private IBsCompanyAllowedService bsCompanyAllowedService;
    @Override
    public IDataService<BsCompanyAllowed> getService() {
        return bsCompanyAllowedService;
    }
}
