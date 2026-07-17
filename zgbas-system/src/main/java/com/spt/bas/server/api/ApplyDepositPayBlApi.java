package com.spt.bas.server.api;

import com.spt.bas.client.entity.ApplyDeliveryOut;
import com.spt.bas.client.entity.ApplyPay;
import com.spt.bas.server.service.IApplyDepositPayBlService;
import com.spt.tools.data.service.BaseApi;
import com.spt.tools.data.service.IDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "apply/depositPayBl")
public class ApplyDepositPayBlApi extends BaseApi<ApplyPay> {
    @Autowired
    private IApplyDepositPayBlService applyDepositPayBlService;
    @Override
    public IDataService<ApplyPay> getService() {
        return applyDepositPayBlService;
    }
}
