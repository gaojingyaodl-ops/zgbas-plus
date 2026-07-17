package com.spt.bas.server.api;

import com.spt.bas.client.entity.CtrOutInLedger;
import com.spt.bas.server.service.ICtrLedgerService;
import com.spt.tools.data.service.BaseApi;
import com.spt.tools.data.service.IDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "ctr/ledger")
public class CtrLedgerApi extends BaseApi<CtrOutInLedger> {

    @Autowired
    private ICtrLedgerService ctrLedgerService;
    @Override
    public IDataService<CtrOutInLedger> getService() {
        return ctrLedgerService;
    }
}
