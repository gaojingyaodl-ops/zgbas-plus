package com.spt.bas.server.api;

import com.spt.bas.client.entity.ApplyInterestPay;
import com.spt.bas.client.vo.FileIdUpdateVo;
import com.spt.bas.client.vo.api.RespVo;
import com.spt.bas.server.service.IApplyInterestPayService;
import com.spt.tools.data.service.BaseApi;
import com.spt.tools.jpa.service.IBaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(value = "apply/interestPay")
public class ApplyInterestPayApi extends BaseApi<ApplyInterestPay> {
    @Autowired
    private IApplyInterestPayService applyInterestPayService;

    @Override
    public IBaseService<ApplyInterestPay> getService() {
        return applyInterestPayService;
    }

    @PostMapping("updateFileId")
    public void updateFileId(@RequestBody FileIdUpdateVo vo) {
        applyInterestPayService.updateFileId(vo.getId(), vo.getFileId());
    }

    @PostMapping("batchPayInterest")
    public RespVo<String> batchPayInterest(@RequestBody ApplyInterestPay entity){
        return applyInterestPayService.batchPayInterest(entity);
    }
}

