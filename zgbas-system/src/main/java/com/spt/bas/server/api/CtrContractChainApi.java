package com.spt.bas.server.api;

import com.spt.bas.client.entity.CtrContractChain;
import com.spt.bas.server.service.ICtrContractChainService;
import com.spt.tools.data.service.BaseApi;
import com.spt.tools.data.service.IDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping(value = "ctr/contractChain")
public class CtrContractChainApi extends BaseApi<CtrContractChain> {

    @Autowired
    private ICtrContractChainService ctrContractChainService;

    @Override
    public IDataService<CtrContractChain> getService() {
        return ctrContractChainService;
    }

    @PostMapping("findByApproveId")
    public CtrContractChain findByContractNo(@RequestBody  String contractNo) {
        return  ctrContractChainService.findByContractNo(contractNo);
    }
}

