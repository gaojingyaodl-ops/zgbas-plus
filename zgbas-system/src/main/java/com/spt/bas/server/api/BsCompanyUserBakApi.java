package com.spt.bas.server.api;



import com.spt.bas.client.entity.BsCompany;
import com.spt.bas.client.entity.BsCompanyUserBak;

import com.spt.bas.server.service.IBsCompanyUserBakService;
import com.spt.tools.data.service.BaseApi;
import com.spt.tools.data.service.IDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "bs/companyUserBak")
public class BsCompanyUserBakApi extends BaseApi<BsCompanyUserBak> {
    @Autowired
    private IBsCompanyUserBakService bsCompanyUserBakService;

    @PostMapping("/getBsCompanyUser")
    public BsCompanyUserBak getBsCompanyUserBak(@RequestParam("companyId") Long companyId, @RequestParam("matchUserId") Long matchUserId){
        return bsCompanyUserBakService.findByMatchUserIdAndFollowDate(companyId,matchUserId);
    }

    @PostMapping("/getCompanyUserForDate")
    List<BsCompany> getCompanyForDate(@RequestBody Long matchUserId){
        return bsCompanyUserBakService.getCompanyForDate(matchUserId);
    }

    @PostMapping("/findByCompanyId")
    BsCompanyUserBak findByCompanyId(@RequestParam("companyId") Long companyId){
        return bsCompanyUserBakService.findByCompanyId(companyId);
    }

    @Override
    public IDataService<BsCompanyUserBak> getService() {
        return bsCompanyUserBakService;
    }
}
