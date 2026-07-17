package com.spt.bas.server.api;

import com.spt.bas.client.entity.ApplyCancelDetail;
import com.spt.bas.client.entity.ApplyInvoiceReceived;
import com.spt.bas.client.vo.FileIdUpdateVo;
import com.spt.bas.server.service.IApplyInvoiceReceivedService;
import com.spt.pm.entity.PmApprove;
import com.spt.pm.service.IPmApproveService;
import com.spt.tools.core.bean.PageSearchVo;
import com.spt.tools.data.service.BaseApi;
import com.spt.tools.jpa.service.IBaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;


@RestController
@RequestMapping(value = "apply/invoiceReceivedDcsx")
public class ApplyInvoiceReceivedDcsxApi extends BaseApi<ApplyInvoiceReceived> {
    @Autowired
    private IApplyInvoiceReceivedService applyInvoiceReceivedService;
    @Autowired
    private IPmApproveService pmApproveService;

    @Override
    public IBaseService<ApplyInvoiceReceived> getService() {
        return applyInvoiceReceivedService;
    }

    @PostMapping("updateFileId")
    public void updateFileId(@RequestBody FileIdUpdateVo vo) {
        applyInvoiceReceivedService.updateFileId(vo.getId(), vo.getFileId());
    }

    @PostMapping("findPageDetail")
    public Page<ApplyCancelDetail> findPageDetail(@RequestBody PageSearchVo searchVo) {
        Page<ApplyInvoiceReceived> page = applyInvoiceReceivedService.findPage(searchVo);
        List<ApplyCancelDetail> list = new ArrayList<ApplyCancelDetail>();
        for (ApplyInvoiceReceived out : page.getContent()) {
            PmApprove approve = pmApproveService.getEntity(out.getApproveId());
            ApplyCancelDetail detail = new ApplyCancelDetail();
            detail.setOldApproveId(out.getApproveId());
            if (approve != null) {
                detail.setOldApproveNo(approve.getApproveNo());
            }
            detail.setOldApproveDate(out.getCreatedDate());
            detail.setCancelAmount(out.getInvoiceAmount());

            list.add(detail);
        }
        PageRequest pageRequest = PageRequest.of(searchVo.getPage() - 1, searchVo.getRows());
        Page<ApplyCancelDetail> pageVo = new PageImpl<>(list, pageRequest, page.getTotalElements());
        return pageVo;
    }
}

