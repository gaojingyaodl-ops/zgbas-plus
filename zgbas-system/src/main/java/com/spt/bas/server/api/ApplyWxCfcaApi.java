package com.spt.bas.server.api;

import com.spt.bas.client.entity.ApplyWxCfca;
import com.spt.bas.server.service.IApplyWxCfcaService;
import com.spt.tools.data.service.BaseApi;
import com.spt.tools.data.service.IDataService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;


@RestController
@RequestMapping(value = "/apply/applyWxCfca")
public class ApplyWxCfcaApi extends BaseApi<ApplyWxCfca> {

    @Resource
    private IApplyWxCfcaService applyWxCfcaService;

    @Override
    public IDataService<ApplyWxCfca> getService() {
        return applyWxCfcaService;
    }
}
