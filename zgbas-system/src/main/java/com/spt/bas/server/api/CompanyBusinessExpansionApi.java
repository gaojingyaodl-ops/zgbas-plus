package com.spt.bas.server.api;

import com.spt.bas.client.entity.CompanyBusinessExpansion;
import com.spt.bas.server.service.ICompanyBusinessExpansionService;
import com.spt.tools.data.service.BaseApi;
import com.spt.tools.data.service.IDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/company/business/Expansion")
public class CompanyBusinessExpansionApi extends BaseApi<CompanyBusinessExpansion> {

    @Autowired
    private ICompanyBusinessExpansionService companyBusinessExpansionService;

    @Override
    public IDataService<CompanyBusinessExpansion> getService() {
        return companyBusinessExpansionService;
    }

   
}
