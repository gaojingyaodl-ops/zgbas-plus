package com.spt.bas.server.api;


import com.spt.bas.client.entity.BasBrand;
import com.spt.bas.server.service.IApplyBrandService;
import com.spt.tools.data.service.BaseApi;
import com.spt.tools.data.service.IDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "apply/brand")
public class ApplyBrandApi extends BaseApi<BasBrand> {

    @Autowired
    private IApplyBrandService applyBrandService;

    @Override
    public IDataService<BasBrand> getService() {
        return applyBrandService;
    }
}
