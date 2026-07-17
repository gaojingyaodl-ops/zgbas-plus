package com.spt.bas.client.remote;

import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.CtrContractLoading;
import com.spt.bas.client.entity.CtrContractLoadingDetail;
import com.spt.pm.entity.PmApprove;
import com.spt.pm.vo.PmApproveSearchVo;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.data.vo.BatchSaveVo;
import com.spt.tools.http.feign.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @Author: gaojy
 * @create 2022/3/16 9:33
 * @version: 1.0
 * @description:
 */
@FeignClient(name = BasConstants.SERVER_NAME, path = BasConstants.SERVER_NAME + "/ctrContract/loading", url = BasConstants.SERVER_URL, configuration = FeignConfig.class)
public interface ICtrContractLoadingClient extends BaseClient<CtrContractLoading> {

    /**
     * 生成电子签合同
     * @param loadingId
     * @return
     */
    @PostMapping(value = "axqLoadingBill")
    CtrContractLoading axqLoadingBill(@RequestBody Long loadingId) throws ApplicationException;

    /**
     * 刷新提货单状态
     * @param loadingId
     */
    @PostMapping(value = "refreshLoadingBillStatus")
    CtrContractLoading refreshLoadingBillStatus(@RequestBody Long loadingId);

    @PostMapping(value = "refreshLoadingBillByContractNo")
    CtrContractLoading refreshLoadingBillByContractNo(@RequestBody String contractNo);

    @PostMapping(value = "findSealUsageApprove")
    List<PmApprove> findSealUsageApprove(@RequestBody PmApproveSearchVo searchVo);

    @PostMapping(value = "saveLoadingDetails")
    void saveLoadingDetails(@RequestBody BatchSaveVo<CtrContractLoadingDetail> loadingDetailSaveVo, @RequestParam("loadingId") Long loadingId, @RequestParam("initDetailIdFlg") Boolean initDetailIdFlg);

    @PostMapping(value = "deleteLoadingDetail")
    void deleteLoadingDetail(@RequestBody Long id);
}
