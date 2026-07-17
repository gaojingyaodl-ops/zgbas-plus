package com.spt.bas.server.api;

import com.spt.bas.client.entity.ApplyBuy;
import com.spt.bas.client.vo.ApplyBuyVo;
import com.spt.bas.client.vo.ApproveFormPrintVo;
import com.spt.bas.client.vo.FileIdUpdateVo;
import com.spt.bas.server.service.IApplyBuyService;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.data.service.BaseApi;
import com.spt.tools.jpa.service.IBaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(value = "apply/buy")
public class ApplyBuyApi extends BaseApi<ApplyBuy> {
    @Autowired
    private IApplyBuyService applyBuyService;

    @Override
    public IBaseService<ApplyBuy> getService() {
        return applyBuyService;
    }

    @PostMapping("updateFileId")
    public void updateFileId(@RequestBody FileIdUpdateVo vo) {
        applyBuyService.updateFileId(vo.getId(), vo.getFileId());
    }

    @PostMapping("findByContractId")
    public ApplyBuy findByContractId(@RequestBody Long contractId) {
        ApplyBuy findByContractId = applyBuyService.findByContractId(contractId);
        return findByContractId;
    }

    @PostMapping("printApplyBuy")
    public ApproveFormPrintVo printApplyBuy(@RequestBody Long applyId) {
        return applyBuyService.printApplyBuy(applyId);
    }


    @PostMapping(value = "applyBuy")
    public void applyBuyApply(@RequestBody  ApplyBuyVo applyBuyVo)throws ApplicationException {
        applyBuyService.applyBuy(applyBuyVo);
    }


}

