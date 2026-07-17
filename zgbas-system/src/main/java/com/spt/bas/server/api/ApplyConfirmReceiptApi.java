package com.spt.bas.server.api;

import com.spt.bas.client.entity.ApplyConfirmReceipt;
import com.spt.bas.client.vo.ApplyConfirmReceiptVo;
import com.spt.bas.client.vo.FileIdUpdateVo;
import com.spt.bas.server.service.IApplyConfrimReceiptService;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.data.service.BaseApi;
import com.spt.tools.data.service.IDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 *
 * </p>
 *
 * @Author: shengong
 * @Date: Created in 2020-11-11 14:59
 */
@RestController
@RequestMapping(value = "apply/confirmReceipt")
public class ApplyConfirmReceiptApi extends BaseApi<ApplyConfirmReceipt> {
    @Autowired
    private IApplyConfrimReceiptService applyConfrimReceiptService;

    @Override
    public IDataService<ApplyConfirmReceipt> getService() {
        return applyConfrimReceiptService;
    }

    @PostMapping("applyConfirmReceipt")
    public void applyConfirmReceipt(@RequestBody ApplyConfirmReceiptVo confirmReceiptVo)throws ApplicationException {
        applyConfrimReceiptService.applyConfirmReceipt(confirmReceiptVo);
    }
    @PostMapping("findByContractId")
    public   List<ApplyConfirmReceipt> findByContractId(@RequestBody Long contractId){
       return applyConfrimReceiptService.findByContractId(contractId);
    }

    @PostMapping("updateFileId")
    public void updateFileId(@RequestBody FileIdUpdateVo vo){
        applyConfrimReceiptService.updateFileId(vo.getId(), vo.getFileId());
    }

}
