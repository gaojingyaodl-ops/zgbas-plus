package com.spt.bas.server.api;

import com.spt.bas.client.entity.ApplyBusinessRestrictRelieve;
import com.spt.bas.client.entity.BusinessRestrictRelieve;
import com.spt.bas.client.vo.BusinessRestrictRelieveVo;
import com.spt.bas.client.vo.FileIdUpdateVo;
import com.spt.bas.server.service.IApplyBusinessRestrictRelieveService;
import com.spt.bas.server.service.IBusinessRestrictRelieveService;
import com.spt.tools.data.service.BaseApi;
import com.spt.tools.data.service.IDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/business/restrict/relieve")
public class BusinessRestrictRelieveApi extends BaseApi<BusinessRestrictRelieve> {

    @Autowired
    private IBusinessRestrictRelieveService businessRestrictRelieveService;

    @Override
    public IDataService<BusinessRestrictRelieve> getService() {
        return businessRestrictRelieveService;
    }

    @PostMapping("updateUsableCount")
    public void updateUsableCount(@RequestBody BusinessRestrictRelieveVo vo) {
        businessRestrictRelieveService.updateUsableCount(vo.getCompanyId(), vo.getUsableCount());
    }
}
