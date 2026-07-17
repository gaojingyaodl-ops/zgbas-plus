package com.spt.bas.server.api;

import com.spt.bas.client.entity.LitigationCase;
import com.spt.bas.client.entity.PiccPayApply;
import com.spt.bas.server.service.ILitigationCaseService;
import com.spt.tools.data.service.BaseApi;
import com.spt.tools.data.service.IDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/ctr/litigationCase")
public class LitigationCaseApi  extends BaseApi<LitigationCase> {
    @Autowired
    private ILitigationCaseService litigationCaseService;
    @Override
    public IDataService<LitigationCase> getService() {
        return litigationCaseService;
    }
}
