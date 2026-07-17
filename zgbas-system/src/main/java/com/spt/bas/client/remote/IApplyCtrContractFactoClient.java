package com.spt.bas.client.remote;


import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.ApplyCreditCycle;
import com.spt.bas.client.entity.ApplyCtrContractFactor;
import com.spt.bas.client.entity.ApplyCtrDCSX;
import com.spt.bas.client.vo.ApplyCtrContractFactorVo;
import com.spt.bas.client.vo.ContractSearchVo;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.http.feign.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.Date;

@FeignClient(name = BasConstants.SERVER_NAME,path= BasConstants.SERVER_NAME+"/apply/contractFacto",url=BasConstants.SERVER_URL,configuration= FeignConfig.class)
public interface IApplyCtrContractFactoClient extends BaseClient<ApplyCtrContractFactor> {

    @PostMapping("confirm")
    ApplyCtrContractFactor confirm(@RequestParam("contractNo")  String contractNo);

    @PostMapping("updateFacto")
    void updateFacto(@RequestBody ApplyCtrContractFactorVo applyCtrContractFactor);

    @PostMapping("updateStatusByContractNo")
    void updateStatusByContractNo(@RequestParam("contractNo") String contractNo,@RequestParam("factorStatus") String factorStatus);

    @PostMapping("autoLaunchApplyPay")
    public void autoLaunchApplyPay(@RequestParam("contractNo") String contractNo);

}
