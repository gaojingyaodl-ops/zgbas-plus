
package com.spt.bas.server.api;

import com.spt.bas.client.entity.BsCompanyFeeConfig;
import com.spt.bas.server.service.BsCompanyFeeConfigService;
import com.spt.tools.data.service.BaseApi;
import com.spt.tools.data.service.IDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "bsCompanyFee/config")
public class BsCompanyFeeConfigApi extends BaseApi<BsCompanyFeeConfig> {
    @Autowired
    private BsCompanyFeeConfigService bsCompanyFeeConfigService;

    @Override
    public IDataService<BsCompanyFeeConfig> getService() {
        return bsCompanyFeeConfigService;
    }
}