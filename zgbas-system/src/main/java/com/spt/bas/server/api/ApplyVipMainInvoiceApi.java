package com.spt.bas.server.api;


import com.spt.bas.client.entity.ApplyVipMainInvoice;

import com.spt.bas.server.service.IApplyVipMainInvoiceService;
import com.spt.tools.data.service.BaseApi;
import com.spt.tools.data.service.IDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(value = "apply/vipMainInvoice")
public class ApplyVipMainInvoiceApi extends BaseApi<ApplyVipMainInvoice> {


    @Autowired
    private IApplyVipMainInvoiceService applyVipMainInvoiceService;

    @Override
    public IDataService<ApplyVipMainInvoice> getService() {
        return applyVipMainInvoiceService;
    }
}

