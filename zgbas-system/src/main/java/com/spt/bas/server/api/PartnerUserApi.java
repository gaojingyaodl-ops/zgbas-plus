package com.spt.bas.server.api;



import com.spt.bas.client.entity.PartnerUser;
import com.spt.bas.server.service.IPartnerUserService;
import com.spt.tools.data.service.BaseApi;
import com.spt.tools.data.service.IDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "bs/PartnerUser")
public class PartnerUserApi extends BaseApi<PartnerUser> {
    @Autowired
    private IPartnerUserService partnerUserService;


    @Override
    public IDataService<PartnerUser> getService() {
        return partnerUserService;
    }

    @PostMapping(value = "getByCompanyId")
    public List<PartnerUser> getByCompanyId(@RequestBody Long companyId){
        return partnerUserService.getByCompanyId(companyId);
    }
    @PostMapping(value = "getByUserId")
    PartnerUser getByUserId(@RequestBody Long userId){
        return partnerUserService.getByUserId(userId);
    }
}
