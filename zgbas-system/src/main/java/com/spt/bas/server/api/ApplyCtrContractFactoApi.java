package com.spt.bas.server.api;


import com.spt.bas.client.entity.ApplyCtrContractFactor;
import com.spt.bas.client.entity.ApplyCtrDCSX;
import com.spt.bas.client.vo.ApplyCtrContractFactorVo;
import com.spt.bas.server.service.IApplyCreditCycleService;
import com.spt.bas.server.service.IApplyCtrContractFactoService;
import com.spt.tools.data.service.BaseApi;
import com.spt.tools.data.service.IDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Date;

@RestController
@RequestMapping(value = "apply/contractFacto")
public class ApplyCtrContractFactoApi  extends BaseApi<ApplyCtrContractFactor> {


    @Autowired
    private IApplyCtrContractFactoService applyCtrContractFactoService;

    @Override
    public IDataService<ApplyCtrContractFactor> getService() {
        return applyCtrContractFactoService;
    }

    @PostMapping("confirm")
    ApplyCtrContractFactor confirm(@RequestParam("contractNo") String contractNo){
       return applyCtrContractFactoService.findByContractNo(contractNo);
    }

    @PostMapping("updateFacto")
    public void updateFacto(@RequestBody ApplyCtrContractFactorVo applyCtrContractFactor ){
        applyCtrContractFactoService.updateFacto(applyCtrContractFactor.getStatus(), applyCtrContractFactor.getLoanAmount(), applyCtrContractFactor.getLoanDate(), applyCtrContractFactor.getContractNo());
    }
    @PostMapping("updateStatusByContractNo")
    public void updateStatusByContractNo(@RequestParam("contractNo") String contractNo,@RequestParam("factorStatus") String factorStatus){
        applyCtrContractFactoService.updateStatusByContractNo(contractNo,factorStatus);
    }

    @PostMapping("autoLaunchApplyPay")
    public void autoLaunchApplyPay(@RequestParam("contractNo") String contractNo){
        applyCtrContractFactoService.autoLaunchApplyPay(contractNo);
    }

}
