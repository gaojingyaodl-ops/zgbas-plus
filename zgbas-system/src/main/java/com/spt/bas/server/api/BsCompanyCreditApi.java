package com.spt.bas.server.api;

import com.spt.bas.client.entity.BsCompanyContacts;
import com.spt.bas.client.entity.BsCompanyCredit;
import com.spt.bas.server.service.IBsCompanyCreditService;
import com.spt.tools.data.service.BaseApi;
import com.spt.tools.data.service.IDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "bs/companyCredit")
public class BsCompanyCreditApi extends BaseApi<BsCompanyCredit> {
    @Autowired
    private IBsCompanyCreditService iBsCompanyCreditService;

    @Override
    public IDataService<BsCompanyCredit> getService() {
        return iBsCompanyCreditService;
    }
    @RequestMapping("/findByCompanyIdAndType")
    public BsCompanyCredit findByCompanyIdAndType(@RequestParam("companyId") Long companyId, @RequestParam("creditType") String creditType){
        return iBsCompanyCreditService.findByCompanyIdAndType(companyId,creditType);
    }
    
    @RequestMapping("/findByCompanyId")
    public List<BsCompanyCredit> findByCompanyId(@RequestParam("companyId") Long companyId){
        return iBsCompanyCreditService.findByCompanyId(companyId);
    }
}
