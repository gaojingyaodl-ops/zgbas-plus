package com.spt.bas.server.api;

import com.spt.bas.client.entity.ApplyFactorSign;
import com.spt.bas.server.service.IApplyFactorSignService;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.data.service.BaseApi;
import com.spt.tools.jpa.service.IBaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 签署保理申请
 */
@RestController
@RequestMapping(value = "apply/factorSign")
public class ApplyFactorSignApi extends BaseApi<ApplyFactorSign> {
    @Autowired
    private IApplyFactorSignService applyFactorSignService;

    @Override
    public IBaseService<ApplyFactorSign> getService() {
        return applyFactorSignService;
    }

    @PostMapping(value = "applyFactorSign")
    public void applyFactorSign(@RequestBody ApplyFactorSign factorSign) throws ApplicationException{
        applyFactorSignService.applyFactorSign(factorSign);
    }
}
