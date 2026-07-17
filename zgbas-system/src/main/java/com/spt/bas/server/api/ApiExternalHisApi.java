package com.spt.bas.server.api;


import com.spt.bas.client.entity.ApiExternalHis;
import com.spt.bas.server.service.IApiExternalHisService;
import com.spt.tools.data.service.BaseApi;
import com.spt.tools.data.service.IDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(value = "api/external")
public class ApiExternalHisApi extends BaseApi<ApiExternalHis> {
    @Autowired
    private IApiExternalHisService apiExternalHisService;

    @Override
    public IDataService<ApiExternalHis> getService() {
        return apiExternalHisService;
    }

    @PostMapping(value = "addExternalHis")
    public void addExternalHis(@RequestBody ApiExternalHis externalHis){
        apiExternalHisService.addApiExternalHis(externalHis);
    }
}

