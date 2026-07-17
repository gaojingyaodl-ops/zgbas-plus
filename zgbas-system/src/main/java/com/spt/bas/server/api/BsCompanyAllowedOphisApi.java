package com.spt.bas.server.api;

import com.spt.bas.client.entity.BsCompanyAllowedOphis;
import com.spt.bas.server.service.IBsCompanyAllowedOphisService;
import com.spt.tools.data.service.BaseApi;
import com.spt.tools.data.service.IDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "bs/companyAllowedOphis")
public class BsCompanyAllowedOphisApi extends BaseApi<BsCompanyAllowedOphis> {
    @Autowired
    private IBsCompanyAllowedOphisService bsCompanyAllowedOphisService;

    @Override
    public IDataService<BsCompanyAllowedOphis> getService() {
        return bsCompanyAllowedOphisService;
    }
}
