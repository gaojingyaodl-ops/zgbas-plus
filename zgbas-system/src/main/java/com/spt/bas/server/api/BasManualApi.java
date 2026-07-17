package com.spt.bas.server.api;

import com.spt.bas.client.entity.BasManual;
import com.spt.bas.server.service.IBasManualService;
import com.spt.tools.data.service.BaseApi;
import com.spt.tools.data.service.IDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "bas/manual")
public class BasManualApi extends BaseApi<BasManual> {
    @Autowired
    private IBasManualService basManualService;
    @Override
    public IDataService<BasManual> getService() {
        return basManualService;
    }

    @PostMapping("findAllEnable")
    public List<BasManual> findAllEnable() {
        return basManualService.findAllEnable();
    }
}
