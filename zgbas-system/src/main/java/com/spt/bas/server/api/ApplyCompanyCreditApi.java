package com.spt.bas.server.api;

import com.spt.bas.client.entity.ApplyCompanyCredit;
import com.spt.bas.client.entity.ApplyCompanyOnline;
import com.spt.bas.server.service.IApplyCompanyCreditService;
import com.spt.tools.data.service.BaseApi;
import com.spt.tools.data.service.IDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/apply/companyCredit")
public class ApplyCompanyCreditApi extends BaseApi<ApplyCompanyCredit> {
    @Autowired
    private IApplyCompanyCreditService applyCompanyCreditService;
    @Override
    public IDataService<ApplyCompanyCredit> getService() {
        return applyCompanyCreditService;
    }
    @RequestMapping("/findByCompanyIdAndType")
    public ApplyCompanyCredit findByCompanyIdAndType(@RequestParam("type") String type, @RequestParam("companyId") Long companyId, @RequestParam("creditType") String creditType){
        return applyCompanyCreditService.findByCompanyIdAndType(type,companyId,creditType);
    }
}
