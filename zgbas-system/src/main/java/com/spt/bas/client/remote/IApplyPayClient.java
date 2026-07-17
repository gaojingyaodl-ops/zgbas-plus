package com.spt.bas.client.remote;

import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.ApplyCancelDetail;
import com.spt.bas.client.entity.ApplyPay;
import com.spt.bas.client.vo.FileIdUpdateVo;
import com.spt.pm.entity.PmApprove;
import com.spt.pm.vo.PmApproveSaveVo;
import com.spt.tools.core.bean.PageSearchVo;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.data.vo.PageDown;
import com.spt.tools.http.feign.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.math.BigDecimal;


@FeignClient(qualifier = "applyPayClient", name = BasConstants.SERVER_NAME, path = BasConstants.SERVER_NAME + "/apply/pay", url = BasConstants.SERVER_URL, configuration = FeignConfig.class)
public interface IApplyPayClient extends BaseClient<ApplyPay> {

    @PostMapping("updateFileId")
    void updateFileId(@RequestBody FileIdUpdateVo vo);

    @PostMapping("findPageSum")
    ApplyPay findPageSum(@RequestBody PageSearchVo searchVo);

    @PostMapping("findPageDetail")
    PageDown<ApplyCancelDetail> findPageDetail(@RequestBody PageSearchVo searchVo);

    @PostMapping("findApplyPayByContractNo")
    ApplyPay findApplyPayByContractNo(String contractNo);

    @PostMapping("startBatchPayApply")
    PmApprove startBatchPayApply(@RequestBody PmApproveSaveVo saveVo) throws ApplicationException;

    @PostMapping("getBrushBrushAmount")
    ApplyPay getBrushBrushAmount(@RequestBody ApplyPay applyPay);
}

