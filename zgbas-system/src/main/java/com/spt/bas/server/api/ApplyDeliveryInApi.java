package com.spt.bas.server.api;

import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.ApplyCancelDetail;
import com.spt.bas.client.entity.ApplyDeliveryIn;
import com.spt.bas.client.entity.CtrContract;
import com.spt.bas.client.entity.StockContractRela;
import com.spt.bas.client.vo.ApplyDeliveryInVo;
import com.spt.bas.client.vo.FileIdUpdateVo;
import com.spt.bas.server.service.IApplyDeliveryInService;
import com.spt.bas.server.service.IApplyProductDetailService;
import com.spt.bas.server.service.ICtrContractService;
import com.spt.bas.server.stock.service.IStockContractRelaService;
import com.spt.pm.entity.PmApprove;
import com.spt.pm.service.IPmApproveService;
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
import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "apply/deliveryIn")
public class ApplyDeliveryInApi extends BaseApi<ApplyDeliveryIn> {
    @Autowired
    private IApplyDeliveryInService applyDeliveryInService;
    @Autowired
    private IApplyProductDetailService applyProductDetailService;
    @Autowired
    private IPmApproveService pmApproveService;
    @Autowired
    private ICtrContractService ctrContractService;
    @Autowired
    private IStockContractRelaService stockContractRelaService;

    @Override
    public IBaseService<ApplyDeliveryIn> getService() {
        return applyDeliveryInService;
    }

    @PostMapping("updateFileId")
    public void updateFileId(@RequestBody FileIdUpdateVo vo) {
        applyDeliveryInService.updateFileId(vo.getId(), vo.getFileId());
    }

    @PostMapping("findDeliveryInContractId")
    public List<ApplyDeliveryIn> findDeliveryInContractId(@RequestBody Long contractId) {
        return applyDeliveryInService.findDeliveryInContractId(contractId);
    }

    @PostMapping("findPageDetail")
    public Page<ApplyCancelDetail> findPageDetail(@RequestBody PageSearchVo searchVo) {

        Page<ApplyDeliveryIn> page = applyDeliveryInService.findPage(searchVo);
        List<ApplyCancelDetail> list = new ArrayList<ApplyCancelDetail>();
        for (ApplyDeliveryIn in : page.getContent()) {
            PmApprove approve = pmApproveService.getEntity(in.getApproveId());
            ApplyCancelDetail detail = new ApplyCancelDetail();
            detail.setOldApproveId(in.getApproveId());
            if (approve != null) {
                detail.setOldApproveNo(approve.getApproveNo());
            }
            detail.setOldApproveDate(in.getCreatedDate());
            //作废总数量、总金额
            List<Object[]> numList = applyProductDetailService.sumApplyDetail(in.getId(), BasConstants.APPLY_TYPE_I);
            if (numList != null) {
                Object[] obj = numList.get(0);
                detail.setCancelNum(obj[2] == null ? BigDecimal.ZERO : new BigDecimal(obj[2].toString()));
            }
            list.add(detail);
        }
        Map<String, Object> searchParams = searchVo.getSearchParams();
        Object contractObj = searchParams.get("EQS_contractId");
        if (list.isEmpty() && contractObj != null) {
            Long contractId = Long.parseLong(contractObj.toString());
            ApplyCancelDetail cancelDetail = new ApplyCancelDetail();
            CtrContract entity = ctrContractService.getEntity(contractId);
            cancelDetail.setCancelNum(entity.getWarehouseNumber());
            List<StockContractRela> relaList = stockContractRelaService.findByContractId(contractId,
                    BasConstants.APPLY_TYPE_I);
            if (!relaList.isEmpty()) {
                Date updatedDate = relaList.get(0).getUpdatedDate();
                cancelDetail.setOldApproveDate(updatedDate);
            }
            list.add(cancelDetail);
        }
        PageRequest pageRequest = PageRequest.of(searchVo.getPage() - 1, searchVo.getRows());
        Page<ApplyCancelDetail> pageVo = new PageImpl<>(list, pageRequest, page.getTotalElements());
        return pageVo;
    }

    @PostMapping("generateApplyNo")
    public ApplyDeliveryIn generateApplyNo(@RequestBody Long contractId) {
        return applyDeliveryInService.generateApplyNo(contractId);
    }
}

