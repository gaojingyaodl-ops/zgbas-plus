package com.spt.bas.server.api.fund;

import com.spt.bas.client.entity.FundAmountFlow;
import com.spt.bas.server.service.IFundAmountFlowService;
import com.spt.tools.data.service.BaseApi;
import com.spt.tools.jpa.service.IBaseService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @Author MoonLight
 * @Date 2024/7/15 9:36
 * @Version 1.0
 */
@RestController
@RequestMapping(value = "fund/amountFlow")
public class FundAmountFlowApi extends BaseApi<FundAmountFlow> {

    @Resource
    private IFundAmountFlowService fundAmountFlowService;

    @Override
    public IBaseService<FundAmountFlow> getService() {
        return fundAmountFlowService;
    }
}
