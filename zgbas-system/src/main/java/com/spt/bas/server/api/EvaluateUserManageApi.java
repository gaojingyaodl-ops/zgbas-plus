package com.spt.bas.server.api;

import com.spt.bas.client.entity.EvaluateUserManage;
import com.spt.bas.server.service.IEvaluateUserManageService;
import com.spt.tools.data.service.BaseApi;
import com.spt.tools.data.service.IDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "evaluate/user/manage")
public class EvaluateUserManageApi extends BaseApi<EvaluateUserManage> {
    @Autowired
    private IEvaluateUserManageService evaluateUserManageService;
    @Override
    public IDataService<EvaluateUserManage> getService() {
        return evaluateUserManageService;
    }

}
