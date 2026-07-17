package com.spt.bas.server.api;

import com.spt.bas.client.entity.CtrContractChainText;
import com.spt.bas.client.entity.CtrContractText;
import com.spt.bas.client.vo.MatchContractTextVo;
import com.spt.bas.server.service.ICtrContractChainTextService;
import com.spt.bas.server.service.ICtrContractTextService;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.data.service.BaseApi;
import com.spt.tools.jpa.service.IBaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(value = "ctr/contractChainText")
public class CtrContractChainTextApi extends BaseApi<CtrContractChainText> {
    @Autowired
    private ICtrContractChainTextService ctrContractChainTextService;

    @Override
    public IBaseService<CtrContractChainText> getService() {
        return ctrContractChainTextService;
    }

    @PostMapping("findContractText")
    public CtrContractChainText findContractText(@RequestBody CtrContractChainText text){
        return ctrContractChainTextService.findByContractIdAndContractType(text.getCtrContractId(),text.getContractType());
    }

}

