package com.spt.bas.server.api;

import com.spt.bas.client.entity.FileType;
import com.spt.bas.client.entity.InsuranceAmountFlow;
import com.spt.bas.server.service.IInsuranceAmountFlowService;
import com.spt.bas.server.service.impl.InsuranceAmountFlowServiceImpl;
import com.spt.tools.data.service.BaseApi;
import com.spt.tools.data.service.IDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "bs/companyDcsx/insuranceAmountFlow")
public class InsuranceAmountFlowApi extends BaseApi<InsuranceAmountFlow> {
    @Autowired
    private IInsuranceAmountFlowService insuranceAmountFlowService;
    @Override
    public IDataService<InsuranceAmountFlow> getService() {
        return insuranceAmountFlowService;
    }
}
