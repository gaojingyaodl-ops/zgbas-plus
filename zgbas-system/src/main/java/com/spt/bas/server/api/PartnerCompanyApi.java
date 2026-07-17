package com.spt.bas.server.api;


import com.spt.bas.client.entity.PartnerCompany;
import com.spt.bas.server.service.IPartnerCompanyService;
import com.spt.tools.data.service.BaseApi;
import com.spt.tools.data.service.IDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "bs/PartnerCompany")
public class PartnerCompanyApi extends BaseApi<PartnerCompany> {
    @Autowired
    private IPartnerCompanyService partnerCompanyService;


    @Override
    public IDataService<PartnerCompany> getService() {
        return partnerCompanyService;
    }
}
