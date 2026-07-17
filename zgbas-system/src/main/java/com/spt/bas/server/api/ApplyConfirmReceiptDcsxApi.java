package com.spt.bas.server.api;

import com.spt.bas.client.entity.ApplyConfirmReceiptDcsx;
import com.spt.bas.client.vo.ApplyConfirmReceiptDcsxVo;
import com.spt.bas.client.vo.FileIdUpdateVo;
import com.spt.bas.server.service.IApplyConfrimReceiptDcsxService;
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
@RequestMapping(value = "apply/confirmReceiptDcsx")
public class ApplyConfirmReceiptDcsxApi extends BaseApi<ApplyConfirmReceiptDcsx> {
    @Autowired
    private IApplyConfrimReceiptDcsxService applyConfrimReceiptDcsxService;

    @Override
    public IDataService<ApplyConfirmReceiptDcsx> getService() {
        return applyConfrimReceiptDcsxService;
    }

    @PostMapping("applyConfirmReceiptDcsx")
    public void applyConfirmReceiptDcsx(@RequestBody ApplyConfirmReceiptDcsxVo confirmReceiptDcsxVo)throws ApplicationException {
        applyConfrimReceiptDcsxService.applyConfirmReceiptDcsx(confirmReceiptDcsxVo);
    }
    @PostMapping("findByContractId")
    public   List<ApplyConfirmReceiptDcsx> findByContractId(@RequestBody Long contractId){
       return applyConfrimReceiptDcsxService.findByContractId(contractId);
    }

    @PostMapping("updateFileId")
    public void updateFileId(@RequestBody FileIdUpdateVo vo){
        applyConfrimReceiptDcsxService.updateFileId(vo.getId(), vo.getFileId());
    }
}
