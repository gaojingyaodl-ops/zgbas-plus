package com.spt.bas.server.api;

import com.spt.bas.client.entity.ApplyCancelDetail;
import com.spt.bas.client.entity.ApplyPay;
import com.spt.bas.client.vo.FileIdUpdateVo;
import com.spt.bas.server.service.IApplyPayService;
import com.spt.bas.server.service.IApplyProductDetailService;
import com.spt.pm.entity.PmApprove;
import com.spt.pm.service.IPmApproveService;
import com.spt.pm.vo.PmApproveSaveVo;
import com.spt.tools.core.bean.PageSearchVo;
import com.spt.tools.core.exception.ApplicationException;
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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


@RestController
@RequestMapping(value = "apply/pay")
public class ApplyPayApi extends BaseApi<ApplyPay> {
    @Autowired
    private IApplyPayService applyPayService;
    @Autowired
    private IApplyProductDetailService applyProductDetailService;
    @Autowired
    private IPmApproveService pmApproveService;

    @Override
    public IBaseService<ApplyPay> getService() {
        return applyPayService;
    }

    @PostMapping("updateFileId")
    public void updateFileId(@RequestBody FileIdUpdateVo vo) {
        applyPayService.updateFileId(vo.getId(), vo.getFileId());
    }

    @PostMapping("findPageSum")
    public ApplyPay findPageSum(@RequestBody PageSearchVo searchVo) {
        return applyPayService.findPageSum(searchVo);
    }

    @PostMapping("findPageDetail")
    public Page<ApplyCancelDetail> findPageDetail(@RequestBody PageSearchVo searchVo) {
        Page<ApplyPay> page = applyPayService.findPage(searchVo);
        List<ApplyCancelDetail> list = new ArrayList<ApplyCancelDetail>();
        for (ApplyPay out : page.getContent()) {
            PmApprove approve = pmApproveService.getEntity(out.getApproveId());
            ApplyCancelDetail detail = new ApplyCancelDetail();
            detail.setOldApproveId(out.getApproveId());
            if (approve != null) {
                detail.setOldApproveNo(approve.getApproveNo());
            }
            detail.setOldApproveDate(out.getCreatedDate());
            detail.setCancelAmount(out.getPayAmount());
            list.add(detail);
        }
        PageRequest pageRequest = PageRequest.of(searchVo.getPage() - 1, searchVo.getRows());
        Page<ApplyCancelDetail> pageVo = new PageImpl<>(list, pageRequest, page.getTotalElements());
        return pageVo;
    }

    @PostMapping("startBatchPayApply")
    public PmApprove startBatchPayApply(@RequestBody PmApproveSaveVo saveVo)  throws ApplicationException {
        return applyPayService.startBatchPayApply(saveVo);
    }

    @PostMapping("getBrushBrushAmount")
    public ApplyPay getBrushBrushAmount(@RequestBody ApplyPay applyPay){
        return applyPayService.getBrushBrushAmount(applyPay);
    }
}

