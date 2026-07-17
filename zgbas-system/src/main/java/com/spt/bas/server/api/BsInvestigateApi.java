package com.spt.bas.server.api;

import com.spt.bas.client.entity.BsInvestigate;
import com.spt.bas.server.service.IBsInvestigateService;
import com.spt.tools.data.service.BaseApi;
import com.spt.tools.data.service.IDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * <p>
 *  企业实地调研信息
 * </p>
 *
 * @Author: shengong
 * @Date: Created in 2021-01-12 17:16
 */
@RestController
@RequestMapping(value = "bs/investigateInfo")
public class BsInvestigateApi extends BaseApi<BsInvestigate> {

    @Autowired
    private IBsInvestigateService bsInvestigateService;

    @Override
    public IDataService<BsInvestigate> getService() {
        return bsInvestigateService;
    }

    @PostMapping("findByCompanyId")
    BsInvestigate findByCompanyId(@RequestBody Long companyId) {
        return bsInvestigateService.findByCompanyId(companyId);
    }
}
