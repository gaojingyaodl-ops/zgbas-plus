package com.spt.bas.server.api;

import com.spt.bas.client.entity.ApplyAgreementVirtual;
import com.spt.bas.client.vo.FileIdUpdateVo;
import com.spt.bas.server.service.IApplyAgreementVirtualService;
import com.spt.tools.data.service.BaseApi;
import com.spt.tools.jpa.service.IBaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(value = "apply/agreementVirtual")
public class ApplyAgreementVirtualApi extends BaseApi<ApplyAgreementVirtual> {
    @Autowired
    private IApplyAgreementVirtualService applyAgreementVirtualService;

    @Override
    public IBaseService<ApplyAgreementVirtual> getService() {
        return applyAgreementVirtualService;
    }

    @PostMapping("updateFileId")
    public void updateFileId(@RequestBody FileIdUpdateVo vo){
        applyAgreementVirtualService.updateFileId(vo.getId(), vo.getFileId());
    }
}

