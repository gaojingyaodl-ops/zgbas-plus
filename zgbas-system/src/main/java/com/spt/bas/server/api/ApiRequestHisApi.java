package com.spt.bas.server.api;

import com.spt.bas.client.constant.ApiAppEnum;
import com.spt.bas.client.entity.ApiRequestHis;
import com.spt.bas.server.service.IApiRequestHisService;
import com.spt.tools.data.service.BaseApi;
import com.spt.tools.data.service.IDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(value = "api/request")
public class ApiRequestHisApi extends BaseApi<ApiRequestHis> {
    @Autowired
    private IApiRequestHisService apiRequestHisService;

    @Override
    public IDataService<ApiRequestHis> getService() {
        return apiRequestHisService;
    }

    @PostMapping(value = "addZYRequestHis")
    public void addZYRequestHis(@RequestBody ApiRequestHis requestHis){
        requestHis.setAppCode(ApiAppEnum.ZY.getCode());
        requestHis.setAppName(ApiAppEnum.ZY.getName());
        apiRequestHisService.addApiRequestHis(requestHis);
    }

    @PostMapping(value = "addWFQRequestHis")
    public void addWFQRequestHis(@RequestBody ApiRequestHis requestHis){
        requestHis.setAppCode(ApiAppEnum.WFQ.getCode());
        requestHis.setAppName(ApiAppEnum.WFQ.getName());
        apiRequestHisService.addApiRequestHis(requestHis);
    }

    @PostMapping(value = "addCFCARequestHis")
    public void addCFCARequestHis(@RequestBody ApiRequestHis requestHis){
        requestHis.setAppCode(ApiAppEnum.CFCA.getCode());
        requestHis.setAppName(ApiAppEnum.CFCA.getName());
        apiRequestHisService.addApiRequestHis(requestHis);
    }

    @PostMapping(value = "addJINXINRequestHis")
    public void addJINXINRequestHis(@RequestBody ApiRequestHis requestHis){
        requestHis.setAppCode(ApiAppEnum.JINXIN.getCode());
        requestHis.setAppName(ApiAppEnum.JINXIN.getName());
        apiRequestHisService.addApiRequestHis(requestHis);
    }

    @PostMapping(value = "addRtRequestHis")
    public void addRtRequestHis(@RequestBody ApiRequestHis requestHis){
        requestHis.setAppCode(ApiAppEnum.RT.getCode());
        requestHis.setAppName(ApiAppEnum.RT.getName());
        apiRequestHisService.addApiRequestHis(requestHis);
    }
}

