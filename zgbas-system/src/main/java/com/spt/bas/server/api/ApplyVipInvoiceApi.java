package com.spt.bas.server.api;

import com.spt.bas.client.entity.ApplyVipInvoice;
import com.spt.bas.server.service.IApplyVipInvoiceService;
import com.spt.tools.data.service.BaseApi;
import com.spt.tools.data.service.IDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;



@RestController
@RequestMapping(value = "apply/vipInvoice")
public class ApplyVipInvoiceApi extends BaseApi<ApplyVipInvoice> {


    @Autowired
    private IApplyVipInvoiceService applyVipInvoiceService;

    @Override
    public IDataService<ApplyVipInvoice> getService() {
        return applyVipInvoiceService;
    }
}

