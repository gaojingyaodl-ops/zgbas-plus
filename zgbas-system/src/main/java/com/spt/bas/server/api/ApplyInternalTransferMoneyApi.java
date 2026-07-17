package com.spt.bas.server.api;


import com.spt.bas.client.entity.ApplyInternalTransferMoney;
import com.spt.bas.server.service.IApplyInternalTransferMoneyService;
import com.spt.tools.data.service.BaseApi;
import com.spt.tools.data.service.IDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "apply/internalTransferMoney")
public class ApplyInternalTransferMoneyApi extends BaseApi<ApplyInternalTransferMoney> {

    @Autowired
    private IApplyInternalTransferMoneyService applyInternalTransferMoneyService;


    @Override
    public IDataService<ApplyInternalTransferMoney> getService() {
        return applyInternalTransferMoneyService;
    }

}
