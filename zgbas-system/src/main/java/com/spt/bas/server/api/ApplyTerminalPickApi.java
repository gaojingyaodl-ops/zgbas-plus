package com.spt.bas.server.api;


import com.spt.bas.client.entity.ApplyTerminalPick;
import com.spt.bas.server.service.IApplyTerminalPickService;
import com.spt.tools.data.service.BaseApi;
import com.spt.tools.data.service.IDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "apply/applyTerminalPick")
public class ApplyTerminalPickApi extends BaseApi<ApplyTerminalPick> {

    @Autowired
    private IApplyTerminalPickService applyTerminalPickService;



    @Override
    public IDataService<ApplyTerminalPick> getService() {
        return applyTerminalPickService;
    }
}
