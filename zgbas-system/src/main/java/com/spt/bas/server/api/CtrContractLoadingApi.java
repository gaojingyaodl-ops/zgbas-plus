package com.spt.bas.server.api;

import com.spt.bas.client.entity.CtrContractLoading;
import com.spt.bas.client.entity.CtrContractLoadingDetail;
import com.spt.bas.server.service.ICtrContractLoadingDetailService;
import com.spt.bas.server.service.ICtrContractLoadingService;
import com.spt.pm.entity.PmApprove;
import com.spt.pm.vo.PmApproveSearchVo;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.data.service.BaseApi;
import com.spt.tools.data.vo.BatchSaveVo;
import com.spt.tools.jpa.service.IBaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Author: gaojy
 * @create 2022/3/16 9:31
 * @version: 1.0
 * @description:
 */
@RestController
@RequestMapping(value = "ctrContract/loading")
public class CtrContractLoadingApi extends BaseApi<CtrContractLoading> {
    @Autowired
    private ICtrContractLoadingService ctrContractLoadingService;
    @Autowired
    private ICtrContractLoadingDetailService ctrContractLoadingDetailService;

    @Override
    public IBaseService<CtrContractLoading> getService() {
        return ctrContractLoadingService;
    }

    /**
     * 生成电子签合同
     * @param loadingId
     * @return
     */
    @PostMapping(value = "axqLoadingBill")
    public CtrContractLoading axqLoadingBill(@RequestBody Long loadingId) throws ApplicationException {
        return ctrContractLoadingService.axqLoadingBill(loadingId);
    }

    /**
     * 刷新提货单状态
     * @param loadingId
     */
    @PostMapping(value = "refreshLoadingBillStatus")
    public CtrContractLoading refreshLoadingBillStatus(@RequestBody Long loadingId){
        return ctrContractLoadingService.refreshLoadingBillStatus(loadingId);
    }

    @PostMapping(value = "refreshLoadingBillByContractNo")
    public CtrContractLoading refreshLoadingBillByContractNo(@RequestBody String contractNo){
        return ctrContractLoadingService.refreshLoadingBillByContractNo(contractNo);
    }

    @PostMapping(value = "findSealUsageApprove")
    public List<PmApprove> findSealUsageApprove(@RequestBody PmApproveSearchVo searchVo){
        return ctrContractLoadingService.findSealUsageApprove(searchVo);
    }

    @PostMapping(value = "saveLoadingDetails")
    public void saveLoadingDetails(@RequestBody BatchSaveVo<CtrContractLoadingDetail> batchSaveVo, @RequestParam("loadingId") Long loadingId, @RequestParam("initDetailIdFlg") Boolean initDetailIdFlg) {
        ctrContractLoadingDetailService.saveLoadingDetails(batchSaveVo.getInsertedRecords(), batchSaveVo.getUpdatedRecords(), batchSaveVo.getDeletedRecords(), loadingId, initDetailIdFlg);
    }

    @PostMapping(value = "deleteLoadingDetail")
    public void deleteLoadingDetail(@RequestBody Long id) {
        ctrContractLoadingDetailService.deleteLoadingDetail(id);
    }
}
