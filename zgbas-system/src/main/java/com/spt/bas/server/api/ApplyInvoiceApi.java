package com.spt.bas.server.api;

import com.spt.bas.client.entity.ApplyCancelDetail;
import com.spt.bas.client.entity.ApplyInvoice;
import com.spt.bas.client.vo.FileIdUpdateVo;
import com.spt.bas.server.service.IApplyInvoiceService;
import com.spt.bas.server.service.IApplyProductDetailService;
import com.spt.pm.entity.PmApprove;
import com.spt.pm.service.IPmApproveService;
import com.spt.tools.core.bean.PageSearchVo;
import com.spt.tools.data.service.BaseApi;
import com.spt.tools.jpa.service.IBaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;


@RestController
@RequestMapping(value = "apply/invoice")
public class ApplyInvoiceApi extends BaseApi<ApplyInvoice> {
    @Autowired
    private IApplyInvoiceService applyInvoiceService;
    @Autowired
    private IApplyProductDetailService applyProductDetailService;
    @Autowired
    private IPmApproveService pmApproveService;

    @Override
    public IBaseService<ApplyInvoice> getService() {
        return applyInvoiceService;
    }

    @PostMapping("updateFileId")
    public void updateFileId(@RequestBody FileIdUpdateVo vo) {
        applyInvoiceService.updateFileId(vo.getId(), vo.getFileId());
    }

    @PostMapping("findPageDetail")
    public Page<ApplyCancelDetail> findPageDetail(@RequestBody PageSearchVo searchVo) {
        Page<ApplyInvoice> page = applyInvoiceService.findPage(searchVo);
        List<ApplyCancelDetail> list = new ArrayList<ApplyCancelDetail>();
        for (ApplyInvoice out : page.getContent()) {
            PmApprove approve = pmApproveService.getEntity(out.getApproveId());
            ApplyCancelDetail detail = new ApplyCancelDetail();
            detail.setOldApproveId(out.getApproveId());
            if (approve != null) {
                detail.setOldApproveNo(approve.getApproveNo());
            }
            detail.setOldApproveDate(out.getCreatedDate());
            detail.setCancelAmount(out.getDealAmount());
            //作废总数量、总金额
//			List<Object[]> numList = applyProductDetailService.sumApplyDetail(out.getId(), BasConstants.APPLY_TYPE_O);
//			if(numList!=null){
//				Object[] obj = numList.get(0);
//				detail.setCancelNum(obj[0]==null?BigDecimal.ZERO:new BigDecimal(obj[0].toString()));
//				detail.setCancelAmount(obj[1]==null?BigDecimal.ZERO:new BigDecimal(obj[1].toString()));
//			}
            list.add(detail);
        }
        PageRequest pageRequest = PageRequest.of(searchVo.getPage() - 1, searchVo.getRows());
        Page<ApplyCancelDetail> pageVo = new PageImpl<>(list, pageRequest, page.getTotalElements());
        return pageVo;
    }

    @PostMapping("findByContractId")
    public List<ApplyInvoice> findByContractId(@RequestParam("contractId") Long contractId){
        return applyInvoiceService.findByContractId(contractId);
    }

    @PostMapping("autoInitiatedInvoice")
    public void autoInitiatedInvoice(@RequestBody Long contractId){
        applyInvoiceService.autoInitiatedInvoice(contractId);
    }
}

