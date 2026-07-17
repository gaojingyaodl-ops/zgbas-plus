package com.spt.bas.server.api;

import com.spt.bas.client.entity.BsCompanyConfig;
import com.spt.bas.server.service.IBsCompanyConfigService;
import com.spt.tools.data.service.BaseApi;
import com.spt.tools.jpa.service.IBaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: gaojy
 * @create 2022/4/2 11:05
 * @version: 1.0
 * @description:
 */
@RestController
@RequestMapping(value = "bs/companyConfig")
public class BsCompanyConfigApi extends BaseApi<BsCompanyConfig> {
    @Autowired
    private IBsCompanyConfigService bsCompanyConfigService;

    @Override
    public IBaseService<BsCompanyConfig> getService() {
        return bsCompanyConfigService;
    }

    @PostMapping(value = "findByBsCompanyIdAndMatchUserId")
    public BsCompanyConfig findByBsCompanyIdAndMatchUserId(@RequestBody BsCompanyConfig bsCompanyConfig){
        return bsCompanyConfigService.findByBsCompanyIdAndMatchUserId(bsCompanyConfig.getBsCompanyId(), bsCompanyConfig.getMatchUserId());
    }
}
