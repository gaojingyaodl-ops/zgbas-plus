package com.spt.bas.server.api;

import com.spt.bas.client.entity.PiccPayApply;
import com.spt.bas.server.service.IPiccPayApplyService;
import com.spt.tools.data.service.BaseApi;
import com.spt.tools.jpa.service.IBaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 人保还款申请保存记录
 */
@RestController
@RequestMapping(value = "picc/pay")
public class PiccPayApplyApi extends BaseApi<PiccPayApply> {
    
    @Autowired
    private IPiccPayApplyService piccPayApplyService;
    
    @Override
    public IBaseService<PiccPayApply> getService() {
        return piccPayApplyService;
    }
}
